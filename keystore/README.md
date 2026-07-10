Generate the release keystore once, locally, and commit the resulting
`release.keystore` file into this folder:

    keytool -genkeypair -v -keystore keystore/release.keystore -alias yfdw \
      -keyalg RSA -keysize 2048 -validity 10000 \
      -storepass yfdw-release -keypass yfdw-release \
      -dname "CN=YFDW, O=MNM YOUNUS"

This is a throwaway, non-secret signing key — its only job is making every
build installable and updatable without wiring up GitHub Secrets. If you
ever want a "real" production key instead, swap this file and the
passwords in app/build.gradle.kts and keep the new keystore private.
