const { GameNotFoundException } = require('../../domain/exception/gameNotFoundException');
const { GameStateDto } = require('../dto/gameDto');

class GetGameStateUseCaseImpl {
  constructor(gameRepository) {
    this.gameRepository = gameRepository;
  }

  async execute(partidaId, userId) {
    const game = await this.gameRepository.findById(partidaId);
    if (!game) throw new GameNotFoundException(`Partida ${partidaId} no encontrada`);
    if (game.userId !== userId) throw Object.assign(new Error('No autorizado'), { status: 403 });
    return new GameStateDto(game);
  }
}

module.exports = { GetGameStateUseCaseImpl };
