class GameNotFoundException extends Error {
  constructor(message) {
    super(message);
    this.name = 'GameNotFoundException';
    this.status = 404;
  }
}

module.exports = { GameNotFoundException };
