import React, { Component } from 'react';
import { MoveCard } from '..';

interface Props {
  angle: number;
  speedIsForward: boolean;
  speedValue: number;
}

const calculateSpeed = (isForward: boolean, value: number) => (isForward ? value : -1 * value);

class MovePreview extends Component<Props> {
  render() {
    const { angle, speedIsForward, speedValue } = this.props;
    const data = { angle, speed: calculateSpeed(speedIsForward, speedValue) };
    return (
      <div className="move-preview">
        <MoveCard {...data} />
        <p style={{textAlign: 'center' }}>{JSON.stringify(data)}</p>
      </div>
    );
  }
}

export default MovePreview;
