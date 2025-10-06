import React from 'react';

import { View, Switch } from 'react-native';
import type { ViewStyle, TextStyle } from 'react-native';

import { Button } from '@/components/Button';
import { Screen } from '@/components/Screen';
import { Text } from '@/components/Text';
import { useAuth } from '@/hooks/useAuth';
import { useAppTheme } from '@/theme/context';
import type { ThemedStyle } from '@/theme/types';

export default function SettingsScreen() {
  const { themed, theme, themeContext, setThemeContextOverride } =
    useAppTheme();
  const { logout } = useAuth();

  const isDarkMode = themeContext === 'dark';

  const toggleTheme = () => {
    setThemeContextOverride(isDarkMode ? 'light' : 'dark');
  };

  const handleLogout = async () => {
    await logout();
  };

  return (
    <Screen preset="scroll" contentContainerStyle={themed($screenContent)}>
      <View style={themed($container)}>
        <Text preset="heading" style={themed($title)}>
          Settings
        </Text>

        <View style={themed($settingsCard)}>
          <View style={themed($settingItem)}>
            <Text preset="default" style={themed($settingLabel)}>
              Dark Mode
            </Text>
            <Switch
              value={isDarkMode}
              onValueChange={toggleTheme}
              trackColor={{
                false: theme.colors.palette.neutral300,
                true: theme.colors.tint,
              }}
              thumbColor={
                isDarkMode
                  ? theme.colors.palette.neutral100
                  : theme.colors.palette.neutral100
              }
            />
          </View>

          <View style={themed($settingItem)}>
            <Text preset="default" style={themed($settingLabel)}>
              Current Theme
            </Text>
            <Text preset="formHelper" style={themed($settingValue)}>
              {themeContext === 'dark' ? 'Dark' : 'Light'}
            </Text>
          </View>
        </View>

        <View style={themed($buttonContainer)}>
          <Button
            preset="primary"
            text="Logout"
            onPress={handleLogout}
            style={themed($logoutButton)}
          />
        </View>

        <Text preset="formHelper" style={themed($description)}>
          Customize your app experience and manage your account settings.
        </Text>
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
  maxWidth: 400,
  width: '100%',
  alignSelf: 'center',
});

const $title: ThemedStyle<TextStyle> = theme => ({
  textAlign: 'center',
  marginBottom: theme.spacing.xl,
});

const $settingsCard: ThemedStyle<ViewStyle> = theme => ({
  backgroundColor: theme.colors.palette.neutral100,
  borderRadius: theme.spacing.sm,
  padding: theme.spacing.lg,
  marginBottom: theme.spacing.lg,
});

const $settingItem: ThemedStyle<ViewStyle> = theme => ({
  flexDirection: 'row',
  justifyContent: 'space-between',
  alignItems: 'center',
  paddingVertical: theme.spacing.sm,
  borderBottomWidth: 1,
  borderBottomColor: theme.colors.border,
  marginBottom: theme.spacing.sm,
});

const $settingLabel: ThemedStyle<TextStyle> = () => ({
  fontWeight: '500',
});

const $settingValue: ThemedStyle<TextStyle> = theme => ({
  color: theme.colors.textDim,
});

const $buttonContainer: ThemedStyle<ViewStyle> = () => ({
  width: '100%',
  alignItems: 'center',
  marginBottom: 24,
});

const $logoutButton: ThemedStyle<ViewStyle> = theme => ({
  minWidth: 120,
  backgroundColor: theme.colors.error,
});

const $description: ThemedStyle<TextStyle> = () => ({
  textAlign: 'center',
  marginTop: 16,
});
