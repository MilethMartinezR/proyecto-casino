const { Router } = require('express');
const { authMiddleware } = require('../middleware/auth.middleware');

function createGameRouter(controller) {
  const router = Router();

  router.use(authMiddleware);

  router.get('/history/:userId', (req, res, next) => controller.history(req, res, next));
  router.post('/start', (req, res, next) => controller.start(req, res, next));
  router.post('/:partidaId/hit', (req, res, next) => controller.hit(req, res, next));
  router.post('/:partidaId/stand', (req, res, next) => controller.stand(req, res, next));
  router.get('/:partidaId/state', (req, res, next) => controller.state(req, res, next));
  router.post('/:partidaId/abandon', (req, res, next) => controller.abandon(req, res, next));

  return router;
}

module.exports = { createGameRouter };
