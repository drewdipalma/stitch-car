import React, { Component } from 'react';
import { ConnectedRouter } from 'connected-react-router';
import { Route, Switch } from 'react-router-dom';
import { ErrorBoundary, Home, NotFound } from '../view';
import { urls } from '../utils';

class Router extends Component<any> {
  render() {
    const { history } = this.props;
    return (
      <ConnectedRouter history={history}>
        <ErrorBoundary>
          <Switch>
            <Route exact path={urls.home()} component={Home} />
            <Route component={NotFound} />
          </Switch>
        </ErrorBoundary>
      </ConnectedRouter>
    );
  }
}

export default Router;
