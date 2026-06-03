const jwt = require('jsonwebtoken');

function authMiddleware(req, res, next) {
  const userIdHeader = req.headers['x-user-id'];
  if (userIdHeader) {
    req.userId = userIdHeader;
    return next();
  }

  const authHeader = req.headers['authorization'];
  if (authHeader?.startsWith('Bearer ')) {
    const token = authHeader.slice(7);
    try {
      const payload = jwt.verify(token, process.env.JWT_SECRET ?? 'default_secret');
      req.userId = payload.sub ?? payload.userId ?? payload.id;
      return next();
    } catch {
      return res.status(401).json({ error: 'Token inválido o expirado' });
    }
  }

  return res.status(401).json({ error: 'No autenticado' });
}

module.exports = { authMiddleware };
