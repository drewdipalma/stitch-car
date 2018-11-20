import React from 'react';
import { render } from 'react-dom';
import { AppContainer } from 'react-hot-loader';
import { Provider } from 'react-redux';
import { Stitch } from 'mongodb-stitch-browser-sdk';
import { Harness, Router } from './app';

require('../static/favicon.ico');
require('../static/main.less');

const { store, history } = new Harness(Stitch.initializeDefaultAppClient('mongodb-rover-elouh'));

const renderApp = (Component: typeof Router) =>
  render(
    <Provider store={store}>
      <AppContainer>
        <Component history={history} />
      </AppContainer>
    </Provider>,
    document.getElementById('root')
  );

renderApp(Router);

if ((module as any).hot) {
  (module as any).hot.accept('./app/router', () => {
    const appRouter = require('./app/router');
    renderApp(appRouter);
  })
}
