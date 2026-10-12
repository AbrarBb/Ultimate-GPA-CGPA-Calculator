package com.khatibstudio.gpacalc.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.khatibstudio.gpacalc.BuildConfig;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.ads.AdManager;
import com.khatibstudio.gpacalc.databinding.FragmentSettingsBinding;
import com.khatibstudio.gpacalc.ui.common.ProfileDialogHelper;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class SettingsFragment extends Fragment {

    // Replace with your actual Play Store package URL before release
    private static final String PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=com.khatibstudio.gpacalc";

    // Replace with your actual privacy policy URL before release
    private static final String PRIVACY_POLICY_URL =
            "https://khatibstudio.github.io/privacy-policy";

    private FragmentSettingsBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        // --- Version string from BuildConfig (auto-updates with every build) ---
        binding.tvVersion.setText(
                getString(R.string.settings_version_format, BuildConfig.VERSION_NAME));

        // --- Dark mode toggle ---
        binding.switchDarkMode.setChecked(PreferencesHelper.isDarkMode(requireContext()));
        binding.switchDarkMode.setOnCheckedChangeListener((btn, checked) -> {
            PreferencesHelper.setDarkMode(requireContext(), checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });

        // --- Manage profiles ---
        binding.rowProfiles.setOnClickListener(v ->
                ProfileDialogHelper.showCreateProfileDialog(
                        requireActivity(), viewModel, null));

        // --- How GPA is calculated ---
        binding.rowHelpGpa.setOnClickListener(v -> showGpaHelpSheet());

        // --- Rate App ---
        binding.rowRateApp.setOnClickListener(v -> openUrl(PLAY_STORE_URL));

        // --- Privacy Policy ---
        binding.rowPrivacy.setOnClickListener(v -> openUrl(PRIVACY_POLICY_URL));

        // --- Banner ad (low-engagement screen) ---
        AdManager.getInstance().loadBanner(requireContext(), binding.adBannerSettings);
    }

    /** Shows a bottom sheet explaining the GPA calculation formula. */
    private void showGpaHelpSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        android.widget.TextView tv = new android.widget.TextView(requireContext());
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        tv.setPadding(pad, pad, pad, pad);
        tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setLineSpacing(0, 1.5f);
        tv.setText(
                "SSC / HSC GPA\n\n" +
                "• Grading scale: A+ = 5.00 | A = 4.00 | A- = 3.50 | B = 3.00 | C = 2.00 | D = 1.00 | F = 0\n" +
                "• GPA (without 4th subject) = Sum of grade points ÷ Number of compulsory subjects\n" +
                "• If you have an optional 4th subject and its grade point > 2.00:\n" +
                "  Bonus = (4th subject GP − 2.00)\n" +
                "  Final GPA = GPA + Bonus  (capped at 5.00)\n" +
                "• Any 'F' in a compulsory subject = overall FAIL (GPA = 0)\n\n" +
                "University CGPA\n\n" +
                "• Semester GPA = Σ(Grade Point × Credit Hours) ÷ Σ(Credit Hours)\n" +
                "• CGPA = Weighted average across all semesters after resolving retakes\n" +
                "• Retake rules depend on your institution:\n" +
                "  — Replace: new grade replaces old, credit counted once\n" +
                "  — Conditional Replace: only if original grade is eligible\n" +
                "  — Average: average of all attempts\n\n" +
                "Target / Required GPA\n\n" +
                "Required GPA = (Target CGPA × Total Credits − Current Points) ÷ Remaining Credits"
        );
        sheet.setContentView(tv);
        sheet.show();
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
