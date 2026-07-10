# YFDW ProGuard Rules

# Android & Core
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# SQLCipher — must not be obfuscated; it's a native bridge
-keep class net.sqlcipher.** { *; }
-keepclassmembers class net.sqlcipher.** { *; }

# Tor & Guardian Project
-keep class org.torproject.** { *; }
-keep class info.guardianproject.** { *; }
-keepclassmembers class org.torproject.** { *; }
-keepclassmembers class info.guardianproject.** { *; }

# OkHttp & Square
-keepclasseswithmembers class okhttp3.** { *; }
-keepclasseswithmembers class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# Hilt DI
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <fields>;
}
-keepclasseswithmembers class * {
    @dagger.hilt.android.HiltAndroidApp <fields>;
}
-keepclassmembers,includedescriptorclasses class com.mnmyounus.yfdw.** {
    @dagger.hilt.** *;
    @javax.inject.** *;
}

# Room Database
-keep class androidx.room.** { *; }
-keepclassmembers class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# DataStore
-keep class androidx.datastore.** { *; }
-keepclassmembers class androidx.datastore.** { *; }

# Android Architecture Components
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keepclassmembers class androidx.lifecycle.** { *; }

# Compose
-keepclasseswithmembers class androidx.compose.** {
    public <methods>;
}

# Our app's ViewModel names (some frameworks look these up by string)
-keepclasseswithmembers class com.mnmyounus.yfdw.presentation.**.* {
    public <methods>;
}

# Enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# JSON serialization (if added later)
-keepclassmembers class * {
    *** *_original(...);
}

# Debugging info
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
