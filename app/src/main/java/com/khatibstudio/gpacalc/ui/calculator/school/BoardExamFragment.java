package com.khatibstudio.gpacalc.ui.calculator.school;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.Subject;
import com.khatibstudio.gpacalc.data.model.SubjectDefinition;
import com.khatibstudio.gpacalc.data.model.SubjectRepository;
import com.khatibstudio.gpacalc.databinding.FragmentBoardExamBinding;
import com.khatibstudio.gpacalc.logic.SchoolGpaCalculator;
import com.khatibstudio.gpacalc.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the auto-loaded subject list for a specific SSC/HSC group.
 * Users dynamically select their 3rd and 4th optional subjects using top dropdowns,
 * which updates the grade-entry list in real time.
 */
public class BoardExamFragment extends Fragment {

    private static final String ARG_EXAM_TYPE = "exam_type";
    private static final String ARG_GROUP = "group";

    private FragmentBoardExamBinding binding;
    private BoardSubjectAdapter adapter;
    private List<SubjectDefinition> compulsorySubjects;
    private List<SubjectDefinition> visibleSubjects;
    private List<String> thirdOptions;
    private List<String> fourthOptions;

    private String examType;
    private String group;

    private String selectedThird = "";
    private String selectedFourth = "";

    public static BoardExamFragment newInstance(String examType, String group) {
        BoardExamFragment fragment = new BoardExamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXAM_TYPE, examType);
        args.putString(ARG_GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBoardExamBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        examType = requireArguments().getString(ARG_EXAM_TYPE, ExamRecord.TYPE_SSC);
        group = requireArguments().getString(ARG_GROUP, ExamRecord.GROUP_SCIENCE);

        // Set header
        binding.tvExamTitle.setText(examType);
        binding.tvGroupName.setText(getGroupDisplayName(group));

        // Load base data
        compulsorySubjects = SubjectRepository.getCompulsorySubjects(examType, group);
        thirdOptions = SubjectRepository.getThirdSubjectOptions(examType, group);
        fourthOptions = SubjectRepository.getFourthSubjectOptions(examType, group);
        visibleSubjects = new ArrayList<>();

        // Hide legacy toggle card (choices card replaces it)
        binding.cardOptional.setVisibility(View.GONE);

        // Setup choices spinners if choices are available
        boolean hasChoices = !thirdOptions.isEmpty() || !fourthOptions.isEmpty();
        if (hasChoices) {
            binding.cardSubjectChoices.setVisibility(View.VISIBLE);
            setupChoices();
        } else {
            binding.cardSubjectChoices.setVisibility(View.GONE);
            rebuildVisibleSubjects();
        }

        // Setup RecyclerView
        adapter = new BoardSubjectAdapter(visibleSubjects, () -> { /* no-op */ });
        binding.recyclerSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSubjects.setAdapter(adapter);

        // Calculate button
        binding.btnCalculate.setOnClickListener(v -> calculateGpa());

        // Reset button
        binding.btnReset.setOnClickListener(v -> {
            adapter.resetGrades();
            if (hasChoices) {
                setupChoices();
            }
        });
    }

    private void setupChoices() {
        // --- 3rd Subject ---
        if (!thirdOptions.isEmpty()) {
            binding.layoutChoiceThird.setVisibility(View.VISIBLE);
            ArrayAdapter<String> thirdAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, thirdOptions);
            binding.spinnerChoiceThird.setAdapter(thirdAdapter);
            binding.spinnerChoiceThird.setText(thirdOptions.get(0), false);
            selectedThird = thirdOptions.get(0);

            binding.spinnerChoiceThird.setOnItemClickListener((parent, view, position, id) -> {
                selectedThird = thirdOptions.get(position);
                updateFourthDropdown();
                rebuildVisibleSubjects();
            });
        } else {
            binding.layoutChoiceThird.setVisibility(View.GONE);
            selectedThird = "";
        }

        // Initialize/Update 4th Subject options
        updateFourthDropdown();
        rebuildVisibleSubjects();
    }

    private void updateFourthDropdown() {
        if (fourthOptions.isEmpty()) {
            binding.layoutChoiceFourth.setVisibility(View.GONE);
            selectedFourth = "";
            return;
        }

        binding.layoutChoiceFourth.setVisibility(View.VISIBLE);

        // Filter: 4th subject options cannot include the chosen 3rd subject
        List<String> filteredFourth = new ArrayList<>();
        for (String option : fourthOptions) {
            if (!option.equals(selectedThird)) {
                filteredFourth.add(option);
            }
        }
        filteredFourth.add("None"); // Add "None" as the first/default option

        ArrayAdapter<String> fourthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, filteredFourth);
        binding.spinnerChoiceFourth.setAdapter(fourthAdapter);

        // Set default to "None" or the first filtered option
        binding.spinnerChoiceFourth.setText("None", false);
        selectedFourth = "None";

        binding.spinnerChoiceFourth.setOnItemClickListener((parent, view, position, id) -> {
            selectedFourth = filteredFourth.get(position);
            rebuildVisibleSubjects();
        });
    }

    private void rebuildVisibleSubjects() {
        visibleSubjects.clear();

        // 1. Add Compulsory core
        visibleSubjects.addAll(compulsorySubjects);

        // 2. Add Selected 3rd subject (acts as a compulsory subject point-wise)
        if (selectedThird != null && !selectedThird.isEmpty()) {
            visibleSubjects.add(new SubjectDefinition(selectedThird, true, false));
        }

        // 3. Add Selected 4th subject (acts as optional)
        if (selectedFourth != null && !selectedFourth.isEmpty() && !"None".equalsIgnoreCase(selectedFourth)) {
            visibleSubjects.add(new SubjectDefinition(selectedFourth, false, true));
        }

        if (adapter != null) {
            adapter.setSubjects(visibleSubjects);
        }
    }

    private void calculateGpa() {
        Map<Integer, String> selectedGrades = adapter.getSelectedGrades();

        // Validate: check all compulsory subjects have grades
        for (int i = 0; i < visibleSubjects.size(); i++) {
            SubjectDefinition sd = visibleSubjects.get(i);
            if (sd.isCompulsory() && !selectedGrades.containsKey(i)) {
                Snackbar.make(binding.getRoot(),
                        getString(R.string.select_grades_all),
                        Snackbar.LENGTH_SHORT).show();
                return;
            }
        }

        // Build Subject entities for the calculator
        List<Subject> subjects = new ArrayList<>();
        Map<String, GradePoint> gradeMap = buildSscHscGradeMap();

        for (int i = 0; i < visibleSubjects.size(); i++) {
            SubjectDefinition sd = visibleSubjects.get(i);
            String grade = selectedGrades.get(i);
            if (grade == null) continue; // skip unselected optional subjects

            Subject subject = new Subject(0, sd.getName(), grade, sd.isOptionalFourth());
            subjects.add(subject);
        }

        // Calculate
        SchoolGpaCalculator.Result result = SchoolGpaCalculator.calculate(subjects, gradeMap);

        // Navigate to GPA Reveal
        navigateToReveal(result);
    }

    private void navigateToReveal(SchoolGpaCalculator.Result result) {
        if (requireActivity() instanceof MainActivity) {
            GpaRevealFragment fragment = GpaRevealFragment.newInstance(
                    examType, group,
                    result.gpaWithFourth, result.gpaWithoutFourth,
                    result.hasFourthSubject, result.isFailed,
                    result.totalSubjects, result.compulsoryCount,
                    result.optionalBonus,
                    result.highestGrade, result.lowestGrade);
            ((MainActivity) requireActivity()).switchFragment(fragment, true);
        }
    }

    private Map<String, GradePoint> buildSscHscGradeMap() {
        Map<String, GradePoint> map = new HashMap<>();
        map.put("A+", new GradePoint(0, "A+", 5.00, 80, 100));
        map.put("A",  new GradePoint(0, "A",  4.00, 70, 79));
        map.put("A-", new GradePoint(0, "A-", 3.50, 60, 69));
        map.put("B",  new GradePoint(0, "B",  3.00, 50, 59));
        map.put("C",  new GradePoint(0, "C",  2.00, 40, 49));
        map.put("D",  new GradePoint(0, "D",  1.00, 33, 39));
        map.put("F",  new GradePoint(0, "F",  0.00, 0,  32));
        return map;
    }

    private String getGroupDisplayName(String group) {
        switch (group) {
            case ExamRecord.GROUP_SCIENCE: return getString(R.string.group_science);
            case ExamRecord.GROUP_BUSINESS_STUDIES: return getString(R.string.group_business_studies);
            case ExamRecord.GROUP_HUMANITIES: return getString(R.string.group_humanities);
            default: return group;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
