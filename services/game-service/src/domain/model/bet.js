class Bet {
  constructor(monto) {
    if (!monto || monto <= 0) throw new Error('La apuesta debe ser mayor a 0');
    this.monto = monto;
  }

  calcularPago(estado) {
    const ESTADOS = require('./game').ESTADOS;
    switch (estado) {
      case ESTADOS.BLACKJACK_JUGADOR:
        return this.monto * 2.5;
      case ESTADOS.JUGADOR_GANO:
      case ESTADOS.DEALER_SE_PASO:
        return this.monto * 2;
      case ESTADOS.EMPATE:
        return this.monto;
      default:
        return 0;
    }
  }
}

module.exports = Bet;
