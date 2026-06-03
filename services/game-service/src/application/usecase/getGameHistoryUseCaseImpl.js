const { GetGameHistoryUseCase } = require('../../domain/port/in/getGameHistoryUseCase');

class GetGameHistoryUseCaseImpl extends GetGameHistoryUseCase {
  constructor(gameRepository) {
    super();
    this.gameRepository = gameRepository;
  }

  async execute(userId, limit = 10) {
    return this.gameRepository.getHistory(userId, limit);
  }
}

module.exports = { GetGameHistoryUseCaseImpl };
