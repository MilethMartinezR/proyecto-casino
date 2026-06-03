const Redis = require('ioredis');

let client = null;

async function connectRedis() {
  client = new Redis({
    host: process.env.REDIS_HOST ?? 'localhost',
    port: parseInt(process.env.REDIS_PORT ?? '6379'),
    password: process.env.REDIS_PASSWORD ?? undefined,
    retryStrategy: (times) => Math.min(times * 200, 5000),
  });

  await new Promise((resolve, reject) => {
    client.once('ready', resolve);
    client.once('error', reject);
  });

  console.log('[Redis] Conectado');
  return client;
}

function getRedis() {
  if (!client) throw new Error('Redis no inicializado');
  return client;
}

module.exports = { connectRedis, getRedis };
