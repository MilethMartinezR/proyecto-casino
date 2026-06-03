const { GameRepositoryPort } = require('../../../../domain/port/out/gameRepositoryPort');
const { Game } = require('../../../../domain/model/game');
const { getMySQL } = require('../../../config/mysql');
const { getMongoDB } = require('../../../config/mongodb');

class GameRepositoryAdapter extends GameRepositoryPort {

  // ── Write side: MySQL ────────────────────────────────────────────────────

  async save(game) {
    const pool = getMySQL();
    const p = game.toPlain();
    await pool.query(
      `INSERT INTO games (id, user_id, estado, mazo, mano_jugador, mano_dealer, apuesta)
       VALUES (?, ?, ?, ?, ?, ?, ?)
       ON DUPLICATE KEY UPDATE
         estado       = VALUES(estado),
         mazo         = VALUES(mazo),
         mano_jugador = VALUES(mano_jugador),
         mano_dealer  = VALUES(mano_dealer),
         updated_at   = CURRENT_TIMESTAMP`,
      [p.id, p.userId, p.estado,
       JSON.stringify(p.mazo), JSON.stringify(p.manoJugador), JSON.stringify(p.manoDealer),
       p.apuesta]
    );
  }

  async findById(id) {
    const pool = getMySQL();
    const [rows] = await pool.query('SELECT * FROM games WHERE id = ?', [id]);
    if (!rows.length) return null;
    const r = rows[0];
    return new Game({
      id:          r.id,
      userId:      r.user_id,
      estado:      r.estado,
      mazo:        r.mazo,
      manoJugador: r.mano_jugador,
      manoDealer:  r.mano_dealer,
      apuesta:     parseFloat(r.apuesta),
      creadaEn:    r.created_at.toISOString(),
      actualizadaEn: r.updated_at.toISOString(),
    });
  }

  // ── Read side: MongoDB ───────────────────────────────────────────────────

  async addToHistory(userId, summary) {
    const db = getMongoDB();
    await db.collection('game_history').insertOne({ ...summary, userId });
  }

  async getHistory(userId, limit = 10) {
    const db = getMongoDB();
    return db.collection('game_history')
      .find({ userId }, { projection: { _id: 0 } })
      .sort({ creadaEn: -1 })
      .limit(limit)
      .toArray();
  }
}

module.exports = { GameRepositoryAdapter };
