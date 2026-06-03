const { AbandonGameUseCase } = require('../../domain/port/in/abandonGameUseCase');
const { GameNotFoundException } = require('../../domain/exception/gameNotFoundException');
const { GameStateDto } = require('../dto/gameDto');
const { buildHistorySummary } = require('./blackjackLogicService');
const { TOPICS, gameAbandoned } = require('../dto/gameEvents');

class AbandonGameUseCaseImpl extends AbandonGameUseCase {
  constructor(gameRepository, eventPublisher) {
    super();
    this.gameRepository = gameRepository;
    this.eventPublisher = eventPublisher;
  }

  async execute(partidaId, userId) {
    const game = await this.gameRepository.findById(partidaId);
    if (!game) throw new GameNotFoundException(`Partida ${partidaId} no encontrada`);
    if (game.userId !== userId) throw Object.assign(new Error('No autorizado'), { status: 403 });

    game.abandon();
    await this.gameRepository.save(game);
    await this.gameRepository.addToHistory(userId, buildHistorySummary(game));

    // pago = 0 → wallet no hace nada, audit registra el abandono
    await this.eventPublisher.publish(TOPICS.GAME_EVENTS, gameAbandoned(game));

    return new GameStateDto(game);
  }
}

module.exports = { AbandonGameUseCaseImpl };
