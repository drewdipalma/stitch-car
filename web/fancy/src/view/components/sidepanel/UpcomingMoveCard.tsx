import React, { Component } from 'react';
import { MoveCard } from '..'

interface Props {
  speed: number;
  angle: number;
}

class NextMoveCard extends Component<Props> {
  render() {
    return (
      <div className="move-card-upcoming">
        <hr />
        <MoveCard {...this.props} />
      </div>
    );
  }
}

export default NextMoveCard;
