const { v4: uuidv4 } = require('uuid');
const Deck = require('./deck');
const Hand = require('./hand');
const Bet = require('./bet');

const ESTADOS = {
  ACTIVA: 'ACTIVA',
  BLACKJACK_JUGADOR: 'BLACKJACK_JUGADOR',
  BLACKJACK_DEALER: 'BLACKJACK_DEALER',
  JUGADOR_GANO: 'JUGADOR_GANO',
  DEALER_GANO: 'DEALER_GANO',
  EMPATE: 'EMPATE',
  JUGADOR_SE_PASO: 'JUGADOR_SE_PASO',
  DEALER_SE_PASO: 'DEALER_SE_PASO',
  ABANDONADA: 'ABANDONADA',
};

class Game {
  constructor({ id, userId, estado, mazo, manoJugador, manoDealer, apuesta, creadaEn, actualizadaEn }) {
    this.id = id;
    this.userId = userId;
    this.estado = estado;
    this.mazo = mazo instanceof Deck ? mazo : new Deck(mazo);
    this.manoJugador = manoJugador instanceof Hand ? manoJugador : new Hand(manoJugador);
    this.manoDealer = manoDealer instanceof Hand ? manoDealer : new Hand(manoDealer);
    this.apuesta = apuesta instanceof Bet ? apuesta : new Bet(apuesta);
    this.creadaEn = creadaEn ?? new Date().toISOString();
    this.actualizadaEn = actualizadaEn ?? new Date().toISOString();
  }

  static iniciar(userId, apuestaMonto) {
    const apuesta = new Bet(apuestaMonto);
    let deck = Deck.crear().mezclar();

    let carta1J, carta2J, carta1D, carta2D;
    ({ carta: carta1J, deck } = deck.sacarCarta());
    ({ carta: carta1D, deck } = deck.sacarCarta());
    ({ carta: carta2J, deck } = deck.sacarCarta());
    ({ carta: carta2D, deck } = deck.sacarCarta());

    const manoJugador = new Hand([carta1J, carta2J]);
    const manoDealer = new Hand([carta1D, carta2D]);

    let estado = ESTADOS.ACTIVA;
    const jugadorBJ = manoJugador.esBlackjack();
    const dealerBJ = manoDealer.esBlackjack();

    if (jugadorBJ && dealerBJ) estado = ESTADOS.EMPATE;
    else if (jugadorBJ) estado = ESTADOS.BLACKJACK_JUGADOR;
    else if (dealerBJ) estado = ESTADOS.BLACKJACK_DEALER;

    return new Game({
      id: uuidv4(),
      userId,
      estado,
      mazo: deck,
      manoJugador,
      manoDealer,
      apuesta,
      creadaEn: new Date().toISOString(),
      actualizadaEn: new Date().toISOString(),
    });
  }

  hit() {
    this._validarEstadoActivo();
    let { carta, deck } = this.mazo.sacarCarta();
    this.mazo = deck;
    this.manoJugador = this.manoJugador.agregar(carta);
    if (this.manoJugador.esBust()) {
      this.estado = ESTADOS.JUGADOR_SE_PASO;
    }
    this.actualizadaEn = new Date().toISOString();
  }

  stand() {
    this._validarEstadoActivo();
    let deck = this.mazo;
    let manoDealer = this.manoDealer;

    while (manoDealer.calcularPuntos() < 17) {
      let carta;
      ({ carta, deck } = deck.sacarCarta());
      manoDealer = manoDealer.agregar(carta);
    }

    this.mazo = deck;
    this.manoDealer = manoDealer;

    const puntosJugador = this.manoJugador.calcularPuntos();
    const puntosDealer = this.manoDealer.calcularPuntos();

    if (this.manoDealer.esBust()) {
      this.estado = ESTADOS.DEALER_SE_PASO;
    } else if (puntosJugador > puntosDealer) {
      this.estado = ESTADOS.JUGADOR_GANO;
    } else if (puntosDealer > puntosJugador) {
      this.estado = ESTADOS.DEALER_GANO;
    } else {
      this.estado = ESTADOS.EMPATE;
    }

    this.actualizadaEn = new Date().toISOString();
  }

  abandon() {
    this._validarEstadoActivo();
    this.estado = ESTADOS.ABANDONADA;
    this.actualizadaEn = new Date().toISOString();
  }

  calcularPago() {
    return this.apuesta.calcularPago(this.estado);
  }

  estaFinalizado() {
    return this.estado !== ESTADOS.ACTIVA;
  }

  toPublicState() {
    const manoDealer = this.estado === ESTADOS.ACTIVA
      ? this.manoDealer.ocultarPrimera()
      : this.manoDealer;

    return {
      id: this.id,
      userId: this.userId,
      estado: this.estado,
      manoJugador: this.manoJugador.toArray(),
      manoDealer: manoDealer.toArray(),
      puntosJugador: this.manoJugador.calcularPuntos(),
      puntosDealer: manoDealer.calcularPuntos(),
      apuesta: this.apuesta.monto,
      creadaEn: this.creadaEn,
      actualizadaEn: this.actualizadaEn,
    };
  }

  toPlain() {
    return {
      id: this.id,
      userId: this.userId,
      estado: this.estado,
      mazo: this.mazo.toArray(),
      manoJugador: this.manoJugador.toArray(),
      manoDealer: this.manoDealer.toArray(),
      apuesta: this.apuesta.monto,
      creadaEn: this.creadaEn,
      actualizadaEn: this.actualizadaEn,
    };
  }

  _validarEstadoActivo() {
    if (this.estado !== ESTADOS.ACTIVA) {
      const { InvalidGameStateException } = require('../exception/invalidGameStateException');
      throw new InvalidGameStateException(`La partida ${this.id} no está activa (estado: ${this.estado})`);
    }
  }
}

module.exports = { Game, ESTADOS };
