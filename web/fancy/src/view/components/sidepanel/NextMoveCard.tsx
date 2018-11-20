import React, { Component } from 'react';
import { MoveCard } from '..'

interface Props {
  speed: number;
  angle: number;
}

class NextMoveCard extends Component<Props> {
  render() {
    return (
      <div className="move-card-next">
        <div className="move-card-next-title">UP NEXT</div>
        <MoveCard {...this.props} />
      </div>
    );
  }
}

export default NextMoveCard;
