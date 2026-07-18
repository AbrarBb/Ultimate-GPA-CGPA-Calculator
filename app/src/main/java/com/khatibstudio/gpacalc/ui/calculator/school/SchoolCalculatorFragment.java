package com.khatibstudio.gpacalc.ui.calculator.school;

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
import com.khatibstudio.gpacalc.databinding.FragmentSchoolCalculatorBinding;
import com.khatibstudio.gpacalc.logic.SchoolGpaCalculator;
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class SchoolCalculatorFragment extends Fragment {

    private FragmentSchoolCalculatorBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSchoolCalculatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        binding.cardSsc.setOnClickListener(v -> openExam(ExamRecord.TYPE_SSC));
        binding.cardHsc.setOnClickListener(v -> openExam(ExamRecord.TYPE_HSC));

        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), id -> refreshSummary());
        refreshSummary();
    }

    private void refreshSummary() {
        com.khatibstudio.gpacalc.data.entity.Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;

        GpaRepository.SchoolSummary summary = viewModel.getSchoolSummary(profile.id);
        if (summary.ssc != null && summary.hasSsc) {
            SchoolGpaCalculator.Result r = viewModel.calculateSchoolGpa(summary.ssc.id);
            binding.tvSscGpa.setText(String.format("%.2f", r.gpaWithFourth));
            binding.tvSscDetail.setText(getString(R.string.gpa_without_fourth) + ": "
                    + String.format("%.2f", r.gpaWithoutFourth));
        } else {
            binding.tvSscGpa.setText("—");
            binding.tvSscDetail.setText(getString(R.string.no_data));
        }

        if (summary.hsc != null && summary.hasHsc) {
            SchoolGpaCalculator.Result r = viewModel.calculateSchoolGpa(summary.hsc.id);
            binding.tvHscGpa.setText(String.format("%.2f", r.gpaWithFourth));
            binding.tvHscDetail.setText(getString(R.string.gpa_without_fourth) + ": "
                    + String.format("%.2f", r.gpaWithoutFourth));
        } else {
            binding.tvHscGpa.setText("—");
            binding.tvHscDetail.setText(getString(R.string.no_data));
        }

        binding.tvCombinedAvg.setText(summary.hasSsc && summary.hasHsc
                ? String.format("%.2f", summary.combined) : "—");
    }

    private void openExam(String examType) {
        com.khatibstudio.gpacalc.data.entity.Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;
        ExamRecord record = GpaRepository.getInstance(requireContext()).getExamByType(profile.id, examType);
        if (record == null) {
            if (requireActivity() instanceof MainActivity) {
                if (ExamRecord.TYPE_SSC.equals(examType)) {
                    ((MainActivity) requireActivity()).showCreateProfileDialog();
                }
            }
            return;
        }
        ExamDetailFragment fragment = ExamDetailFragment.newInstance(record.id, examType);
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).switchFragment(fragment, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSummary();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
