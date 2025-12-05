# b.well Embeddable Android Example

A minimal Android app that renders the b.well embeddable in a WebView.

## Setup

### 1. Configure Environment Variables

Add the following to your `local.properties` file (in the project root):

```properties
BWELL_ENVIRONMENT=dev
BWELL_CLIENT_ID=your-client-id
CLIENT_USER_TOKEN=your-user-token
INITIAL_PATH=home
```

| Variable | Required | Description |
|----------|----------|-------------|
| `BWELL_ENVIRONMENT` | Yes | One of: `dev`, `staging`, `client-sandbox`, `prod` |
| `BWELL_CLIENT_ID` | Yes | Your b.well client identifier |
| `CLIENT_USER_TOKEN` | Yes | JWT token for user authentication |
| `INITIAL_PATH` | No | Initial route path (defaults to empty) |

### 2. Build and Run

**Android Studio:**
1. Open the project in Android Studio
2. Sync Gradle files (File > Sync Project with Gradle Files)
3. Run the app (Run > Run 'app' or press the play button)

**CLI:**
```bash
# Build and install on connected device/emulator
./gradlew installDebug

# Just build the APK
./gradlew assembleDebug
```

## Notes

- After changing `local.properties`, you must rebuild the app for changes to take effect
- The `local.properties` file is gitignored by default and should not be committed
