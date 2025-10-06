import '@/utils/gestureHandler';

import React, { useEffect, useState } from 'react';

import { useFonts } from 'expo-font';
import { Slot } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';

import { KeyboardProvider } from 'react-native-keyboard-controller';
import {
    initialWindowMetrics,
    SafeAreaProvider,
} from 'react-native-safe-area-context';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';

import { AnimatedSplashScreen } from '@/components/AnimatedSplashScreen';
import { ErrorBoundary } from '@/components/errorBoundary/ErrorBoundary';
import { RootErrorBoundary } from '@/components/errorBoundary/RootErrorBoundary';
import { initI18n } from '@/i18n';
import { store, persistor } from '@/store';
import { ThemeProvider } from '@/theme/context';
import { customFontsToLoad } from '@/theme/typography';
import { loadDateFnsLocale } from '@/utils/formatDate';

SplashScreen.preventAutoHideAsync();

/**
 * This is the root component of our app.
 * @returns {React.ReactNode} The rendered `App` component.
 */
export default function RootLayout(): React.ReactNode {
    const [fontsLoaded, fontError] = useFonts(customFontsToLoad);
    const [isI18nInitialized, setIsI18nInitialized] = useState(false);
    const [showAnimatedSplash, setShowAnimatedSplash] = useState(true);

    useEffect(() => {
        initI18n()
            .then(() => setIsI18nInitialized(true))
            .then(() => loadDateFnsLocale());
    }, []);

    useEffect(() => {
        if (fontError) throw fontError;
    }, [fontError]);

    useEffect(() => {
        if (fontsLoaded && isI18nInitialized) {
            SplashScreen.hideAsync();
        }
    }, [fontsLoaded, isI18nInitialized]);

    if (!fontsLoaded || !isI18nInitialized) {
        return null;
    }

    if (showAnimatedSplash) {
        return (
            <AnimatedSplashScreen
                onComplete={() => {
                    setShowAnimatedSplash(false);
                }}
            />
        );
    }

    // otherwise, we're ready to render the app
    return (
        <RootErrorBoundary
            onError={(error, errorInfo) => {
                // Log to your error tracking service for theme/provider errors
                console.log('Provider Error logged:', error, errorInfo);
            }}
            onReset={() => {
                // Clean up any state
                console.log('Provider error boundary reset');
            }}
        >
            <SafeAreaProvider initialMetrics={initialWindowMetrics}>
                <Provider store={store}>
                    <PersistGate
                        loading={null}
                        persistor={persistor}
                    >
                        <ThemeProvider>
                            <ErrorBoundary
                                onError={(error, errorInfo) => {
                                    // Log to your error tracking service for app errors
                                    console.log('App Error logged:', error, errorInfo);
                                }}
                                onReset={() => {
                                    // Clean up any state
                                    console.log('App error boundary reset');
                                }}
                            >
                                <KeyboardProvider>
                                    <Slot />
                                </KeyboardProvider>
                            </ErrorBoundary>
                        </ThemeProvider>
                    </PersistGate>
                </Provider>
            </SafeAreaProvider>
        </RootErrorBoundary>
    );
}
