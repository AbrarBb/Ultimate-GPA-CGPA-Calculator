package com.khatibstudio.gpacalc.ui.calculator.university;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.databinding.FragmentTargetGpaBinding;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class TargetGpaFragment extends Fragment {

    private FragmentTargetGpaBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTargetGpaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        binding.btnCalculateTarget.setOnClickListener(v -> {
            String targetStr = binding.etTargetCgpa.getText() != null ? binding.etTargetCgpa.getText().toString().trim() : "";
            String creditsStr = binding.etRemainingCredits.getText() != null ? binding.etRemainingCredits.getText().toString().trim() : "";

            if (targetStr.isEmpty() || creditsStr.isEmpty()) return;

            try {
                double targetCgpa = Double.parseDouble(targetStr);
                double remainingCredits = Double.parseDouble(creditsStr);
                int presetId = PreferencesHelper.getActivePresetId(requireContext());
                viewModel.calculateRequiredGpa(targetCgpa, remainingCredits, presetId);
            } catch (NumberFormatException ignored) {
            }
        });

        viewModel.getRequiredGpaResult().observe(getViewLifecycleOwner(), requiredGpa -> {
            if (requiredGpa != null) {
                if (requiredGpa < 0 || requiredGpa > 4.00) {
                    binding.tvRequiredGpa.setText(getString(R.string.required_gpa) + ": " + String.format("%.2f", requiredGpa) + "\n(Unachievable)");
                } else {
                    binding.tvRequiredGpa.setText(getString(R.string.required_gpa) + ": " + String.format("%.2f", requiredGpa));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
