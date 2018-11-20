export const con = <T>(arg?: T): T => {
  if (arg !== undefined) {
    return arg;
  }
  throw new Error('arg expected');
};

export const isFunction = <T extends Function>(value: any): value is T =>
  typeof value === 'function';
