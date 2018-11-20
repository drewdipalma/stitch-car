import actionCreatorFactory from 'typescript-fsa';
import { asyncFactory } from 'typescript-fsa-redux-thunk';
import { RemoteMongoClient, RemoteUpdateResult } from 'mongodb-stitch-browser-sdk';
import BSON from 'bson';
import { AppState, AsyncContext, RoverData } from '..';

const create = actionCreatorFactory('rover');
const createAsync = asyncFactory<AppState, AsyncContext>(create);

const ROVER_ID = '5bee1053fdc728f2623e20eb';

export const findRover = createAsync<{}, RoverData | undefined>(
  'find',
  (_params, _dispatch, _getState, { stitch }) =>
    stitch
      .getServiceClient(RemoteMongoClient.factory, 'mongodb-atlas')
      .db('rover')
      .collection<RoverData>('rovers')
      .find({ _id: ROVER_ID })
      .first()
);

export const updateRover = create<RoverData>('update');

export const watchRover = createAsync(
  'watch',
  (_params, dispatch, _getState, { stitch }) =>
    stitch
      .getServiceClient(RemoteMongoClient.factory, 'mongodb-atlas')
      .db('rover')
      .collection<RoverData>('rovers')
      .watch([ROVER_ID as any])
      .then((stream: any) => {
        stream.onNext((e: any) => {
          dispatch(updateRover(e.fullDocument));
        })
      })
);

export const setRoverAngle = create<number>('set angle');

export const setRoverSpeedForward = create<boolean>('set speed forward');

export const setRoverSpeedValue = create<number>('set speed value');

interface AddRoverMoveRequest {
  angle: number;
  isForward: boolean;
  value: number;
}

export const addRoverMove = createAsync<AddRoverMoveRequest, RemoteUpdateResult>(
  'add move',
  ({ angle, isForward, value }, _dispatch, _getState, { stitch }) =>
    stitch
      .getServiceClient(RemoteMongoClient.factory, 'mongodb-atlas')
      .db('rover')
      .collection<RoverData>('rovers')
      .updateOne(
        { _id: ROVER_ID },
        { '$push': { moves: {
          _id: new BSON.ObjectId().toHexString(),
          angle,
          speed: (isForward ? 1 : -1) * value
        } } }
      )
);
