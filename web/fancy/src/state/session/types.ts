export interface User {
  id: string;
  provider: string;
  type?: string;
  name?: string;
  email?: string;
}

export interface SessionState {
  currentUser?: User;
}

export const initialSessionState: SessionState = {};
