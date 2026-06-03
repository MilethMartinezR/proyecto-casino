-- =============================================
-- CQRS Write Side — MySQL Schema
-- =============================================

-- Auth Service
CREATE TABLE IF NOT EXISTS users (
  id            VARCHAR(36)  PRIMARY KEY,
  username      VARCHAR(100) UNIQUE NOT NULL,
  email         VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('PLAYER','ADMIN') DEFAULT 'PLAYER',
  active        BOOLEAN DEFAULT TRUE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Wallet Service
CREATE TABLE IF NOT EXISTS wallets (
  id         VARCHAR(36)    PRIMARY KEY,
  user_id    VARCHAR(36)    NOT NULL UNIQUE,
  balance    DECIMAL(15,2)  DEFAULT 0.00,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS transactions (
  id          VARCHAR(36)   PRIMARY KEY,
  wallet_id   VARCHAR(36)   NOT NULL,
  user_id     VARCHAR(36)   NOT NULL,
  type        ENUM('DEPOSIT','WITHDRAWAL','BET','WIN') NOT NULL,
  amount      DECIMAL(15,2) NOT NULL,
  description VARCHAR(500),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

-- Game Service (CQRS write side — estado activo de partidas)
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

-- Admin Service
CREATE TABLE IF NOT EXISTS admin_actions (
  id          VARCHAR(36)  PRIMARY KEY,
  admin_id    VARCHAR(36)  NOT NULL,
  target_user VARCHAR(36),
  action      VARCHAR(100) NOT NULL,
  reason      VARCHAR(500),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
