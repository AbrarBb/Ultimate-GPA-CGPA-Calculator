package com.khatibstudio.gpacalc.ui.calculator.school;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.databinding.FragmentExamDetailBinding;
import com.khatibstudio.gpacalc.logic.SchoolGpaCalculator;
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.ui.common.FormDialogHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class ExamDetailFragment extends Fragment {

    private static final String ARG_EXAM_ID = "exam_id";
    private static final String ARG_EXAM_TYPE = "exam_type";

    private FragmentExamDetailBinding binding;
    private GpaViewModel viewModel;
    private SubjectAdapter adapter;
    private int examRecordId;
    private int scaleId;

    public static ExamDetailFragment newInstance(int examRecordId, String examType) {
        ExamDetailFragment fragment = new ExamDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EXAM_ID, examRecordId);
        args.putString(ARG_EXAM_TYPE, examType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExamDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        examRecordId = requireArguments().getInt(ARG_EXAM_ID);
        String examType = requireArguments().getString(ARG_EXAM_TYPE, ExamRecord.TYPE_SSC);
        binding.tvExamTitle.setText(examType);

        ExamRecord byId = GpaRepository.getInstance(requireContext()).getExamRecordById(examRecordId);
        scaleId = byId != null ? byId.scaleId : 1;

        adapter = new SubjectAdapter(viewModel::deleteSubject);
        binding.recyclerSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSubjects.setAdapter(adapter);

        binding.btnAddSubject.setOnClickListener(v ->
                FormDialogHelper.showSubjectDialog(requireContext(), viewModel, examRecordId, scaleId,
                        this::updateLiveGpa));

        viewModel.getSubjects(examRecordId).observe(getViewLifecycleOwner(), subjects -> {
            adapter.submitList(subjects);
            updateLiveGpa();
        });
    }

    private void updateLiveGpa() {
        SchoolGpaCalculator.Result result = viewModel.calculateSchoolGpa(examRecordId);
        binding.tvLiveGpa.setText(String.format("%.2f", result.gpaWithFourth));
        binding.tvLiveGpaDetail.setText(getString(R.string.gpa_without_fourth) + ": "
                + String.format("%.2f", result.gpaWithoutFourth)
                + (result.hasFourthSubject ? "  •  4th subject bonus applied" : ""));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
