import { StitchAppClient } from 'mongodb-stitch-browser-sdk';
import { RouterState } from 'connected-react-router';
import { SessionState, initialSessionState as session, RoverState, initialRoverState as rover } from '.';

export interface AsyncContext {
  stitch: StitchAppClient;
}

export interface AppState {
  session: SessionState;
  rover: RoverState;
  router: RouterState;
}

export const initialAppState: AppState = {
  session,
  rover,
  router: {
    location: {
      pathname: '/',
      search: '',
      hash: '',
      state: undefined
    },
    action: 'POP'
  }
 }
