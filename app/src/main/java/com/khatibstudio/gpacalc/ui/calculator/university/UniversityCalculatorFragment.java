package com.khatibstudio.gpacalc.ui.calculator.university;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.FragmentUniversityCalculatorBinding;
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class UniversityCalculatorFragment extends Fragment {

    private FragmentUniversityCalculatorBinding binding;
    private GpaViewModel viewModel;
    private SemesterAdapter adapter;
    private List<InstitutionPreset> presets = new ArrayList<>();
    private int activePresetId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUniversityCalculatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        adapter = new SemesterAdapter(semester -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).switchFragment(
                        SemesterDetailFragment.newInstance(semester.id, semester.name), true);
            }
        });
        binding.recyclerSemesters.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSemesters.setAdapter(adapter);

        // Load institution presets into the dropdown
        viewModel.getAllPresets().observe(getViewLifecycleOwner(), allPresets -> {
            if (allPresets == null || allPresets.isEmpty()) return;
            presets = allPresets;
            List<String> names = new ArrayList<>();
            for (InstitutionPreset p : allPresets) names.add(p.name);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, names);
            binding.spinnerInstitution.setAdapter(spinnerAdapter);

            // Restore last selected preset
            int savedPresetId = PreferencesHelper.getActivePresetId(requireContext());
            int defaultIndex = 0;
            for (int i = 0; i < allPresets.size(); i++) {
                if (allPresets.get(i).id == savedPresetId) { defaultIndex = i; break; }
            }
            binding.spinnerInstitution.setText(names.get(defaultIndex), false);
            activePresetId = allPresets.get(defaultIndex).id;
            updateRetakeLabel(allPresets.get(defaultIndex));
            bindProfile();
        });

        binding.spinnerInstitution.setOnItemClickListener((parent, v, position, id) -> {
            if (position >= 0 && position < presets.size()) {
                InstitutionPreset selected = presets.get(position);
                activePresetId = selected.id;
                PreferencesHelper.setActivePresetId(requireContext(), activePresetId);
                updateRetakeLabel(selected);
                bindProfile();
            }
        });

        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), profileId -> bindProfile());

        viewModel.getUniversitySummaryLive().observe(getViewLifecycleOwner(), summary -> {
            if (summary == null) return;
            binding.tvCgpa.setText(String.format("%.2f", summary.cgpa));
            binding.tvTotalCredits.setText(getString(R.string.total_credits)
                    + ": " + String.format("%.1f", summary.totalCredits));
        });
    }

    private void updateRetakeLabel(InstitutionPreset preset) {
        String ruleLabel;
        switch (preset.retakeRule) {
            case "REPLACE_CONDITIONAL": ruleLabel = "Retake: Conditional Replace (NSU)"; break;
            case "AVERAGE":             ruleLabel = "Retake: Average";                   break;
            default:                    ruleLabel = "Retake: Replace";                   break;
        }
        binding.tvRetakeRule.setText(ruleLabel);
    }

    public void refreshData() {
        bindProfile();
    }

    private void bindProfile() {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;
        viewModel.loadUniversitySummary(profile.id, activePresetId);
        viewModel.getSemesters(profile.id).observe(getViewLifecycleOwner(), semesters ->
                adapter.submitList(semesters, profile.id, viewModel));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
