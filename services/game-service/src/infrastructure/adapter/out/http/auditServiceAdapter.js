const axios = require('axios');
const { AuditServicePort } = require('../../../../domain/port/out/auditServicePort');

class AuditServiceAdapter extends AuditServicePort {
  constructor() {
    super();
    this.client = axios.create({
      baseURL: process.env.AUDIT_SERVICE_URL ?? 'http://localhost:8084',
      timeout: 3000,
    });
  }

  async log(userId, tipo, datos) {
    try {
      await this.client.post('/audit/log', {
        userId,
        tipo,
        servicio: 'game-service',
        datos,
        timestamp: new Date().toISOString(),
      });
    } catch (err) {
      console.warn('[AuditAdapter] No se pudo registrar evento:', err.message);
    }
  }
}

module.exports = { AuditServiceAdapter };
