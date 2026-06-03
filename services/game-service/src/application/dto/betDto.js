class BetDto {
  constructor({ userId, apuesta }) {
    if (!userId) throw Object.assign(new Error('userId requerido'), { status: 400 });
    if (!apuesta || apuesta <= 0) throw Object.assign(new Error('apuesta debe ser mayor a 0'), { status: 400 });
    this.userId = userId;
    this.apuesta = Number(apuesta);
  }
}

module.exports = { BetDto };
