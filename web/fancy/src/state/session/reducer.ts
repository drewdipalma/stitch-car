import { buildReducer, newAsyncActionHandler } from '../../utils';
import { initialSessionState, login, logout } from '.';

export default buildReducer(initialSessionState, [
  newAsyncActionHandler(login.async, {
    onSuccess: (state, { result }) => {
      const { id, loggedInProviderType: provider, userType: type, profile: { name, email } } = result;
      state.currentUser = { id, provider, type, name, email };
    },
  }),
  newAsyncActionHandler(logout.async, {
    onSuccess: (state) => {
      delete state.currentUser;
    },
  })
]);
