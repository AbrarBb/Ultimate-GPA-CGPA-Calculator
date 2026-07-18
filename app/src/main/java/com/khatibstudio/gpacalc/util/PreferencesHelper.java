package com.khatibstudio.gpacalc.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.khatibstudio.gpacalc.data.entity.Course;

public final class PreferencesHelper {

    private static final String PREFS              = "gpacalc_prefs";
    private static final String KEY_ACTIVE_PROFILE  = "active_profile_id";
    private static final String KEY_DARK_MODE       = "dark_mode";
    private static final String KEY_RETAKE_RULE     = "retake_rule";
    private static final String KEY_ONBOARDING_DONE = "onboarding_complete";
    private static final String KEY_ACTIVE_PRESET   = "active_preset_id";

    private PreferencesHelper() {}

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // ---- Profile ----

    public static int getActiveProfileId(Context context) {
        return prefs(context).getInt(KEY_ACTIVE_PROFILE, -1);
    }

    public static void setActiveProfileId(Context context, int profileId) {
        prefs(context).edit().putInt(KEY_ACTIVE_PROFILE, profileId).apply();
    }

    // ---- Dark mode ----

    public static boolean isDarkMode(Context context) {
        return prefs(context).getBoolean(KEY_DARK_MODE, false);
    }

    public static void setDarkMode(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    // ---- Retake rule (global fallback) ----

    public static String getRetakeRule(Context context) {
        return prefs(context).getString(KEY_RETAKE_RULE, Course.RETAKE_RULE_REPLACE);
    }

    public static void setRetakeRule(Context context, String rule) {
        prefs(context).edit().putString(KEY_RETAKE_RULE, rule).apply();
    }

    // ---- Onboarding ----

    public static boolean isOnboardingComplete(Context context) {
        return prefs(context).getBoolean(KEY_ONBOARDING_DONE, false);
    }

    public static void setOnboardingComplete(Context context, boolean done) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, done).apply();
    }

    // ---- Institution preset (per-profile, stored globally for simplicity in v1) ----

    public static int getActivePresetId(Context context) {
        return prefs(context).getInt(KEY_ACTIVE_PRESET, -1);
    }

    public static void setActivePresetId(Context context, int presetId) {
        prefs(context).edit().putInt(KEY_ACTIVE_PRESET, presetId).apply();
    }
}
