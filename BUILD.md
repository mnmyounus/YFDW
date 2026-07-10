# YFDW Build Guide

## Prerequisites

- JDK 17 or later (https://jdk.java.net/17/)
- Android SDK (API level 34)
- Gradle 8.10.2 (auto-downloaded by gradlew)

## Step 1: Generate Signing Keystore (one-time, local)

```bash
cd keystore
keytool -genkeypair -v -keystore release.keystore -alias yfdw \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass yfdw-release -keypass yfdw-release \
  -dname "CN=YFDW, O=MNM YOUNUS"
cd ..
git add keystore/release.keystore
```

This creates a throwaway signing key. Commit `release.keystore` to the repo — it's non-secret, only for signing.

## Step 2: Build Debug APK (for testing)

```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Step 3: Build Release APK

```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

## Step 4: Automated Release via GitHub Actions

Push a version tag to trigger the workflow:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow (`.github/workflows/release.yml`) will:
1. Build `assembleRelease`
2. Sign with the committed keystore
3. Attach APK to a GitHub Release

## Troubleshooting

### "Tor service not found / TorManager fails to start"

The `TorManager` uses reflection to load `org.torproject.jni.TorService` from the
`tor-android` library (0.4.9.8). If Tor broadcasts don't fire, the library may have
different constant names. Check the actual JAR:

```bash
unzip -l ~/.gradle/caches/*/tor-android-0.4.9.8.aar | grep -i torservice
```

Then update `TorManager.kt` broadcast action/extra/status names to match.

### Build fails: "Unsupported class-file format"

Ensure JDK 17+:

```bash
java -version
# Should show: openjdk 17.0.* or similar
```

### APK won't install

- Ensure you're signing with the correct key (see Step 1).
- Check that `app/build.gradle.kts` points to the correct keystore path.
- If reinstalling over an older build, use `adb install -r` to replace.

## Testing the Security Gate

1. **Standard mode**: Download works on any network (VPN not required).
2. **Privacy & Anonymity mode**:
   - Toggle VPN on → downloads proceed.
   - Toggle VPN off, no Tor → kill switch fires, app exits after 3s.
   - Enable Tor (Settings > Tor) → app stays alive, downloads route through Tor.

## Debug Logs

```bash
adb logcat | grep -i yfdw
adb logcat | grep -i tor
adb logcat | grep -i download
```

## Production Deployment

1. **Create a real signing key** (keep private):
   ```bash
   keytool -genkeypair -v -keystore ~/.yfdw-prod.keystore -alias yfdw-prod \
     -keyalg RSA -keysize 4096 -validity 36500 \
     -storepass [strong-password] -keypass [strong-password] \
     -dname "CN=YFDW Production, OU=MNM YOUNUS"
   ```

2. **Update `app/build.gradle.kts`**:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file(System.getenv("HOME") + "/.yfdw-prod.keystore")
           storePassword = System.getenv("YFDW_KEYSTORE_PASSWORD")
           keyAlias = System.getenv("YFDW_KEY_ALIAS")
           keyPassword = System.getenv("YFDW_KEY_PASSWORD")
       }
   }
   ```

3. **Set environment variables** (before building):
   ```bash
   export YFDW_KEYSTORE_PASSWORD="..."
   export YFDW_KEY_ALIAS="yfdw-prod"
   export YFDW_KEY_PASSWORD="..."
   ```

4. **Build & sign**:
   ```bash
   ./gradlew assembleRelease
   ```

## Performance Notes

- **First build** takes ~5–10 minutes (Gradle setup, dependency download).
- **Incremental builds** take ~1–2 minutes.
- **Release build with ProGuard** takes ~3–5 minutes.

Use `--parallel` for faster multi-project builds:

```bash
./gradlew --parallel assembleRelease
```

