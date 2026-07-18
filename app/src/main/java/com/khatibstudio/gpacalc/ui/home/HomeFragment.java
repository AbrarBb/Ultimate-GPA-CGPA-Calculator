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
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.FragmentHomeBinding;
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

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

        binding.btnGoCalculator.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).navigateToCalculator();
            }
        });
        binding.btnGoAdmission.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).navigateToAdmission();
            }
        });
        binding.cardStatistics.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).switchFragment(new com.khatibstudio.gpacalc.ui.statistics.StatisticsFragment(), true);
            }
        });

        // Observe school summary (async — no main-thread DB reads)
        viewModel.getSchoolSummaryLive().observe(getViewLifecycleOwner(),
                this::renderSchoolSummary);

        // Observe university summary (async)
        viewModel.getUniversitySummaryLive().observe(getViewLifecycleOwner(),
                this::renderUniversitySummary);

        // Trigger reload when the active profile changes
        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(),
                id -> refreshDashboard());
        viewModel.getAllProfiles().observe(getViewLifecycleOwner(),
                profiles -> refreshDashboard());
    }

    private void refreshDashboard() {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) {
            showEmpty();
            return;
        }
        binding.tvProfileName.setText(profile.name);
        binding.tvProfileMode.setText(GradingScale.MODE_SCHOOL.equals(profile.activeMode)
                ? getString(R.string.mode_school)
                : getString(R.string.mode_university));

        // Trigger async loads — results delivered via LiveData observers above
        if (GradingScale.MODE_SCHOOL.equals(profile.activeMode)) {
            viewModel.loadSchoolSummary(profile.id);
        } else {
            viewModel.loadUniversitySummary(profile.id, -1);
        }
    }

    private void showEmpty() {
        binding.tvProfileName.setText(R.string.no_profile_title);
        binding.tvProfileMode.setText(R.string.no_profile_message);
        binding.tvPrimaryMetric.setText("—");
        binding.tvSecondaryMetric.setVisibility(View.GONE);
        binding.tvStatOne.setText("");
        binding.tvStatTwo.setText("");
    }

    private void renderSchoolSummary(GpaRepository.SchoolSummary summary) {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null || !GradingScale.MODE_SCHOOL.equals(profile.activeMode)) return;

        binding.tvPrimaryMetricLabel.setText(R.string.gpa_with_fourth);
        binding.tvPrimaryMetric.setText(summary.hasHsc || summary.hasSsc
                ? String.format("%.2f", summary.hasHsc ? summary.hscGpa : summary.sscGpa)
                : "—");
        binding.tvSecondaryMetric.setVisibility(View.VISIBLE);
        binding.tvSecondaryMetric.setText(getString(R.string.combined_average) + ": "
                + (summary.hasSsc && summary.hasHsc
                    ? String.format("%.2f", summary.combined) : "—"));
        binding.tvStatOne.setText(getString(R.string.ssc) + ": "
                + (summary.hasSsc ? String.format("%.2f", summary.sscGpa) : "—"));
        binding.tvStatTwo.setText(getString(R.string.hsc) + ": "
                + (summary.hasHsc ? String.format("%.2f", summary.hscGpa) : "—"));
    }

    private void renderUniversitySummary(GpaRepository.UniversitySummary summary) {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null || !GradingScale.MODE_UNIVERSITY.equals(profile.activeMode)) return;

        binding.tvPrimaryMetricLabel.setText(R.string.cgpa);
        binding.tvPrimaryMetric.setText(String.format("%.2f", summary.cgpa));
        binding.tvSecondaryMetric.setVisibility(View.VISIBLE);
        binding.tvSecondaryMetric.setText(getString(R.string.total_credits) + ": "
                + String.format("%.1f", summary.totalCredits));
        binding.tvStatOne.setText(getString(R.string.total_semesters) + ": "
                + summary.semesterCount);
        binding.tvStatTwo.setText(getString(R.string.total_courses) + ": "
                + summary.courseCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
