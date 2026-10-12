package com.khatibstudio.gpacalc.ads;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * Central AdMob wrapper following the PRD ad strategy:
 *
 * Banner    → Home, History, Statistics, Settings. Never on calculator entry or result screens.
 * Native    → Inserted every 5 cards in the History list.
 * Interstitial → Only after meaningful save actions; minimum 3-minute gap between shows.
 * Rewarded  → Future gated features (PDF export, unlimited scales). Scaffold only for now.
 *
 * ⚠️ Replace all TEST_* IDs with real AdMob unit IDs before publishing.
 */
public final class AdManager {

    private static final String TAG = "AdManager";

    // ---------------------------------------------------------------------------
    // Ad Unit IDs  (test IDs — safe for development, generate no revenue)
    // TODO: Replace with real unit IDs before release
    // ---------------------------------------------------------------------------
    public static final String BANNER_UNIT_ID        = "ca-app-pub-3940256099942544/6300978111";
    public static final String INTERSTITIAL_UNIT_ID  = "ca-app-pub-3940256099942544/1033173712";
    public static final String NATIVE_UNIT_ID        = "ca-app-pub-3940256099942544/2247696110";
    public static final String REWARDED_UNIT_ID      = "ca-app-pub-3940256099942544/5224354917";

    /** Minimum gap between any two interstitials (3 minutes). */
    private static final long INTERSTITIAL_COOLDOWN_MS = 3 * 60 * 1000L;

    private static volatile AdManager INSTANCE;

    private InterstitialAd interstitialAd;
    private RewardedAd     rewardedAd;
    private long           lastInterstitialShownMs = 0;
    private boolean        initialized = false;

    // ---------------------------------------------------------------------------
    // Singleton
    // ---------------------------------------------------------------------------

    public static AdManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AdManager.class) {
                if (INSTANCE == null) INSTANCE = new AdManager();
            }
        }
        return INSTANCE;
    }

    private AdManager() {}

    // ---------------------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------------------

    /** Call once in Application.onCreate() or MainActivity.onCreate(). */
    public void initialize(Context context) {
        if (initialized) return;
        initialized = true;
        MobileAds.initialize(context, status -> Log.d(TAG, "AdMob initialized"));
        preloadInterstitial(context.getApplicationContext());
        preloadRewarded(context.getApplicationContext());
    }

    // ---------------------------------------------------------------------------
    // Banner
    // ---------------------------------------------------------------------------

    /**
     * Creates and loads an adaptive banner into {@code container}.
     * Pass the fragment's ad_banner FrameLayout.
     */
    public void loadBanner(Context context, ViewGroup container) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BANNER_UNIT_ID);
        container.removeAllViews();
        container.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override public void onAdFailedToLoad(@NonNull LoadAdError error) {
                // Hide container so empty space doesn't appear on failure
                container.setVisibility(ViewGroup.GONE);
                Log.w(TAG, "Banner failed: " + error.getMessage());
            }
            @Override public void onAdLoaded() {
                container.setVisibility(ViewGroup.VISIBLE);
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    // ---------------------------------------------------------------------------
    // Native Ad
    // ---------------------------------------------------------------------------

    public interface NativeAdCallback {
        void onLoaded(NativeAd ad);
        void onFailed();
    }

    /**
     * Loads a single native ad for use in the History list.
     * The caller is responsible for destroying the ad when the view is destroyed.
     */
    public void loadNativeAd(Context context, NativeAdCallback callback) {
        AdLoader loader = new AdLoader.Builder(context, NATIVE_UNIT_ID)
                .forNativeAd(callback::onLoaded)
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build())
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        Log.w(TAG, "Native ad failed: " + error.getMessage());
                        callback.onFailed();
                    }
                })
                .build();
        loader.loadAd(new AdRequest.Builder().build());
    }

    // ---------------------------------------------------------------------------
    // Interstitial
    // ---------------------------------------------------------------------------

    private void preloadInterstitial(Context context) {
        InterstitialAd.load(context, INTERSTITIAL_UNIT_ID,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        Log.d(TAG, "Interstitial loaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError err) {
                        interstitialAd = null;
                        Log.w(TAG, "Interstitial failed: " + err.getMessage());
                    }
                });
    }

    /**
     * Shows the interstitial if loaded and cooldown has elapsed.
     * Fire after meaningful save actions ONLY — never mid-flow or on result screens.
     *
     * @param activity foreground Activity (required by AdMob)
     * @param context  application context for preloading the next ad
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

    // ---------------------------------------------------------------------------
    // Rewarded  (scaffold — wire up when PDF export gating is built)
    // ---------------------------------------------------------------------------

    public interface OnRewarded {
        void onUserEarnedReward();
        void onAdNotAvailable();
    }

    private void preloadRewarded(Context context) {
        RewardedAd.load(context, REWARDED_UNIT_ID,
                new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Rewarded loaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError err) {
                        rewardedAd = null;
                        Log.w(TAG, "Rewarded failed: " + err.getMessage());
                    }
                });
    }

    /**
     * Shows the rewarded ad, granting the feature if user watches fully.
     * Call from PDF export, unlimited-scale prompt, etc.
     */
    public void showRewardedAd(Activity activity, Context context, OnRewarded callback) {
        if (rewardedAd == null) {
            callback.onAdNotAvailable();
            return;
        }
        rewardedAd.show(activity, rewardItem -> callback.onUserEarnedReward());
        rewardedAd = null;
        preloadRewarded(context.getApplicationContext());
    }
}
