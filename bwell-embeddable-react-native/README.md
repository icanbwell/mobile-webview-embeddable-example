# b.well Embeddable React Native Example

A minimal bare React Native app that renders the b.well embeddable in a WebView.

## Setup

### 1. Install Dependencies

```bash
npm install
```

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
```bash
# Start the Metro bundler (keep this running)
npm start

# Android — in a separate terminal
npm run android

# iOS — in a separate terminal
npm run ios
```

**IDE:**

*Android Studio:* Open the `android/` folder, sync Gradle, then run the app.

*Xcode:* Open `ios/BwellEmbeddable.xcworkspace` (not `.xcodeproj`) and run on a simulator or device.

## Notes

- After changing `.env`, rebuild the app for changes to take effect (Metro hot-reload does **not** pick up `.env` changes)
- The `.env` file is gitignored by default and should not be committed
- The `true;` at the end of the injected JavaScript is required by `react-native-webview` — removing it will cause silent injection failures
