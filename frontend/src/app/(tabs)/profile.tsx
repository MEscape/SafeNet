import React from 'react';

import { View } from 'react-native';
import type { ViewStyle, TextStyle } from 'react-native';

import { Screen } from '@/components/Screen';
import { Text } from '@/components/Text';
import { useAuth } from '@/hooks/useAuth';
import { useAppTheme } from '@/theme/context';
import type { ThemedStyle } from '@/theme/types';

export default function ProfileScreen() {
  const { themed } = useAppTheme();
  const { user } = useAuth();

  return (
    <Screen preset="scroll" contentContainerStyle={themed($screenContent)}>
      <View style={themed($container)}>
        <Text preset="heading" style={themed($title)}>
          Profile
        </Text>

        <View style={themed($profileCard)}>
          <Text preset="subheading" style={themed($label)}>
            Name
          </Text>
          <Text preset="default" style={themed($value)}>
            {user?.preferred_username || 'Not provided'}
          </Text>

          <Text preset="subheading" style={themed($label)}>
            Email
          </Text>
          <Text preset="default" style={themed($value)}>
            {user?.email || 'Not provided'}
          </Text>

          <Text preset="subheading" style={themed($label)}>
            User ID
          </Text>
          <Text preset="default" style={themed($value)}>
            {user?.sub || 'Not available'}
          </Text>
        </View>

        <Text preset="formHelper" style={themed($description)}>
          Your profile information is synced from your authentication provider.
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

const $profileCard: ThemedStyle<ViewStyle> = theme => ({
  backgroundColor: theme.colors.palette.neutral100,
  borderRadius: theme.spacing.sm,
  padding: theme.spacing.lg,
  marginBottom: theme.spacing.lg,
});

const $label: ThemedStyle<TextStyle> = theme => ({
  marginTop: theme.spacing.md,
  marginBottom: theme.spacing.xs,
  fontWeight: '600',
});

const $value: ThemedStyle<TextStyle> = theme => ({
  marginBottom: theme.spacing.sm,
  color: theme.colors.textDim,
});

const $description: ThemedStyle<TextStyle> = () => ({
  textAlign: 'center',
  marginTop: 16,
});
