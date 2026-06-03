const { StartGameUseCase } = require('../../domain/port/in/startGameUseCase');
const { Game } = require('../../domain/model/game');
const { GameStateDto } = require('../dto/gameDto');
const { buildHistorySummary } = require('./blackjackLogicService');
const { TOPICS, gameStarted, gameFinished } = require('../dto/gameEvents');

class StartGameUseCaseImpl extends StartGameUseCase {
  constructor(gameRepository, walletService, eventPublisher) {
    super();
    this.gameRepository = gameRepository;
    this.walletService = walletService;
    this.eventPublisher = eventPublisher;
  }

  async execute(userId, apuestaMonto) {
    // Cobro síncrono: el jugador necesita saber si tiene fondos antes de empezar
    await this.walletService.cobrarApuesta(userId, apuestaMonto);

    const game = Game.iniciar(userId, apuestaMonto);
    await this.gameRepository.save(game);

    if (game.estaFinalizado()) {
      // Blackjack inmediato — wallet y audit se enteran por evento
      await this.gameRepository.addToHistory(userId, buildHistorySummary(game));
      await this.eventPublisher.publish(TOPICS.GAME_EVENTS, gameFinished(game));
    } else {
      await this.eventPublisher.publish(TOPICS.GAME_EVENTS, gameStarted(game));
    }

    return new GameStateDto(game);
  }
}

module.exports = { StartGameUseCaseImpl };
