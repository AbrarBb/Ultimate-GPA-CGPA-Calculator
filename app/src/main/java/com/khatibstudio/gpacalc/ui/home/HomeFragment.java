package com.khatibstudio.gpacalc.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.FragmentHomeBinding;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.ui.calculator.school.BoardGroupSelectorFragment;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        setupGreeting();
        setupCardListeners();

        // Update profile name when it changes
        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), id -> {
            Profile profile = viewModel.getActiveProfile();
            if (profile != null) {
                binding.tvProfileName.setText(profile.name);
            } else {
                binding.tvProfileName.setText(R.string.no_profile_message);
            }
        });
    }

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = getString(R.string.good_morning);
        } else if (hour < 17) {
            greeting = getString(R.string.good_afternoon);
        } else {
            greeting = getString(R.string.good_evening);
        }
        binding.tvGreeting.setText(greeting);
    }

    private void setupCardListeners() {
        // SSC → Group Selector
        binding.cardSsc.setOnClickListener(v ->
                navigateToGroupSelector(ExamRecord.TYPE_SSC));

        // HSC → Group Selector
        binding.cardHsc.setOnClickListener(v ->
                navigateToGroupSelector(ExamRecord.TYPE_HSC));

        // National University
        binding.cardNationalUniversity.setOnClickListener(v ->
                handleUniversitySelection("National University"));

        // Public University
        binding.cardPublicUniversity.setOnClickListener(v ->
                handleUniversitySelection("Dhaka & Jahangirnagar University"));

        // Private University (defaults to BRAC, user can select others in dropdown)
        binding.cardPrivateUniversity.setOnClickListener(v ->
                handleUniversitySelection("BRAC University"));

        // Custom Calculator (defaults to AUST / DIU or custom)
        binding.cardCustom.setOnClickListener(v ->
                handleUniversitySelection("AUST / DIU"));
    }

    private void handleUniversitySelection(String presetName) {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).showCreateProfileDialog();
            }
            return;
        }

        // Change mode to university
        if (!GradingScale.MODE_UNIVERSITY.equals(profile.activeMode)) {
            profile.activeMode = GradingScale.MODE_UNIVERSITY;
            viewModel.updateProfile(profile);
        }

        // Find preset by name and set it active in background thread
        com.khatibstudio.gpacalc.data.AppDatabase.databaseWriteExecutor.execute(() -> {
            List<InstitutionPreset> presets = viewModel.getAllPresetsSync();
            InstitutionPreset target = null;
            for (InstitutionPreset p : presets) {
                if (p.name.equalsIgnoreCase(presetName) || p.name.contains(presetName)) {
                    target = p;
                    break;
                }
            }
            if (target != null) {
                PreferencesHelper.setActivePresetId(requireContext(), target.id);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::navigateToCalculator);
            }
        });
    }

    private void navigateToGroupSelector(String examType) {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).showCreateProfileDialog();
            }
            return;
        }

        // Ensure school mode is active for this profile
        if (!GradingScale.MODE_SCHOOL.equals(profile.activeMode)) {
            profile.activeMode = GradingScale.MODE_SCHOOL;
            viewModel.updateProfile(profile);
        }

        if (requireActivity() instanceof MainActivity) {
            BoardGroupSelectorFragment fragment = BoardGroupSelectorFragment.newInstance(examType);
            ((MainActivity) requireActivity()).switchFragment(fragment, true);
        }
    }

    private void navigateToCalculator() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).navigateToCalculator();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
