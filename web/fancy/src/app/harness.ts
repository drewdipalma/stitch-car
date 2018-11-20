import { applyMiddleware, createStore, compose, Middleware, Store } from 'redux';
import thunk from 'redux-thunk';
import { routerMiddleware } from 'connected-react-router';
import { createBrowserHistory, createMemoryHistory, History } from 'history';
import { StitchAppClient } from 'mongodb-stitch-browser-sdk';
import { AppState, initialAppState, login, findRover, watchRover } from '../state';
import createRootReducer from '../state/reducers';

class Harness {
  public history: History;
  private middlewares: Middleware[];
  public store: Store<AppState>;

  constructor(public stitch: StitchAppClient) {
    this.history = typeof window !== 'undefined' ? createBrowserHistory() : createMemoryHistory();
    this.middlewares = [routerMiddleware(this.history), thunk.withExtraArgument({ stitch })];

    this.setupLoggerMiddleware();

    const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
    this.store = createStore(
      createRootReducer(this.history),
      initialAppState,
      composeEnhancers(applyMiddleware(...this.middlewares))
    );

    this.store.dispatch<any>(login.action())
      .then(() => this.store.dispatch<any>(findRover.action()))
      .then(() => this.store.dispatch<any>(watchRover.action()));
  }

  setupLoggerMiddleware() {
    const { createLogger } = require('redux-logger');
    this.middlewares.push(createLogger({ duration: true }));
  }
}

export default Harness;
