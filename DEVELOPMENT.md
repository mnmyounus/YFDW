# YFDW Development Notes

## Architecture

```
domain/         — pure Kotlin, no Android imports
  model/        — sealed interfaces (SecurityState), data classes
  repository/   — interfaces
  usecase/      — use cases, @Inject constructors

data/           — Android-specific implementations
  security/     — VPN observer, Tor manager, kill switch
  settings/     — DataStore mode persistence
  local/        — Room + SQLCipher encrypted DB
  integrity/    — streaming hash, malware-signature check
  download/     — OkHttp chunk planner, foreground service, engine

presentation/   — Jetpack Compose UI
  theme/        — colors, typography
  navigation/   — NavHost setup
  gate/         — mode picker, security dialogs, ViewModels
  downloads/    — download list, add-sheet, progress
  settings/     — mode settings screen
  MainActivity  — entry point

di/             — Hilt dependency injection modules
```

## Adding a Feature

1. **Define the domain model** in `domain/model/`.
2. **Create a repository interface** in `domain/repository/`.
3. **Implement the repository** in `data/` (e.g., `data/settings/`, `data/download/`).
4. **Create a use case** in `domain/usecase/`.
5. **Inject and use in a ViewModel** in `presentation/`.
6. **Compose UI** in `presentation/screens/`.

## Testing

Currently, there are no unit tests or instrumentation tests in the scaffold.
To add them:

```
app/src/test/kotlin/          — unit tests (local JVM)
app/src/androidTest/kotlin/   — instrumentation tests (device/emulator)
```

Example unit test for a repository:

```kotlin
@RunWith(JUnit4::class)
class OperatingModeRepositoryTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataStore: SettingsDataStore
    private lateinit var repository: OperatingModeRepositoryImpl

    @Before
    fun setup() {
        // Set up test DataStore, etc.
    }

    @Test
    fun testModeIsPersisted() = runBlocking {
        repository.setMode(OperatingMode.PRIVACY_ANONYMITY)
        val mode = repository.mode.first()
        assertEquals(OperatingMode.PRIVACY_ANONYMITY, mode)
    }
}
```

## Common Gradle Commands

```bash
./gradlew clean                    # Remove build artifacts
./gradlew assembleDebug            # Build debug APK
./gradlew assembleRelease          # Build release APK
./gradlew bundle                   # Build AAB (Google Play)
./gradlew connectedAndroidTest     # Run instrumentation tests
./gradlew test                     # Run unit tests
./gradlew lint                     # Static analysis
./gradlew dependency               # Print dependency tree
```

## Debugging

Enable verbose logging in `App.onCreate()`:

```kotlin
if (BuildConfig.DEBUG) {
    android.util.Log.d("YFDW", "Debug mode")
}
```

Inspect network requests via OkHttp logging:

```kotlin
val logging = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
}
client.newBuilder().addInterceptor(logging).build()
```

## Updating Dependencies

Update Gradle plugins and dependencies in `build.gradle.kts`:

```kotlin
id("com.android.application") version "8.5.2"  // Android Gradle Plugin
id("org.jetbrains.kotlin.android") version "1.9.24"  // Kotlin
implementation("androidx.room:room-runtime:2.6.1")  // Room version
```

Always test after updating major versions.

## Proguard/R8 Shrinking

ProGuard rules are in `app/proguard-rules.pro`. If a class disappears after
minification, add a keep rule:

```proguard
-keep class com.mnmyounus.yfdw.data.download.** { *; }
```

Release builds use R8 (ProGuard's successor, built into AGP 8+).

