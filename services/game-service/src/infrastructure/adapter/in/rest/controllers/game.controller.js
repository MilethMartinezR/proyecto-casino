class GameController {
  constructor(startGame, hitGame, standGame, abandonGame, getHistory, getGameState) {
    this.startGame = startGame;
    this.hitGame = hitGame;
    this.standGame = standGame;
    this.abandonGame = abandonGame;
    this.getHistory = getHistory;
    this.getGameState = getGameState;
  }

  async start(req, res, next) {
    try {
      const userId = req.userId;
      const { apuesta } = req.body;
      if (!apuesta || apuesta <= 0) return res.status(400).json({ error: 'apuesta inválida' });
      const result = await this.startGame.execute(userId, Number(apuesta));
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async hit(req, res, next) {
    try {
      const result = await this.hitGame.execute(req.params.partidaId, req.userId);
      res.json(result);
    } catch (err) { next(err); }
  }

  async stand(req, res, next) {
    try {
      const result = await this.standGame.execute(req.params.partidaId, req.userId);
      res.json(result);
    } catch (err) { next(err); }
  }

  async state(req, res, next) {
    try {
      const result = await this.getGameState.execute(req.params.partidaId, req.userId);
      res.json(result);
    } catch (err) { next(err); }
  }

  async abandon(req, res, next) {
    try {
      const result = await this.abandonGame.execute(req.params.partidaId, req.userId);
      res.json(result);
    } catch (err) { next(err); }
  }

  async history(req, res, next) {
    try {
      const limit = parseInt(req.query.limit ?? '10');
      const result = await this.getHistory.execute(req.params.userId, limit);
      res.json(result);
    } catch (err) { next(err); }
  }
}

module.exports = { GameController };
