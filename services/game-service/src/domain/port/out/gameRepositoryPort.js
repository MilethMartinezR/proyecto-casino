class GameRepositoryPort {
  async save(game) { throw new Error('Not implemented'); }
  async findById(id) { throw new Error('Not implemented'); }
  async addToHistory(userId, gameSummary) { throw new Error('Not implemented'); }
  async getHistory(userId, limit) { throw new Error('Not implemented'); }
}

module.exports = { GameRepositoryPort };
