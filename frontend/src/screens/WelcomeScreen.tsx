import React, { useEffect } from 'react';
import { View } from 'react-native';
import type { TextStyle, ViewStyle } from 'react-native';
import { Button } from '@/components/Button';
import { Screen } from '@/components/Screen';
import { Text } from '@/components/Text';
import { useAuth } from '@/hooks/useAuth';
import { useAppTheme } from '@/theme/context';
import type { ThemedStyle } from '@/theme/types';
import { useRouter } from 'expo-router';

const WelcomeScreen = () => {
  const router = useRouter();
  const { themed } = useAppTheme();
  const { login, isLoading, error, isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated && !isLoading) {
      router.replace('/(tabs)');
    }
  }, [isAuthenticated, isLoading, router]);

  const handleLogin = async () => {
    await login();
  };

  return (
      <Screen preset="fixed" contentContainerStyle={themed($screenContent)}>
        <View style={themed($container)}>
          <Text preset="heading" style={themed($title)}>
            Welcome to SafeNet
          </Text>

          <Text preset="default" style={themed($subtitle)}>
            Your safety network in times of crisis.
          </Text>

          <Text preset="formHelper" style={themed($description)}>
            SafeNet helps you find secure evacuation routes, share your status,
            and stay informed with live disaster alerts and crowd reports —
            all in one place.
          </Text>

          {error && (
              <Text preset="formHelper" style={themed($errorText)}>
                {error}
              </Text>
          )}

          <View style={themed($buttonContainer)}>
            <Button
                preset="primary"
                text={isLoading ? 'Signing in...' : 'Sign In to Stay Safe'}
                onPress={handleLogin}
                disabled={isLoading}
                style={themed($loginButton)}
            />
          </View>

          <Text preset="formHelper" style={themed($footerText)}>
            By signing in, you can report incidents, mark yourself safe, and
            access real-time evacuation maps.
          </Text>
        </View>
      </Screen>
  );
};

// Themed Styles
const $screenContent: ThemedStyle<ViewStyle> = theme => ({
  flex: 1,
  justifyContent: 'center',
  alignItems: 'center',
  padding: theme.spacing.lg,
});

const $container: ThemedStyle<ViewStyle> = () => ({
  alignItems: 'center',
  justifyContent: 'center',
  maxWidth: 400,
  width: '100%',
});

const $title: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  marginBottom: theme.spacing.md,
});

const $subtitle: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  marginBottom: theme.spacing.lg,
  opacity: 0.9,
});

const $description: ThemedStyle<TextStyle> = () => ({
  textAlign: 'center',
  opacity: 0.8,
  marginBottom: 32,
  maxWidth: 320,
});

const $errorText: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  color: theme.colors.error,
  marginBottom: 16,
});

const $buttonContainer: ThemedStyle<ViewStyle> = () => ({
  width: '100%',
  alignItems: 'center',
});

const $loginButton: ThemedStyle<ViewStyle> = theme => ({
  minWidth: 220,
  backgroundColor: theme.colors.tint,
});

const $footerText: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  color: theme.colors.textDim,
  marginTop: theme.spacing.md,
  opacity: 0.7,
  maxWidth: 280,
});

export default WelcomeScreen;
