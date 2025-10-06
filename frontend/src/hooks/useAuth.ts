import { useCallback, useEffect } from 'react';

import {
  useGetCurrentUserQuery,
  useLoginMutation,
  useLogoutMutation,
  useRefreshTokenMutation,
} from '@/store/api/authApi';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import {
  setCredentials,
  clearAuth,
  selectIsAuthenticated,
  selectUser,
  selectAuthLoading,
  selectTokens,
  clearError,
  setLoading,
  setError,
  setTokens,
  setUser,
  selectAuthError,
} from '@/store/slices/authSlice';

export const useAuth = () => {
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const user = useAppSelector(selectUser);
  const tokens = useAppSelector(selectTokens);
  const isLoading = useAppSelector(selectAuthLoading);
  const error = useAppSelector(selectAuthError);

  const [loginMutation, { isLoading: isLoggingIn }] = useLoginMutation();
  const [logoutMutation, { isLoading: isLoggingOut }] = useLogoutMutation();
  const [refreshTokenMutation] = useRefreshTokenMutation();

  const {
    data: currentUser,
    isLoading: isFetchingUser,
    refetch: refetchUser,
  } = useGetCurrentUserQuery(undefined, {
    skip: !isAuthenticated, // only fetch if logged in
  });

  // sync store when currentUser updates
  useEffect(() => {
    if (currentUser) {
      dispatch(setUser(currentUser));
    }
  }, [currentUser, dispatch]);

  const login = useCallback(async () => {
    dispatch(setLoading(true));
    dispatch(clearError());

    try {
      const response = await loginMutation().unwrap();
      dispatch(setCredentials(response));

      return { success: true, data: response };
    } catch (error: any) {
      dispatch(setError(error.error ?? 'Login failed'));
      return { success: false, error };
    } finally {
      dispatch(setLoading(false));
    }
  }, [loginMutation, dispatch]);

  const logout = useCallback(async () => {
    dispatch(setLoading(true));
    dispatch(clearError());

    try {
      await logoutMutation().unwrap();
      dispatch(clearAuth());
      return { success: true };
    } catch (error: any) {
      dispatch(clearAuth());
      dispatch(setError(error.error ?? 'Logout failed'));
      return { success: false, error };
    } finally {
      dispatch(setLoading(false));
    }
  }, [logoutMutation, dispatch]);

  const refreshToken = useCallback(async () => {
    try {
      const tokens = await refreshTokenMutation().unwrap();
      dispatch(setTokens(tokens));
      return tokens;
    } catch (error: any) {
      dispatch(setError(error.error ?? 'Token refresh failed'));
      return null;
    }
  }, [dispatch, refreshTokenMutation]);

  return {
    user,
    tokens,
    isAuthenticated,
    isLoading: isLoading || isLoggingIn || isLoggingOut || isFetchingUser,
    login,
    logout,
    refreshToken,
    refetchUser,
    error,
  };
};
