import React, {useRef, useState} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
} from 'react-native';
import {WebView} from 'react-native-webview';
import type {WebViewErrorEvent} from 'react-native-webview/lib/WebViewTypes';
import Config from 'react-native-config';

const VALID_ENVIRONMENTS = ['dev', 'staging', 'client-sandbox', 'prod'];

function validateConfig(): string[] {
  const errors: string[] = [];

  if (!Config.BWELL_ENVIRONMENT || Config.BWELL_ENVIRONMENT.trim() === '') {
    errors.push('BWELL_ENVIRONMENT is required');
  } else if (!VALID_ENVIRONMENTS.includes(Config.BWELL_ENVIRONMENT)) {
    errors.push(
      `BWELL_ENVIRONMENT must be one of: ${VALID_ENVIRONMENTS.join(', ')}`,
    );
  }

  if (!Config.BWELL_CLIENT_ID || Config.BWELL_CLIENT_ID.trim() === '') {
    errors.push('BWELL_CLIENT_ID is required');
  }

  if (!Config.CLIENT_USER_TOKEN || Config.CLIENT_USER_TOKEN.trim() === '') {
    errors.push('CLIENT_USER_TOKEN is required');
  }

  // INITIAL_PATH is optional, defaults to empty string

  return errors;
}

function buildUrl(): string {
  const environment = Config.BWELL_ENVIRONMENT ?? '';
  const clientId = Config.BWELL_CLIENT_ID ?? '';
  const initialPath = Config.INITIAL_PATH ?? '';
  return `https://app.${environment}.icanbwell.com/${clientId}/#${initialPath}`;
}

function ErrorScreen({message}: {message: string}): React.JSX.Element {
  return (
    <SafeAreaView style={styles.errorContainer}>
      <ScrollView contentContainerStyle={styles.errorContent}>
        <Text style={styles.errorText}>{'Configuration Error:\n\n' + message}</Text>
      </ScrollView>
    </SafeAreaView>
  );
}

export default function App(): React.JSX.Element {
  const webViewRef = useRef<WebView>(null);
  const hasInjectedToken = useRef(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  const validationErrors = validateConfig();
  if (validationErrors.length > 0) {
    return <ErrorScreen message={validationErrors.join('\n')} />;
  }

  if (loadError) {
    return <ErrorScreen message={loadError} />;
  }

  // Escape backslashes and single quotes so the token is safe inside the JS string literal
  const userToken = (Config.CLIENT_USER_TOKEN ?? '')
    .replace(/\\/g, '\\\\')
    .replace(/'/g, "\\'");

  const tokenSetterJs = `(function() {
  async function setUserToken() {
    try {
      await bwell.setUserToken('${userToken}');
      console.log('User token set successfully');
    } catch (e) {
      var errorMsg = e.message || String(e);
      if (errorMsg.length > 500) {
        errorMsg = errorMsg.substring(0, 500) + '...';
      }
      document.body.innerHTML = '<div style="font-family: monospace; padding: 24px; background: #fff3f3; color: #d32f2f;">' +
        '<h3 style="margin-top: 0;">Error Setting User Token</h3>' +
        '<pre style="white-space: pre-wrap; word-break: break-word; background: #ffebee; padding: 12px; border-radius: 4px;">' +
        errorMsg +
        '</pre></div>';
    }
  }

  if (typeof bwell !== 'undefined' && bwell.isInitialized) {
    setUserToken();
  } else if (typeof bwell !== 'undefined') {
    bwell.once('initialized', setUserToken);
  } else {
    console.error('bwell object not found on window');
  }
})();
true;`;

  function handleLoadEnd() {
    if (!hasInjectedToken.current) {
      webViewRef.current?.injectJavaScript(tokenSetterJs);
      hasInjectedToken.current = true;
    }
  }

  function handleError({nativeEvent}: WebViewErrorEvent) {
    const message = `Error loading page:\n\nURL: ${nativeEvent.url}\nCode: ${nativeEvent.code}\nDescription: ${nativeEvent.description}`;
    setLoadError(message);
  }

  return (
    <SafeAreaView style={styles.container}>
      <WebView
        ref={webViewRef}
        source={{uri: buildUrl()}}
        style={styles.webView}
        javaScriptEnabled={true}
        domStorageEnabled={true}
        onLoadEnd={handleLoadEnd}
        onError={handleError}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  webView: {
    flex: 1,
  },
  errorContainer: {
    flex: 1,
    backgroundColor: '#fff3f3',
  },
  errorContent: {
    padding: 24,
  },
  errorText: {
    fontFamily: 'monospace',
    fontSize: 14,
    color: '#d32f2f',
  },
});
