package com.khatibstudio.gpacalc.ui.calculator.school;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.model.SubjectRepository;
import com.khatibstudio.gpacalc.databinding.FragmentBoardGroupSelectorBinding;
import com.khatibstudio.gpacalc.ui.MainActivity;

/**
 * Displays the 3 group options (Science, Business Studies, Humanities)
 * after the user selects SSC or HSC from the home screen.
 */
public class BoardGroupSelectorFragment extends Fragment {

    private static final String ARG_EXAM_TYPE = "exam_type";

    private FragmentBoardGroupSelectorBinding binding;
    private String examType;

    public static BoardGroupSelectorFragment newInstance(String examType) {
        BoardGroupSelectorFragment fragment = new BoardGroupSelectorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXAM_TYPE, examType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBoardGroupSelectorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        examType = requireArguments().getString(ARG_EXAM_TYPE, ExamRecord.TYPE_SSC);

        // Set subtitle to exam type
        binding.tvExamType.setText(examType.equals(ExamRecord.TYPE_SSC)
                ? getString(R.string.ssc) : getString(R.string.hsc));

        // Show subject counts per group
        int scienceCount = SubjectRepository.getSubjects(examType, ExamRecord.GROUP_SCIENCE).size();
        int businessCount = SubjectRepository.getSubjects(examType, ExamRecord.GROUP_BUSINESS_STUDIES).size();
        int humanitiesCount = SubjectRepository.getSubjects(examType, ExamRecord.GROUP_HUMANITIES).size();

        binding.tvScienceCount.setText(scienceCount + " " + getString(R.string.subjects).toLowerCase());
        binding.tvBusinessCount.setText(businessCount + " " + getString(R.string.subjects).toLowerCase());
        binding.tvHumanitiesCount.setText(humanitiesCount + " " + getString(R.string.subjects).toLowerCase());

        // Click handlers
        binding.cardScience.setOnClickListener(v ->
                navigateToBoardExam(ExamRecord.GROUP_SCIENCE));
        binding.cardBusiness.setOnClickListener(v ->
                navigateToBoardExam(ExamRecord.GROUP_BUSINESS_STUDIES));
        binding.cardHumanities.setOnClickListener(v ->
                navigateToBoardExam(ExamRecord.GROUP_HUMANITIES));
    }

    private void navigateToBoardExam(String group) {
        if (requireActivity() instanceof MainActivity) {
            BoardExamFragment fragment = BoardExamFragment.newInstance(examType, group);
            ((MainActivity) requireActivity()).switchFragment(fragment, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
