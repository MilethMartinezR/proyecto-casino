const { ESTADOS } = require('../../domain/model/game');

function buildHistorySummary(game) {
  const pub = game.toPublicState();
  return {
    id: pub.id,
    estado: pub.estado,
    apuesta: pub.apuesta,
    pago: game.calcularPago(),
    manoJugador: game.manoJugador.toArray(),
    manoDealer: game.manoDealer.toArray(),
    puntosJugador: game.manoJugador.calcularPuntos(),
    puntosDealer: game.manoDealer.calcularPuntos(),
    creadaEn: pub.creadaEn,
  };
}

function esFinalizado(estado) {
  return estado !== ESTADOS.ACTIVA;
}

module.exports = { buildHistorySummary, esFinalizado };
