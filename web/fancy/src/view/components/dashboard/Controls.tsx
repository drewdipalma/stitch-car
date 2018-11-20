import React, { Component } from 'react';
import { DirectionControl, SpeedControl } from '../';

interface Props {
  angle: number;
  speedIsForward: boolean;
  speedValue: number;
  addMove: () => void;
  setAngle: (angle: number) => void;
  setSpeedForward: (isForward: boolean) => void;
  setSpeedValue: (value: number) => void;
}

class Controls extends Component<Props> {
  render() {
    return (
      <div className="controls">
        <DirectionControl {...this.props} />
        <SpeedControl {...this.props} />
      </div>
    );
  }
}

export default Controls;
