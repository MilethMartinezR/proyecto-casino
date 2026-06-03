const TOPICS = {
  GAME_EVENTS: 'game-events',
};

const EVENT_TYPES = {
  GAME_STARTED:   'GAME_STARTED',
  GAME_HIT:       'GAME_HIT',
  GAME_BUST:      'GAME_BUST',
  GAME_FINISHED:  'GAME_FINISHED',   // stand → wallet acredita, audit registra
  GAME_ABANDONED: 'GAME_ABANDONED',  // audit registra, wallet no hace nada
};

function gameStarted(game) {
  return {
    eventType: EVENT_TYPES.GAME_STARTED,
    gameId:    game.id,
    userId:    game.userId,
    apuesta:   game.apuesta.monto,
    estado:    game.estado,
    timestamp: new Date().toISOString(),
  };
}

function gameHit(game) {
  return {
    eventType: EVENT_TYPES.GAME_HIT,
    gameId:    game.id,
    userId:    game.userId,
    estado:    game.estado,
    timestamp: new Date().toISOString(),
  };
}

function gameBust(game) {
  return {
    eventType: EVENT_TYPES.GAME_BUST,
    gameId:    game.id,
    userId:    game.userId,
    apuesta:   game.apuesta.monto,
    pago:      0,
    estado:    game.estado,
    timestamp: new Date().toISOString(),
  };
}

function gameFinished(game) {
  const pub = game.toPublicState();
  return {
    eventType:    EVENT_TYPES.GAME_FINISHED,
    gameId:       game.id,
    userId:       game.userId,
    estado:       game.estado,
    apuesta:      pub.apuesta,
    pago:         game.calcularPago(),
    manoJugador:  pub.manoJugador,
    manoDealer:   pub.manoDealer,
    puntosJugador: pub.puntosJugador,
    puntosDealer:  pub.puntosDealer,
    timestamp:    new Date().toISOString(),
  };
}

function gameAbandoned(game) {
  return {
    eventType: EVENT_TYPES.GAME_ABANDONED,
    gameId:    game.id,
    userId:    game.userId,
    apuesta:   game.apuesta.monto,
    pago:      0,
    estado:    game.estado,
    timestamp: new Date().toISOString(),
  };
}

module.exports = { TOPICS, EVENT_TYPES, gameStarted, gameHit, gameBust, gameFinished, gameAbandoned };
