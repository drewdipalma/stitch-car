import React, { Component } from 'react';
import classNames from 'classnames';

interface Props {
  angle: number;
  speedIsForward: boolean;
  setAngle: (angle: number) => void;
  setSpeedForward: (speedIsForward: boolean) => void;
}

class DirectionGauge extends Component<Props> {
  toggleButtonClassNames = (speedIsForward: boolean) =>
    classNames('button', 'button-is-small', { 'button-is-pressed': speedIsForward == this.props.speedIsForward });

  toggleButton = (speedIsForward: boolean) => {
    this.props.setSpeedForward(speedIsForward);
  };

  render() {
    const { angle, setAngle } = this.props;
    return (
      <div className="controls-direction-gauge">
        <div className="controls-direction-gauge-meter">
          <input
            type="range"
            min="45"
            max="135"
            value={angle}
            onChange={e => setAngle(parseInt(e.target.value))}
          />
        </div>
        <div className="controls-direction-gauge-toggle">
          <button className={this.toggleButtonClassNames(true)} onClick={() => this.toggleButton(true)}>
            FORWARD
          </button>
          <button className={this.toggleButtonClassNames(false)} onClick={() => this.toggleButton(false)}>
            REVERSE
          </button>
        </div>
      </div>
    );
  }
}

export default DirectionGauge;
