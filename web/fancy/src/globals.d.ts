declare type Omit<T, K> = Pick<T, Exclude<keyof T, K>>;

declare type Constructor<T = {}> = new (...args: any[]) => T;

type ProcessEnv = NodeJS.ProcessEnv;

interface Window {
  __REDUX_DEVTOOLS_EXTENSION_COMPOSE__: <R>(a: R) => R;
}

declare module 'mini-svg-data-uri' {
  export default function(uri: string): string;
}
