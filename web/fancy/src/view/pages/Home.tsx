import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import {
  AppState,
  RoverData,
  RoverControl,
  setRoverAngle,
  setRoverSpeedForward,
  setRoverSpeedValue,
  addRoverMove,
} from '../../state';
import { Header, MovePreview, Controls, MovesList } from '..';

interface OwnProps {}

interface StateProps {
  roverData?: RoverData;
  roverControl: RoverControl;
}

interface DispatchProps {
  addMove: () => void;
  setAngle: (angle: number) => void;
  setSpeedForward: (isForward: boolean) => void;
  setSpeedValue: (value: number) => void;
}

type Props = OwnProps & StateProps & DispatchProps;

class Home extends Component<Props> {
  render() {
    const { roverData = { moves: [] }, roverControl, addMove, setAngle, setSpeedForward, setSpeedValue } = this.props;
    const moveData = { angle: roverControl.angle, speedIsForward: roverControl.speed.isForward, speedValue: roverControl.speed.value };
    return (
      <div className="home">
        <div className="home-dashboard">
          <Header isPathClear={true} temperature={67} luminosity={87} />
          <hr />
          <MovePreview {...moveData} />
          <Controls
            {...moveData}
            addMove={addMove}
            setAngle={setAngle}
            setSpeedForward={setSpeedForward}
            setSpeedValue={setSpeedValue}
          />
        </div>
        <div className="home-sidepanel">
          <MovesList moves={roverData.moves} />
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ rover }: AppState) => ({
  roverData: rover.data,
  roverControl: rover.control,
});

interface UnmergedDispatchProps {
  addMove: (angle: number, isForward: boolean, value: number) => void;
  setAngle: (angle: number) => void;
  setSpeedForward: (isForward: boolean) => void;
  setSpeedValue: (value: number) => void;
}

const mapDispatchToProps = (dispatch: Dispatch): UnmergedDispatchProps => ({
  addMove: (angle: number, isForward: boolean, value: number) =>
    dispatch<any>(addRoverMove.action({ angle, isForward, value })),
  setAngle: (angle: number) => dispatch(setRoverAngle(angle)),
  setSpeedForward: (isForward: boolean) => dispatch(setRoverSpeedForward(isForward)),
  setSpeedValue: (value: number) => dispatch(setRoverSpeedValue(value)),
});

const mergeProps = ({ roverData, roverControl }: StateProps, { addMove, ...dispatchProps }: UnmergedDispatchProps) => ({
  roverData,
  roverControl,
  addMove: () => addMove(roverControl.angle, roverControl.speed.isForward, roverControl.speed.value),
  ...dispatchProps,
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps
)(Home);
