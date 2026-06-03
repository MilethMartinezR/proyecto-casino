const axios = require('axios');
const { WalletServicePort } = require('../../../../domain/port/out/walletServicePort');
const { InsufficientFundsException } = require('../../../../domain/exception/insufficientFundsException');

// Solo se usa para cobrar la apuesta (síncrono).
// La acreditación de ganancias la maneja wallet-service al consumir GAME_FINISHED en Kafka.
class WalletServiceAdapter extends WalletServicePort {
  constructor() {
    super();
    const internalKey = process.env.INTERNAL_SERVICE_KEY ?? 'casino_internal_service_key_2025';
    this.client = axios.create({
      baseURL: process.env.WALLET_SERVICE_URL ?? 'http://localhost:8082',
      timeout: 5000,
      headers: {
        'X-Service-Token': internalKey,
      },
    });
  }

  async cobrarApuesta(userId, monto) {
    try {
      await this.client.post('/wallet/cobrar-apuesta', {
        usuario_id: userId,
        monto_creditos: monto,
        descripcion: `Apuesta blackjack: ${monto} créditos`,
      }, {
        headers: { 'X-User-Id': userId },
      });
    } catch (err) {
      if (err.response?.status === 400 || err.response?.status === 422) {
        throw new InsufficientFundsException('Saldo insuficiente para realizar la apuesta');
      }
      throw err;
    }
  }
}

module.exports = { WalletServiceAdapter };
