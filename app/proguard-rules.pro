# ProGuard / R8 Rules for Automotive-App

# Keep data models used for JSON serialization and persistence
-keep class com.android.car.automotiveapp.core.model.** { *; }

# Keep Hilt generated classes and injection targets
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends androidx.lifecycle.ViewModel

# Preserve Coroutines and Flow line numbers and stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Timber logging in release
-assumenosideeffects class com.android.car.automotiveapp.core.common.AppLog {
    public static void v(...);
    public static void d(...);
}
