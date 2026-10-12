# ============================================================
# Room Database
# ============================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}
-dontwarn androidx.room.paging.**

# ============================================================
# App Entities and Models (never obfuscate DB-mapped classes)
# ============================================================
-keep class com.khatibstudio.gpacalc.data.entity.** { *; }
-keep class com.khatibstudio.gpacalc.data.model.** { *; }
-keep class com.khatibstudio.gpacalc.logic.** { *; }

# ============================================================
# AdMob / Google Mobile Ads
# ============================================================
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# ============================================================
# MPAndroidChart
# ============================================================
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# ============================================================
# Material / AndroidX
# ============================================================
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-keep class androidx.** { *; }
-dontwarn androidx.**

# ============================================================
# ViewBinding — keep generated Binding classes
# ============================================================
-keep class com.khatibstudio.gpacalc.databinding.** { *; }

# ============================================================
# Kotlin / Coroutines (if ever added)
# ============================================================
-dontwarn kotlin.**
-dontwarn kotlinx.**

# ============================================================
# General safety rules
# ============================================================
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
