import React from 'react';
import { DirectionGauge } from '..';

interface Props {
  angle: number;
  speedIsForward: boolean;
  addMove: () => void;
  setAngle: (angle: number) => void;
  setSpeedForward: (speedIsForward: boolean) => void;
}

const DirectionControl = ({ angle, speedIsForward, addMove, setAngle, setSpeedForward }: Props) => (
  <div className="controls-direction">
    <h2>Direction</h2>
    <DirectionGauge
      angle={angle}
      speedIsForward={speedIsForward}
      setAngle={setAngle}
      setSpeedForward={setSpeedForward}
    />
    <button className="button button-is-primary controls-direction-add" onClick={addMove}>
      Add Move
    </button>
  </div>
);

export default DirectionControl;
