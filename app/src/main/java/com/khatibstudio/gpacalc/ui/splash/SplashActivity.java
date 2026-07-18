package com.khatibstudio.gpacalc.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.databinding.ActivitySplashBinding;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.ui.onboarding.OnboardingActivity;
import com.khatibstudio.gpacalc.util.PreferencesHelper;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        if (PreferencesHelper.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fade-in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(700);
        binding.tvAppName.startAnimation(fadeIn);
        binding.tvTagline.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Class<?> destination = PreferencesHelper.isOnboardingComplete(this)
                    ? MainActivity.class
                    : OnboardingActivity.class;
            startActivity(new Intent(this, destination));
            finish();
        }, SPLASH_DELAY_MS);
    }
}
