-- =============================================
-- CQRS Write Side — MySQL Schema
-- =============================================

-- ── Auth Service ──────────────────────────────
CREATE TABLE IF NOT EXISTS users (
  id            VARCHAR(36)  PRIMARY KEY,
  username      VARCHAR(100) UNIQUE NOT NULL,
  email         VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('PLAYER','ADMIN') DEFAULT 'PLAYER',
  active        BOOLEAN DEFAULT TRUE,
  mfa_enabled   BOOLEAN DEFAULT FALSE,
  mfa_secret    VARCHAR(64) DEFAULT NULL,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ── Wallet Service ────────────────────────────
CREATE TABLE IF NOT EXISTS wallets (
  wallet_id          VARCHAR(36)   PRIMARY KEY,
  usuario_id         VARCHAR(36)   NOT NULL UNIQUE,
  saldo_creditos     DOUBLE        NOT NULL DEFAULT 0.0,
  tasa_cambio        DOUBLE        NOT NULL DEFAULT 1000.0,
  fecha_actualizacion DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_wallets_usuario (usuario_id)
);

CREATE TABLE IF NOT EXISTS transacciones (
  transaccion_id VARCHAR(36)  PRIMARY KEY,
  wallet_id      VARCHAR(36)  NOT NULL,
  tipo           ENUM('DEPOSITO','RETIRO','APUESTA','GANANCIA') NOT NULL,
  monto          DOUBLE       NOT NULL,
  monto_creditos DOUBLE       NOT NULL,
  estado         ENUM('PENDIENTE','COMPLETADA','FALLIDA') DEFAULT 'PENDIENTE',
  fecha          DATETIME     DEFAULT CURRENT_TIMESTAMP,
  descripcion    VARCHAR(255),
  FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id),
  INDEX idx_tx_wallet (wallet_id)
);

CREATE TABLE IF NOT EXISTS solicitudes_retiro (
  solicitud_id    VARCHAR(36) PRIMARY KEY,
  transaccion_id  VARCHAR(36) NOT NULL,
  cuenta_destino  VARCHAR(100) NOT NULL,
  estado          ENUM('PENDIENTE','COMPLETADA','FALLIDA') DEFAULT 'PENDIENTE',
  fecha_solicitud DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_ejecucion DATETIME,
  FOREIGN KEY (transaccion_id) REFERENCES transacciones(transaccion_id)
);

-- ── Game Service ──────────────────────────────
CREATE TABLE IF NOT EXISTS games (
  id           VARCHAR(36)   PRIMARY KEY,
  user_id      VARCHAR(36)   NOT NULL,
  estado       VARCHAR(50)   NOT NULL,
  mazo         JSON          NOT NULL,
  mano_jugador JSON          NOT NULL,
  mano_dealer  JSON          NOT NULL,
  apuesta      DECIMAL(15,2) NOT NULL,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_games_user (user_id),
  INDEX idx_games_estado (estado)
);

-- ── Admin Service ─────────────────────────────
-- Snapshot de usuarios recibido vía auth-events
CREATE TABLE IF NOT EXISTS usuarios_snapshot (
  usuario_id          VARCHAR(36)  PRIMARY KEY,
  nombre              VARCHAR(120) NOT NULL,
  email               VARCHAR(255) NOT NULL UNIQUE,
  estado              ENUM('ACTIVO','SUSPENDIDO','PENDIENTE_VERIFICACION') DEFAULT 'PENDIENTE_VERIFICACION',
  mfa_habilitado      BOOLEAN DEFAULT FALSE,
  fecha_registro      DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_snapshot_estado (estado)
);

-- Reportes generados por admins
CREATE TABLE IF NOT EXISTS reportes (
  reporte_id                VARCHAR(36) PRIMARY KEY,
  generado_por              VARCHAR(36) NOT NULL,
  tipo                      ENUM('DIARIO','SEMANAL','MENSUAL') NOT NULL,
  periodo_inicio            DATETIME NOT NULL,
  periodo_fin               DATETIME NOT NULL,
  total_creditos_comprados  DOUBLE DEFAULT 0.0,
  total_creditos_apostados  DOUBLE DEFAULT 0.0,
  total_creditos_ganados    DOUBLE DEFAULT 0.0,
  balance_global            DOUBLE DEFAULT 0.0,
  fecha_generacion          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Movimientos de juego recibidos vía game-events (fuente propia para reportes)
CREATE TABLE IF NOT EXISTS movimientos_juego (
  id             VARCHAR(36)   PRIMARY KEY,
  usuario_id     VARCHAR(36)   NOT NULL,
  game_id        VARCHAR(36)   NOT NULL,
  event_type     VARCHAR(50)   NOT NULL,
  monto_apostado DOUBLE        DEFAULT 0.0,
  monto_ganado   DOUBLE        DEFAULT 0.0,
  timestamp      DATETIME      DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_mov_usuario (usuario_id),
  INDEX idx_mov_timestamp (timestamp)
);

CREATE TABLE IF NOT EXISTS admin_actions (
  id          VARCHAR(36)  PRIMARY KEY,
  admin_id    VARCHAR(36)  NOT NULL,
  target_user VARCHAR(36),
  action      VARCHAR(100) NOT NULL,
  reason      VARCHAR(500),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
