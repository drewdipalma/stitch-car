import React, { Component, Fragment } from 'react';
import { Logo, PathClear, PathBlocked, Thermometer, Lightbulb } from '../..';

interface Props {
  isPathClear: boolean;
  temperature: number;
  luminosity: number;
}

class Header extends Component<Props> {
  render() {
    const { isPathClear, temperature, luminosity } = this.props;
    return (
      <div className="header">
        <div className="header-logo">
          <Logo />
        </div>
        <div className="header-indicator">
          {isPathClear && (
            <Fragment>
              Path Clear<PathClear />
            </Fragment>
          )}
          {!isPathClear && (
            <Fragment>
              Path Blocked<PathBlocked />
            </Fragment>
          )}
        </div>
        <div className="header-indicator">
          {temperature}℉<Thermometer />
        </div>
        <div className="header-indicator">
          {luminosity}% Light<Lightbulb />
        </div>
      </div>
    );
  }
}

export default Header;
