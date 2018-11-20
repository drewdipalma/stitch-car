import { ComponentType } from 'react';

export const getComponentName = (component: ComponentType<any>) =>
  component.displayName || (component as any).name;

export const getHocComponentName = (hocName: string, component: ComponentType<any>) =>
  `${hocName}(${getComponentName(component)})`;
