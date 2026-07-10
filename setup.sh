#!/bin/bash
set -e

echo "YFDW Setup Script"
echo "================"

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Install JDK 17+."
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "✓ Found: $JAVA_VERSION"

# Check Android SDK
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️  ANDROID_HOME not set. Trying default locations..."
    if [ -d "$HOME/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Android/sdk"
        echo "✓ Found Android SDK at $ANDROID_HOME"
    else
        echo "❌ Android SDK not found. Set ANDROID_HOME or install Android Studio."
        exit 1
    fi
fi

# Generate keystore if missing
if [ ! -f "keystore/release.keystore" ]; then
    echo ""
    echo "Generating signing keystore (one-time setup)..."
    mkdir -p keystore
    keytool -genkeypair -v -keystore keystore/release.keystore -alias yfdw \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -storepass yfdw-release -keypass yfdw-release \
        -dname "CN=YFDW, O=MNM YOUNUS"
    echo "✓ Keystore created at keystore/release.keystore"
fi

echo ""
echo "Setup complete! Try building:"
echo ""
echo "  Debug:   ./gradlew assembleDebug"
echo "  Release: ./gradlew assembleRelease"
echo ""
echo "For more details, see BUILD.md"
