import React, { Component } from 'react';

interface Props {
  speedValue: number;
  setSpeedValue: (value: number) => void;
}

class SpeedControl extends Component<Props> {
  render() {
    const { speedValue, setSpeedValue } = this.props;
    return (
      <div className="controls-speed">
        <h2>Speed</h2>
        <div>
          <input
            type="range"
            min="1"
            max="3"
            step="1"
            value={speedValue}
            onChange={e => setSpeedValue(parseInt(e.target.value))}
          />
        </div>
      </div>
    );
  }
}

export default SpeedControl;
