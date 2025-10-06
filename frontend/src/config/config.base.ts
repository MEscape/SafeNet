import Constants from 'expo-constants';
import {auth, root} from "@/config/config.redux";
import type { PersistConfig } from 'redux-persist';
import {RootState} from "@/store";
import {AuthState} from "@/store/slices/authSlice";

const {
  EXPO_PUBLIC_OAUTH_CLIENT_ID,
  EXPO_PUBLIC_API_BASE_URL,
  EXPO_PUBLIC_OAUTH_REDIRECT_PATH,
  EXPO_PUBLIC_APP_SCHEME,
  EXPO_PUBLIC_OAUTH_ISSUER,
} = Constants.expoConfig?.extra || {};

export interface ConfigBaseProps {
  catchErrors: 'always' | 'dev' | 'prod' | 'never';
  oauth: {
    clientId: string;
    redirectPath: string;
    appScheme: string;
    scopes: string[];
    issuer: string;
  };
  api: {
    baseUrl: string;
    timeout: number;
  };
  redux: {
    persist: {
      root: PersistConfig<RootState>;
      auth: PersistConfig<AuthState>;
    };
  }
}

const BaseConfig: ConfigBaseProps = {
  /**
   * Only enable if we're catching errors in the right environment
   */
  catchErrors: 'always',

  /**
   * OAuth configuration from environment variables
   */
  oauth: {
    clientId: EXPO_PUBLIC_OAUTH_CLIENT_ID,
    redirectPath: EXPO_PUBLIC_OAUTH_REDIRECT_PATH,
    appScheme: EXPO_PUBLIC_APP_SCHEME,
    scopes: ['openid', 'email'],
    issuer: EXPO_PUBLIC_OAUTH_ISSUER,
  },

  /**
   * API configuration from environment variables
   */
  api: {
    baseUrl: EXPO_PUBLIC_API_BASE_URL,
    timeout: 10000,
  },

  redux: {
    persist: {
      root,
      auth
    }
  }
};

export default BaseConfig;
