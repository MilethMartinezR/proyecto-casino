class InsufficientFundsException extends Error {
  constructor(message) {
    super(message);
    this.name = 'InsufficientFundsException';
    this.status = 402;
  }
}

module.exports = { InsufficientFundsException };
