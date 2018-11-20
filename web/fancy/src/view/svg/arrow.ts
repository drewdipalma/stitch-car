import { SpeedCoordinates } from '../../state';
import fastArrow from './fast-arrow';
import normalArrow from './normal-arrow';
import slowArrow from './slow-arrow';

const parseSpeed = (speed: number) => ({
  isForward: Math.sign(speed) >= 0, // will consider 0 a "forward",
  value: Math.abs(speed)
});

const calculateRotation = (isForward: boolean, angle: number) => {
  if (isForward) {
    return angle - 90;
  }
  return angle + 90;
}

export default ({ speed, angle }: SpeedCoordinates) => {
  const { isForward, value } = parseSpeed(speed);
  const rotation = calculateRotation(isForward, angle);
  if (value <= 1) {
    return slowArrow(rotation);
  } else if (value <= 2) {
    return normalArrow(rotation);
  } else {
    return fastArrow(rotation);
  }
};
