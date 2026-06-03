const express = require('express');
const cors = require('cors');
const morgan = require('morgan');

const { buildContainer } = require('./infrastructure/config/container');
const { createGameRouter } = require('./infrastructure/adapter/in/rest/routes/game.routes');
const { errorMiddleware } = require('./infrastructure/adapter/in/rest/middleware/error.middleware');

function createApp() {
  const app = express();
  const { controller } = buildContainer();

  app.use(cors());
  app.use(morgan('dev'));
  app.use(express.json());

  app.get('/health', (_, res) => res.json({ status: 'UP' }));
  app.get('/actuator/health', (_, res) => res.json({ status: 'UP' }));

  app.use('/game', createGameRouter(controller));

  app.use(errorMiddleware);

  return app;
}

module.exports = { createApp };
