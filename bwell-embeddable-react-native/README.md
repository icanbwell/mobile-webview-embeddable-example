# b.well Embeddable React Native Example

A minimal bare React Native app that renders the b.well embeddable in a WebView.

## Setup

### 1. Install Dependencies

```bash
npm install
```

This installs React Native, the WebView package, `react-native-config` (for `.env` support), and the React Native CLI toolchain (`@react-native-community/cli`). You may see deprecation warnings from transitive dependencies — these are safe to ignore.

### 2. Configure Environment Variables

Copy `.env.example` to `.env` in the project root and fill in your values:

```bash
cp .env.example .env
```

```properties
BWELL_ENVIRONMENT=dev
BWELL_CLIENT_ID=your-client-id
CLIENT_USER_TOKEN=your-user-token
INITIAL_PATH=home
```

| Variable | Required | Description |
|----------|----------|-------------|
| `BWELL_ENVIRONMENT` | Yes | One of: `dev`, `staging`, `client-sandbox`, `prod` |
| `BWELL_CLIENT_ID` | Yes | Your b.well client identifier (e.g. `acmecorp`) |
| `CLIENT_USER_TOKEN` | Yes | Client token for user authentication |
| `INITIAL_PATH` | No | Initial route path (defaults to empty) |

### 3. iOS — Additional Setup

Install CocoaPods dependencies:

```bash
cd ios && pod install && cd ..
```

Then add the `react-native-config` build phase in Xcode:

1. Open `ios/BwellEmbeddable.xcworkspace` in Xcode
2. Select the **BwellEmbeddable** target → **Build Phases**
3. Click **+** → **New Run Script Phase**
4. Name it **Config** and set the script to:
   ```bash
   "${SRCROOT}/../node_modules/react-native-config/ios/ReactNativeConfig/BuildXCConfig.rb" "${SRCROOT}/.." "${SRCROOT}/tmp.xcconfig"
   ```
5. Drag **Config** above the **Compile Sources** phase

### 4. Build and Run

**CLI:**

Start the Metro bundler first and keep it running:

```bash
npm start
```

When Metro is ready you'll see `info Dev server ready` and an interactive menu (`i` for iOS, `a` for Android). Then in a separate terminal:

```bash
# Android
npm run android

# iOS
npm run ios
```

**IDE:**

*Android Studio:* Open the `android/` folder, sync Gradle, then run the app. Metro must still be running in a terminal.

*Xcode:* Open `ios/BwellEmbeddable.xcworkspace` (not `.xcodeproj`) and run on a simulator or device. Metro must still be running in a terminal.

## Notes

- After changing `.env`, you must do a full rebuild for changes to take effect — Metro hot-reload does **not** pick up `.env` changes
- The `.env` file is gitignored by default and should not be committed
- The `true;` at the end of the injected JavaScript is required by `react-native-webview` — removing it will cause silent injection failures
