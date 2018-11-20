import React, { Component } from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import svgToMiniDataURI from 'mini-svg-data-uri';
import { Arrow } from '../../';

interface Props {
  speed: number;
  angle: number;
}

class MoveCard extends Component<Props> {
  render() {
    return (
      <div
        className="move-card"
        style={{
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          backgroundSize: '80px',
          backgroundImage: `url("${svgToMiniDataURI(renderToStaticMarkup(<Arrow {...this.props} />))}")`,
        }}
      />
    );
  }
}

export default MoveCard;
