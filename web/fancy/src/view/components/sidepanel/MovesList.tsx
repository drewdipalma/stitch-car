import React, { Component } from 'react';
import { Move } from '../../../state';
import { NextMoveCard, UpcomingMoveCard } from '..';

interface Props {
  moves: Move[];
}

class MovesList extends Component<Props> {
  render() {
    const { moves } = this.props;
    return (
      <div className="moves-list">
        <h3>Upcoming Moves</h3>
        {moves.length > 0 && <NextMoveCard {...moves[0]} />}
        {moves.length > 1 &&
          moves.slice(1).map(move => <UpcomingMoveCard key={move._id} {...move} />)}
      </div>
    );
  }
}

export default MovesList;
