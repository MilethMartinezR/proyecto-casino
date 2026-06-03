function errorMiddleware(err, req, res, next) {
  const status = err.status ?? err.statusCode ?? 500;
  if (status >= 500) console.error('[Error]', err);
  res.status(status).json({ error: err.message ?? 'Error interno del servidor' });
}

module.exports = { errorMiddleware };
