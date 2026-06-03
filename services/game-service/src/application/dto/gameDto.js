class GameStateDto {
  constructor(game) {
    const pub = game.toPublicState();
    this.id = pub.id;
    this.userId = pub.userId;
    this.estado = pub.estado;
    this.manoJugador = pub.manoJugador;
    this.manoDealer = pub.manoDealer;
    this.puntosJugador = pub.puntosJugador;
    this.puntosDealer = pub.puntosDealer;
    this.apuesta = pub.apuesta;
    this.creadaEn = pub.creadaEn;
    this.actualizadaEn = pub.actualizadaEn;
  }
}

module.exports = { GameStateDto };
