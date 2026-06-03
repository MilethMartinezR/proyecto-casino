require('dotenv').config();

const { connectMySQL } = require('./infrastructure/config/mysql');
const { connectMongoDB } = require('./infrastructure/config/mongodb');
const { connectKafka } = require('./infrastructure/config/kafka');
const { registerEureka } = require('./infrastructure/config/eureka');
const { createApp } = require('./app');

const PORT = parseInt(process.env.PORT ?? '8083');

async function bootstrap() {
  await connectMySQL();
  await connectMongoDB();
  await connectKafka();

  const app = createApp();
  app.listen(PORT, () => console.log(`[game-service] Puerto ${PORT}`));

  registerEureka(PORT);
}

bootstrap().catch((err) => {
  console.error('[game-service] Error al iniciar:', err.message);
  process.exit(1);
});
