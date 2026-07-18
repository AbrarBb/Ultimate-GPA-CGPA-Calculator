package com.khatibstudio.gpacalc.ads;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Central AdMob wrapper following the PRD ad strategy:
 * - Banner: Home, History, Admission, Statistics, Settings.
 *   Never on calculator entry screens, dialogs, or result screens.
 * - Interstitial: only after meaningful save actions; max once per 3 minutes.
 *
 * IMPORTANT: Replace TEST_* IDs with real AdMob unit IDs before publishing.
 * Test IDs are intentionally safe — they never generate real revenue.
 */
public final class AdManager {

    private static final String TAG = "AdManager";

    // TODO: Replace with real AdMob unit IDs before release
    private static final String TEST_APP_ID        = "ca-app-pub-3940256099942544~3347511713";
    public  static final String BANNER_UNIT_ID     = "ca-app-pub-3940256099942544/6300978111";
    public  static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    /** Minimum gap between interstitial displays, in milliseconds (3 minutes). */
    private static final long INTERSTITIAL_COOLDOWN_MS = 3 * 60 * 1000L;

    private static volatile AdManager INSTANCE;

    private InterstitialAd interstitialAd;
    private long lastInterstitialShownMs = 0;
    private boolean initialized = false;

    public static AdManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AdManager.class) {
                if (INSTANCE == null) INSTANCE = new AdManager();
            }
        }
        return INSTANCE;
    }

    private AdManager() {}

    /** Call once in Application.onCreate() or MainActivity.onCreate(). */
    public void initialize(Context context) {
        if (initialized) return;
        initialized = true;
        MobileAds.initialize(context, initializationStatus ->
                Log.d(TAG, "AdMob initialized"));
        preloadInterstitial(context.getApplicationContext());
    }

    // -------------------------------------------------------------------------
    // Banner
    // -------------------------------------------------------------------------

    /**
     * Creates and loads a banner ad into the given container view.
     * Call this from onViewCreated() of allowed screens.
     */
    public void loadBanner(Context context, ViewGroup container) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BANNER_UNIT_ID);
        container.removeAllViews();
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    // -------------------------------------------------------------------------
    // Interstitial
    // -------------------------------------------------------------------------

    private void preloadInterstitial(Context context) {
        InterstitialAd.load(context, INTERSTITIAL_UNIT_ID,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@androidx.annotation.NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        Log.d(TAG, "Interstitial loaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@androidx.annotation.NonNull LoadAdError err) {
                        interstitialAd = null;
                        Log.w(TAG, "Interstitial failed: " + err.getMessage());
                    }
                });
    }

    /**
     * Shows the interstitial if one is loaded and the cooldown has elapsed.
     * Call after a meaningful save action (save semester, save SSC/HSC result, export).
     *
     * @param activity the foreground Activity (required by AdMob)
     * @param context  application context used to preload the next ad
     */
    public void showInterstitialAfterSave(Activity activity, Context context) {
        long now = SystemClock.elapsedRealtime();
        if (interstitialAd == null || (now - lastInterstitialShownMs) < INTERSTITIAL_COOLDOWN_MS) {
            return;
        }
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                interstitialAd = null;
                preloadInterstitial(context.getApplicationContext());
            }
        });
        interstitialAd.show(activity);
        lastInterstitialShownMs = now;
    }
}
