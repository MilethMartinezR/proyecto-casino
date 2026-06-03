const { Eureka } = require('eureka-js-client');

function registerEureka(port) {
  const client = new Eureka({
    instance: {
      app: 'game-service',
      hostName: process.env.HOSTNAME ?? 'localhost',
      ipAddr: '127.0.0.1',
      statusPageUrl: `http://localhost:${port}/actuator/info`,
      healthCheckUrl: `http://localhost:${port}/actuator/health`,
      port: { $: port, '@enabled': true },
      vipAddress: 'game-service',
      dataCenterInfo: { '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo', name: 'MyOwn' },
    },
    eureka: {
      host: process.env.EUREKA_HOST ?? 'localhost',
      port: parseInt(process.env.EUREKA_PORT ?? '8761'),
      servicePath: '/eureka/apps/',
      maxRetries: 3,
      requestRetryDelay: 2000,
    },
  });

  client.start((err) => {
    if (err) console.warn('[Eureka] No se pudo registrar:', err.message);
    else console.log('[Eureka] Registrado correctamente');
  });

  return client;
}

module.exports = { registerEureka };
