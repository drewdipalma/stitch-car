export interface SpeedCoordinates {
  angle: number;
  speed: number;
}

export interface Move extends SpeedCoordinates {
  _id: string;
}

export interface RoverSpeed {
  isForward: boolean;
  value: number;
}

export interface RoverControl {
  angle: number;
  speed: RoverSpeed;
}

export interface RoverData {
  _id: string;
  moves: Move[];
}

export interface RoverState {
  loading: boolean;
  error?: Error;
  data?: RoverData;
  control: RoverControl;
}

export const initialRoverState: RoverState = {
  loading: false,
  control: {
    angle: 90,
    speed: { isForward: true, value: 2 },
  },
};
