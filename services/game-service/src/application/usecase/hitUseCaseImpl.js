const { HitUseCase } = require('../../domain/port/in/hitUseCase');
const { GameNotFoundException } = require('../../domain/exception/gameNotFoundException');
const { GameStateDto } = require('../dto/gameDto');
const { buildHistorySummary } = require('./blackjackLogicService');
const { TOPICS, gameHit, gameBust } = require('../dto/gameEvents');

class HitUseCaseImpl extends HitUseCase {
  constructor(gameRepository, eventPublisher) {
    super();
    this.gameRepository = gameRepository;
    this.eventPublisher = eventPublisher;
  }

  async execute(partidaId, userId) {
    const game = await this.gameRepository.findById(partidaId);
    if (!game) throw new GameNotFoundException(`Partida ${partidaId} no encontrada`);
    if (game.userId !== userId) throw Object.assign(new Error('No autorizado'), { status: 403 });

    game.hit();
    await this.gameRepository.save(game);

    if (game.estaFinalizado()) {
      await this.gameRepository.addToHistory(userId, buildHistorySummary(game));
      await this.eventPublisher.publish(TOPICS.GAME_EVENTS, gameBust(game));
    } else {
      await this.eventPublisher.publish(TOPICS.GAME_EVENTS, gameHit(game));
    }

    return new GameStateDto(game);
  }
}

module.exports = { HitUseCaseImpl };
