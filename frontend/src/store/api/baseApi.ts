import {
  createApi,
  fetchBaseQuery,
  BaseQueryFn,
  FetchArgs,
  FetchBaseQueryError,
} from '@reduxjs/toolkit/query/react';

import { AuthTokensSchema } from '@/schemas/auth.schema';
import { oauth2AuthService } from '@/services/authService';
import type { RootState } from '@/store';
import { setTokens, clearAuth } from '@/store/slices/authSlice';
import { safeValidateApiResponse } from '@/utils/validation';
import Config from "@/config";

const baseQuery = fetchBaseQuery({
  baseUrl: Config.api.baseUrl,
  timeout: Config.api.timeout,
  prepareHeaders: async headers => {
    try {
      const tokens = await oauth2AuthService.getTokens();
      if (tokens?.accessToken) {
        headers.set('Authorization', `Bearer ${tokens.accessToken}`);
      }
    } catch (e) {
      console.warn('Failed to attach auth header:', e);
    }

    if (!headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }
    return headers;
  },
});

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

const baseQueryWithReauth: BaseQueryFn<
  string | FetchArgs,
  unknown,
  FetchBaseQueryError
> = async (args, api, extraOptions) => {
  // Wait if refresh is in progress
  if (isRefreshing && refreshPromise) {
    await refreshPromise;
  }

  let result = await baseQuery(args, api, extraOptions);

  if (result.error?.status === 401) {
    const state = api.getState() as RootState;
    const refreshToken = state.auth?.tokens?.refreshToken;

    if (!refreshToken) {
      api.dispatch(clearAuth());
      return result;
    }

    // Prevent multiple simultaneous refresh attempts
    if (!isRefreshing) {
      isRefreshing = true;
      refreshPromise = (async () => {
        try {
          const tokens = await oauth2AuthService.refreshAccessToken();

          if (tokens) {
            const validationResult = safeValidateApiResponse(
              AuthTokensSchema,
              tokens
            );

            if (validationResult.success) {
              api.dispatch(setTokens(validationResult.data));
              return true;
            }
          }

          api.dispatch(clearAuth());
          return false;
        } catch (_err) {
          api.dispatch(clearAuth());
          return false;
        } finally {
          isRefreshing = false;
          refreshPromise = null;
        }
      })();

      const refreshSuccess = await refreshPromise;

      if (refreshSuccess) {
        result = await baseQuery(args, api, extraOptions);
      }
    } else {
      await refreshPromise;
      result = await baseQuery(args, api, extraOptions);
    }
  }

  return result;
};

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['User', 'Auth'],
  endpoints: () => ({}),
});
