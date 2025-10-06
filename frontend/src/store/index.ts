import { configureStore, combineReducers, Middleware } from '@reduxjs/toolkit';
import { createLogger } from 'redux-logger';
import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from 'redux-persist';

import { apiSlice } from './api/baseApi';
import authReducer from './slices/authSlice';
import Config from "@/config";
import * as app from 'app.json'

// Root reducer
const rootReducer = combineReducers({
  auth: persistReducer(Config.redux.persist.auth, authReducer),
  [apiSlice.reducerPath]: apiSlice.reducer,
});

// Persisted reducer
const persistedReducer = persistReducer(Config.redux.persist.root, rootReducer);

// Optimized error handling middleware
const errorHandlingMiddleware: Middleware = () => next => action => {
  try {
    return next(action);
  } catch (error) {
    if (__DEV__) {
      console.error('Redux Error:', error);
    }
    throw error;
  }
};

// Logger configuration (only in DEV)
const logger = __DEV__
  ? createLogger({
      collapsed: true,
      duration: true,
      timestamp: false, // Reduce overhead
      logErrors: true,
      diff: false, // Disable diff in production builds
    })
  : null;

// Configure store
export const store = configureStore({
  reducer: persistedReducer,
  middleware: getDefaultMiddleware => {
    const middleware = getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
      immutableCheck: __DEV__ ? { warnAfter: 128 } : false,
    })
      .concat(apiSlice.middleware)
      .concat(errorHandlingMiddleware);

    // Add logger only in development
    if (__DEV__ && logger) {
      return middleware.concat(logger);
    }

    return middleware;
  },
  devTools: __DEV__ && {
    name: app.expo.name,
    trace: false, // Disable trace in production
    traceLimit: 25,
    maxAge: 50, // Limit action history
  },
});

// Create persistor
export const persistor = persistStore(store);

export type RootState = ReturnType<typeof rootReducer>;
export type PersistedRootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
