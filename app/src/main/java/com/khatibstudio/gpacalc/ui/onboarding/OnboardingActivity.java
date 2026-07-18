package com.khatibstudio.gpacalc.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.databinding.ActivityOnboardingBinding;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.util.PreferencesHelper;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;

    // Page data: emoji, title res, description res
    private static final int[] EMOJIS    = { R.string.onboard_emoji_1, R.string.onboard_emoji_2, R.string.onboard_emoji_3 };
    private static final int[] TITLES    = { R.string.onboard_title_1, R.string.onboard_title_2, R.string.onboard_title_3 };
    private static final int[] DESCS     = { R.string.onboard_desc_1,  R.string.onboard_desc_2,  R.string.onboard_desc_3  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(EMOJIS, TITLES, DESCS);
        binding.viewPager.setAdapter(adapter);

        // Wire dot indicators to ViewPager2
        new TabLayoutMediator(binding.tabIndicator, binding.viewPager,
                (tab, position) -> {}).attach();

        binding.btnNext.setText(R.string.next);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                boolean isLast = position == TITLES.length - 1;
                binding.btnNext.setText(isLast
                        ? R.string.get_started
                        : R.string.next);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < TITLES.length - 1) {
                binding.viewPager.setCurrentItem(current + 1);
            } else {
                finishOnboarding();
            }
        });
    }

    private void finishOnboarding() {
        PreferencesHelper.setOnboardingComplete(this, true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
