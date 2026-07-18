package com.khatibstudio.gpacalc.ui.admission;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.databinding.FragmentAdmissionBinding;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

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

        setupEligibilityTab();
        setupScoreTab();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                binding.layoutEligibility.setVisibility(tab.getPosition() == 0 ? View.VISIBLE : View.GONE);
                binding.layoutScore.setVisibility(tab.getPosition() == 1 ? View.VISIBLE : View.GONE);
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
                if (s.length() == 0) return;
                try {
                    double gpa = Double.parseDouble(s.toString());
                    viewModel.checkEligibility(gpa);
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void setupScoreTab() {
        binding.btnCalculateScore.setOnClickListener(v -> {
            try {
                double ssc     = Double.parseDouble(binding.etSscGpa.getText().toString());
                double hsc     = Double.parseDouble(binding.etHscGpa.getText().toString());
                double written = parseOrZero(binding.etWrittenScore.getText().toString());
                double sscW    = parseOrDefault(binding.etSscWeight.getText().toString(), 5.0);
                double hscW    = parseOrDefault(binding.etHscWeight.getText().toString(), 5.0);
                double wrtW    = parseOrDefault(binding.etWrittenWeight.getText().toString(), 0.0);
                viewModel.calculateAdmissionScore(ssc, sscW, hsc, hscW, written, wrtW);
            } catch (NumberFormatException ignored) {}
        });

        viewModel.getAdmissionScoreResult().observe(getViewLifecycleOwner(), score -> {
            if (score != null) {
                binding.tvScoreResult.setVisibility(View.VISIBLE);
                binding.tvScoreResult.setText(
                        getString(R.string.total_score) + ": " + String.format("%.2f", score));
            }
        });
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
