import { buildReducer, newActionHandler, newAsyncActionHandler } from '../../utils';
import {
  initialRoverState,
  findRover,
  updateRover,
  setRoverAngle,
  setRoverSpeedForward,
  setRoverSpeedValue,
} from './';

export default buildReducer(initialRoverState, [
  newAsyncActionHandler(findRover.async, {
    onRequest: state => {
      state.loading = true;
      delete state.data;
      delete state.error;
    },
    onSuccess: (state, { result: roverData }) => {
      state.loading = false;
      state.data = roverData;
    },
    onFailure: (state, { error }) => {
      state.loading = false;
      state.error = error;
    },
  }),
  newActionHandler(updateRover, (state, roverData) => {
    state.data = roverData;
  }),
  newActionHandler(setRoverAngle, (state, angle) => {
    state.control.angle = angle;
  }),
  newActionHandler(setRoverSpeedForward, (state, isForward) => {
    state.control.speed.isForward = isForward;
  }),
  newActionHandler(setRoverSpeedValue, (state, value) => {
    state.control.speed.value = value;
  })
]);
