import { combineReducers } from 'redux';
import { connectRouter } from 'connected-react-router'
import { History } from 'history'
import { AppState } from '.';
import sessionReducer from './session/reducer';
import roverReducer from './rover/reducer';

export default (history: History) => combineReducers<AppState>({
  session: sessionReducer,
  rover: roverReducer,
  router: connectRouter(history)
})
