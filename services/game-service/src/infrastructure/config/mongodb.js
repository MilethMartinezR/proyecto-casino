const { MongoClient } = require('mongodb');

let db = null;

async function connectMongoDB() {
  const uri = process.env.MONGO_URI
    ?? 'mongodb://casino_admin:casino_secret@mongodb:27017/casino_db?authSource=admin';
  const client = new MongoClient(uri);
  await client.connect();
  db = client.db(process.env.MONGO_DATABASE ?? 'casino_db');
  console.log('[MongoDB] Conectado');
  return db;
}

function getMongoDB() {
  if (!db) throw new Error('MongoDB no inicializado');
  return db;
}

module.exports = { connectMongoDB, getMongoDB };
