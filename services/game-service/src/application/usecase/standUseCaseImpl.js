const { StandUseCase } = require('../../domain/port/in/standUseCase');
const { GameNotFoundException } = require('../../domain/exception/gameNotFoundException');
const { GameStateDto } = require('../dto/gameDto');
const { buildHistorySummary } = require('./blackjackLogicService');
const { TOPICS, gameFinished } = require('../dto/gameEvents');

class StandUseCaseImpl extends StandUseCase {
  constructor(gameRepository, eventPublisher) {
    super();
    this.gameRepository = gameRepository;
    this.eventPublisher = eventPublisher;
  }

  async execute(partidaId, userId) {
    const game = await this.gameRepository.findById(partidaId);
    if (!game) throw new GameNotFoundException(`Partida ${partidaId} no encontrada`);
    if (game.userId !== userId) throw Object.assign(new Error('No autorizado'), { status: 403 });

    game.stand();
    await this.gameRepository.save(game);
    await this.gameRepository.addToHistory(userId, buildHistorySummary(game));

    // wallet-service acredita la ganancia al consumir este evento
    // audit-service registra el resultado al consumir este evento
    await this.eventPublisher.publish(TOPICS.GAME_EVENTS, gameFinished(game));

    return new GameStateDto(game);
  }
}

module.exports = { StandUseCaseImpl };
