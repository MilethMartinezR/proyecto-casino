const { BET_SAGA_STATE } = require('../state/betSagaState');

class BetSagaOrchestrator {
  constructor(steps) {
    this.steps = steps;
  }

  async run(context) {
    const executed = [];
    context.state = BET_SAGA_STATE.PENDING;

    for (const step of this.steps) {
      try {
        await step.execute(context);
        executed.push(step);
      } catch (err) {
        context.state = BET_SAGA_STATE.COMPENSATING;
        for (const done of executed.reverse()) {
          await done.compensate(context).catch((e) =>
            console.error(`[saga] compensate ${done.name} failed:`, e.message)
          );
        }
        context.state = BET_SAGA_STATE.FAILED;
        throw err;
      }
    }

    context.state = BET_SAGA_STATE.COMPLETED;
    return context;
  }
}

module.exports = { BetSagaOrchestrator };
