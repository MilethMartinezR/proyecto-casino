const { GameRepositoryAdapter } = require('../adapter/out/persistence/gameRepositoryAdapter');
const { WalletServiceAdapter } = require('../adapter/out/http/walletServiceAdapter');
const { KafkaEventPublisher } = require('../adapter/out/messaging/kafkaEventPublisher');

const { StartGameUseCaseImpl } = require('../../application/usecase/startGameUseCaseImpl');
const { HitUseCaseImpl } = require('../../application/usecase/hitUseCaseImpl');
const { StandUseCaseImpl } = require('../../application/usecase/standUseCaseImpl');
const { AbandonGameUseCaseImpl } = require('../../application/usecase/abandonGameUseCaseImpl');
const { GetGameHistoryUseCaseImpl } = require('../../application/usecase/getGameHistoryUseCaseImpl');
const { GetGameStateUseCaseImpl } = require('../../application/usecase/getGameStateUseCaseImpl');

const { GameController } = require('../adapter/in/rest/controllers/game.controller');

function buildContainer() {
  const gameRepository  = new GameRepositoryAdapter();
  const walletService   = new WalletServiceAdapter();
  const eventPublisher  = new KafkaEventPublisher();

  const startGame  = new StartGameUseCaseImpl(gameRepository, walletService, eventPublisher);
  const hitGame    = new HitUseCaseImpl(gameRepository, eventPublisher);
  const standGame  = new StandUseCaseImpl(gameRepository, eventPublisher);
  const abandonGame = new AbandonGameUseCaseImpl(gameRepository, eventPublisher);
  const getHistory = new GetGameHistoryUseCaseImpl(gameRepository);
  const getGameState = new GetGameStateUseCaseImpl(gameRepository);

  const controller = new GameController(startGame, hitGame, standGame, abandonGame, getHistory, getGameState);

  return { controller };
}

module.exports = { buildContainer };
