import React from 'react';

import { View } from 'react-native';
import type { ViewStyle, TextStyle } from 'react-native';

import { Button } from '@/components/Button';
import { Screen } from '@/components/Screen';
import { Text } from '@/components/Text';
import { useAuth } from '@/hooks/useAuth';
import { useAppTheme } from '@/theme/context';
import type { ThemedStyle } from '@/theme/types';

export default function HomeScreen() {
  const { themed } = useAppTheme();
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
  };

  return (
    <Screen preset="scroll" contentContainerStyle={themed($screenContent)}>
      <View style={themed($container)}>
        <Text preset="heading" style={themed($title)}>
          Welcome to SafeNet
        </Text>

        <Text preset="default" style={themed($subtitle)}>
          Hello, {user?.preferred_username || user?.email || 'User'}!
        </Text>

        <Text preset="formHelper" style={themed($description)}>
          This is your home screen. You can access all the main features of
          SafeNet from here.
        </Text>

        <View style={themed($buttonContainer)}>
          <Button
            preset="primary"
            text="Logout"
            onPress={handleLogout}
            style={themed($logoutButton)}
          />
        </View>
      </View>
    </Screen>
  );
}

// Themed Styles
const $screenContent: ThemedStyle<ViewStyle> = theme => ({
  flex: 1,
  padding: theme.spacing.lg,
});

const $container: ThemedStyle<ViewStyle> = () => ({
  flex: 1,
  justifyContent: 'center',
  alignItems: 'center',
  maxWidth: 400,
  width: '100%',
  alignSelf: 'center',
});

const $title: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  marginBottom: theme.spacing.md,
});

const $subtitle: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  marginBottom: theme.spacing.lg,
  fontSize: 18,
  fontWeight: '500',
});

const $description: ThemedStyle<TextStyle> = () => ({
  textAlign: 'center',
  marginBottom: 32,
});

const $buttonContainer: ThemedStyle<ViewStyle> = () => ({
  width: '100%',
  alignItems: 'center',
});

const $logoutButton: ThemedStyle<ViewStyle> = theme => ({
  minWidth: 120,
  backgroundColor: theme.colors.error,
});
