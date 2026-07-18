package com.khatibstudio.gpacalc.ui.calculator.university;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khatibstudio.gpacalc.databinding.FragmentSemesterDetailBinding;
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.ui.common.FormDialogHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class SemesterDetailFragment extends Fragment {

    private static final String ARG_SEMESTER_ID = "semester_id";
    private static final String ARG_SEMESTER_NAME = "semester_name";

    private FragmentSemesterDetailBinding binding;
    private GpaViewModel viewModel;
    private CourseAdapter adapter;
    private int semesterId;
    private int scaleId;

    public static SemesterDetailFragment newInstance(int semesterId, String semesterName) {
        SemesterDetailFragment fragment = new SemesterDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SEMESTER_ID, semesterId);
        args.putString(ARG_SEMESTER_NAME, semesterName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSemesterDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        semesterId = requireArguments().getInt(ARG_SEMESTER_ID);
        String semesterName = requireArguments().getString(ARG_SEMESTER_NAME, "");
        binding.tvSemesterTitle.setText(semesterName);

        com.khatibstudio.gpacalc.data.entity.Semester semester =
                GpaRepository.getInstance(requireContext()).getSemesterById(semesterId);
        scaleId = semester != null ? semester.scaleId : 1;

        adapter = new CourseAdapter(viewModel::deleteCourse);
        binding.recyclerCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCourses.setAdapter(adapter);

        binding.btnAddCourse.setOnClickListener(v ->
                FormDialogHelper.showCourseDialog(requireContext(), viewModel, semesterId, scaleId,
                        this::updateSemesterGpa));

        viewModel.getCourses(semesterId).observe(getViewLifecycleOwner(), courses -> {
            adapter.submitList(courses);
            updateSemesterGpa();
        });
    }

    private void updateSemesterGpa() {
        double gpa = viewModel.getSemesterGpa(semesterId);
        binding.tvSemesterGpa.setText(String.format("%.2f", gpa));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
