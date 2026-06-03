const mysql = require('mysql2/promise');

let pool = null;

async function connectMySQL() {
  pool = mysql.createPool({
    host:     process.env.MYSQL_HOST     ?? 'mysql',
    port:     parseInt(process.env.MYSQL_PORT ?? '3306'),
    user:     process.env.MYSQL_USER     ?? 'casino_user',
    password: process.env.MYSQL_PASSWORD ?? 'casino_pass',
    database: process.env.MYSQL_DATABASE ?? 'casino_db',
    waitForConnections: true,
    connectionLimit: 10,
    timezone: 'Z',
  });
  await pool.query('SELECT 1');
  console.log('[MySQL] Conectado');
  return pool;
}

function getMySQL() {
  if (!pool) throw new Error('MySQL no inicializado');
  return pool;
}

module.exports = { connectMySQL, getMySQL };
