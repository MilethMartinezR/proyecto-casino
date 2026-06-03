class InvalidGameStateException extends Error {
  constructor(message) {
    super(message);
    this.name = 'InvalidGameStateException';
    this.status = 409;
  }
}

module.exports = { InvalidGameStateException };
