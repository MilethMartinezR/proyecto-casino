class BetSagaStep {
  constructor({ name, execute, compensate }) {
    this.name = name;
    this.execute = execute;
    this.compensate = compensate ?? (() => Promise.resolve());
  }
}

module.exports = { BetSagaStep };
