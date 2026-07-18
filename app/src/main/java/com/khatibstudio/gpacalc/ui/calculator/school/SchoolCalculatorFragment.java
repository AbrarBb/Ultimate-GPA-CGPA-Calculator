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
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.ui.common.FormDialogHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class SchoolCalculatorFragment extends Fragment {

    private FragmentSchoolCalculatorBinding binding;
    private GpaViewModel viewModel;
    private GpaRepository.SchoolSummary currentSummary;

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

        viewModel.getSchoolSummaryLive().observe(getViewLifecycleOwner(), summary -> {
            if (summary == null) return;
            currentSummary = summary;

            if (summary.ssc != null && summary.hasSsc) {
                binding.tvSscGpa.setText(String.format("%.2f", summary.sscGpa));
                binding.tvSscDetail.setText(getString(R.string.gpa_without_fourth) + ": "
                        + String.format("%.2f", summary.sscGpaWithoutFourth));
            } else {
                binding.tvSscGpa.setText("—");
                binding.tvSscDetail.setText(getString(R.string.no_data));
            }

            if (summary.hsc != null && summary.hasHsc) {
                binding.tvHscGpa.setText(String.format("%.2f", summary.hscGpa));
                binding.tvHscDetail.setText(getString(R.string.gpa_without_fourth) + ": "
                        + String.format("%.2f", summary.hscGpaWithoutFourth));
            } else {
                binding.tvHscGpa.setText("—");
                binding.tvHscDetail.setText(getString(R.string.no_data));
            }

            binding.tvCombinedAvg.setText(summary.hasSsc && summary.hasHsc
                    ? String.format("%.2f", summary.combined) : "—");
        });

        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), id -> refreshSummary());
        refreshSummary();
    }

    private void refreshSummary() {
        com.khatibstudio.gpacalc.data.entity.Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;
        viewModel.loadSchoolSummary(profile.id);
    }

    private void openExam(String examType) {
        if (currentSummary == null) return;
        ExamRecord record = ExamRecord.TYPE_SSC.equals(examType) ? currentSummary.ssc : currentSummary.hsc;
        if (record == null) {
            FormDialogHelper.showExamDialog(requireContext(), viewModel, examType, (group, scaleId) -> {
                com.khatibstudio.gpacalc.data.entity.Profile profile = viewModel.getActiveProfile();
                if (profile != null) {
                    viewModel.createExamRecord(profile.id, examType, group, scaleId, () -> {
                        ExamRecord newRecord = GpaRepository.getInstance(requireContext())
                                .getExamByType(profile.id, examType);
                        if (newRecord != null) {
                            requireActivity().runOnUiThread(() -> {
                                ExamDetailFragment fragment = ExamDetailFragment.newInstance(newRecord.id, examType);
                                if (requireActivity() instanceof MainActivity) {
                                    ((MainActivity) requireActivity()).switchFragment(fragment, true);
                                }
                            });
                        }
                    });
                }
            });
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
