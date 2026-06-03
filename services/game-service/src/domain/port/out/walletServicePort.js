class WalletServicePort {
  async cobrarApuesta(userId, monto) { throw new Error('Not implemented'); }
  async acreditarGanancia(userId, monto) { throw new Error('Not implemented'); }
}

module.exports = { WalletServicePort };
