import produce, { Draft } from 'immer';
import { ActionCreator, AsyncActionCreators, Failure, Success } from 'typescript-fsa';
import { reducerWithInitialState } from 'typescript-fsa-reducers';

type StateModifier<STATE, PAYLOAD> = (state: Draft<STATE>, payload: PAYLOAD) => void | STATE;

type ReducerCase<STATE, PAYLOAD, PARAMS = any, ERROR = Error> =
  | ActionHandler<STATE, PAYLOAD>
  | AsyncActionHandler<STATE, PARAMS, PAYLOAD, ERROR>;

interface ActionHandler<STATE, PAYLOAD> {
  isAsync: false;
  actionCreator: ActionCreator<PAYLOAD>;
  stateModifier: StateModifier<STATE, PAYLOAD>;
}

interface AsyncActionHandler<STATE, PARAMS, PAYLOAD, ERROR = Error> {
  isAsync: true;
  actionCreator: AsyncActionCreators<PARAMS, PAYLOAD, ERROR>;
  stateModifier: AsyncStateModifier<STATE, PAYLOAD, PARAMS, ERROR>;
}

interface AsyncStateModifier<STATE, PAYLOAD, PARAMS = any, ERROR = Error> {
  onRequest?: StateModifier<STATE, PARAMS>;
  onSuccess?: StateModifier<STATE, Success<PARAMS, PAYLOAD>>;
  onFailure?: StateModifier<STATE, Failure<PARAMS, ERROR>>;
}

export const buildReducer = <STATE>(
  initialState: STATE,
  reducerCases: ReducerCase<STATE, any, any, any>[]
) => {
  return reducerCases.reduce((reducer, reducerCase) => {
    if (!reducerCase.isAsync) {
      const { actionCreator: action, stateModifier } = reducerCase;

      reducer.case(action, (state, payload) =>
        produce(state, draft => stateModifier(draft, payload))
      );
    } else {
      const {
        actionCreator: async,
        stateModifier: { onRequest, onSuccess, onFailure }
      } = reducerCase;

      if (onRequest) {
        reducer.case(async.started, (state, params) =>
          produce(state, draft => onRequest(draft, params))
        );
      }

      if (onSuccess) {
        reducer.case(async.done, (state, success) =>
          produce(state, draft => onSuccess(draft, success))
        );
      }

      if (onFailure) {
        reducer.case(async.failed, (state, failure) =>
          produce(state, draft => onFailure(draft, failure))
        );
      }
    }
    return reducer;
  }, reducerWithInitialState(initialState));
};

export const newActionHandler = <STATE, PAYLOAD>(
  actionCreator: ActionCreator<PAYLOAD>,
  stateModifier: StateModifier<STATE, PAYLOAD>
): ActionHandler<STATE, PAYLOAD> => ({ isAsync: false, actionCreator, stateModifier });

export const newAsyncActionHandler = <STATE, PARAMS, PAYLOAD, ERROR = Error>(
  actionCreator: AsyncActionCreators<PARAMS, PAYLOAD, ERROR>,
  stateModifier: AsyncStateModifier<STATE, PAYLOAD, PARAMS, ERROR>
): AsyncActionHandler<STATE, PARAMS, PAYLOAD, ERROR> => ({
  isAsync: true,
  actionCreator,
  stateModifier
});
