package com.khatibstudio.gpacalc.ui.admission;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.FragmentAdmissionBinding;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

import java.util.Locale;

public class AdmissionFragment extends Fragment {

    private FragmentAdmissionBinding binding;
    private GpaViewModel viewModel;
    private AdmissionCutoffAdapter cutoffAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdmissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        setupTabs();
        setupEligibilityTab();
        setupScoreTab();
        prefillGpaFromActiveProfile();
    }

    private void setupTabs() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.admission_eligibility));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.admission_score));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.layoutEligibility.setVisibility(View.VISIBLE);
                    binding.layoutScore.setVisibility(View.GONE);
                } else {
                    binding.layoutEligibility.setVisibility(View.GONE);
                    binding.layoutScore.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupEligibilityTab() {
        cutoffAdapter = new AdmissionCutoffAdapter();
        binding.rvEligiblePrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEligiblePrograms.setAdapter(cutoffAdapter);

        viewModel.getEligibleProgramsLive().observe(getViewLifecycleOwner(), cutoffs -> {
            cutoffAdapter.submitList(cutoffs);
            binding.tvNoResults.setVisibility(
                    (cutoffs == null || cutoffs.isEmpty()) ? View.VISIBLE : View.GONE);
        });

        binding.etGpa.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    viewModel.checkEligibility(0.0);
                    return;
                }
                try {
                    double gpa = Double.parseDouble(s.toString().trim());
                    viewModel.checkEligibility(gpa);
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void setupScoreTab() {
        binding.btnCalculateScore.setOnClickListener(v -> {
            try {
                String sscStr = binding.etSscGpa.getText() != null ? binding.etSscGpa.getText().toString().trim() : "";
                String hscStr = binding.etHscGpa.getText() != null ? binding.etHscGpa.getText().toString().trim() : "";
                if (sscStr.isEmpty() || hscStr.isEmpty()) return;

                double ssc     = Double.parseDouble(sscStr);
                double hsc     = Double.parseDouble(hscStr);
                double written = parseOrZero(binding.etWrittenScore.getText() != null ? binding.etWrittenScore.getText().toString().trim() : "");
                double sscW    = parseOrDefault(binding.etSscWeight.getText() != null ? binding.etSscWeight.getText().toString().trim() : "", 5.0);
                double hscW    = parseOrDefault(binding.etHscWeight.getText() != null ? binding.etHscWeight.getText().toString().trim() : "", 5.0);
                double wrtW    = parseOrDefault(binding.etWrittenWeight.getText() != null ? binding.etWrittenWeight.getText().toString().trim() : "", 0.0);
                viewModel.calculateAdmissionScore(ssc, sscW, hsc, hscW, written, wrtW);
            } catch (NumberFormatException ignored) {}
        });

        viewModel.getAdmissionScoreResult().observe(getViewLifecycleOwner(), score -> {
            if (score != null) {
                binding.tvScoreResult.setVisibility(View.VISIBLE);
                binding.tvScoreResult.setText(
                        getString(R.string.total_score) + ": " + String.format(Locale.US, "%.2f", score));
            }
        });
    }

    private void prefillGpaFromActiveProfile() {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;

        if (GradingScale.MODE_SCHOOL.equals(profile.activeMode)) {
            viewModel.getSchoolSummaryLive().observe(getViewLifecycleOwner(), summary -> {
                if (summary != null) {
                    if (summary.hasSsc && binding.etSscGpa.getText() != null && binding.etSscGpa.getText().length() == 0) {
                        binding.etSscGpa.setText(String.format(Locale.US, "%.2f", summary.sscGpa));
                    }
                    if (summary.hasHsc && binding.etHscGpa.getText() != null && binding.etHscGpa.getText().length() == 0) {
                        binding.etHscGpa.setText(String.format(Locale.US, "%.2f", summary.hscGpa));
                    }
                    if (binding.etGpa.getText() != null && binding.etGpa.getText().length() == 0) {
                        if (summary.hasSsc && summary.hasHsc) {
                            binding.etGpa.setText(String.format(Locale.US, "%.2f", summary.combined));
                        } else if (summary.hasHsc) {
                            binding.etGpa.setText(String.format(Locale.US, "%.2f", summary.hscGpa));
                        } else if (summary.hasSsc) {
                            binding.etGpa.setText(String.format(Locale.US, "%.2f", summary.sscGpa));
                        }
                    }
                }
            });
            viewModel.loadSchoolSummary(profile.id);
        } else {
            viewModel.getUniversitySummaryLive().observe(getViewLifecycleOwner(), summary -> {
                if (summary != null && summary.cgpa > 0 && binding.etGpa.getText() != null && binding.etGpa.getText().length() == 0) {
                    binding.etGpa.setText(String.format(Locale.US, "%.2f", summary.cgpa));
                }
            });
            int presetId = PreferencesHelper.getActivePresetId(requireContext());
            viewModel.loadUniversitySummary(profile.id, presetId);
        }
    }

    private double parseOrZero(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private double parseOrDefault(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
