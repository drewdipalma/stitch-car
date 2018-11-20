import React, { Component } from 'react';

interface Props {}

interface State {
  err?: Error;
}

class ErrorBoundary extends Component<Props, State> {
  state: State = {};

  render() {
    const { children } = this.props;
    const { err } = this.state;

    if (err) {
      return (
        <div className="error-boundary">
          <h1>Oops ... something went wrong here!</h1>
          <p>Please try refreshing the page.</p>
          <hr />
          <div>
            <h2>Error Details</h2>
            <h3>{err.name || 'System Error'}</h3>
            <p>{err.message || 'An unexpected error occurred.'}</p>
          </div>
        </div>
      );
    }

    return children;
  }
}

export default ErrorBoundary;
