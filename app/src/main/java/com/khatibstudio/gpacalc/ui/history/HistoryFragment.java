package com.khatibstudio.gpacalc.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.databinding.FragmentHistoryBinding;
import com.khatibstudio.gpacalc.ui.MainActivity;
import com.khatibstudio.gpacalc.ui.calculator.school.ExamDetailFragment;
import com.khatibstudio.gpacalc.ui.calculator.university.SemesterDetailFragment;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private GpaViewModel viewModel;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        adapter = new HistoryAdapter(item -> {
            if (requireActivity() instanceof MainActivity) {
                if (item instanceof ExamRecord) {
                    ExamRecord exam = (ExamRecord) item;
                    ((MainActivity) requireActivity()).switchFragment(
                            ExamDetailFragment.newInstance(exam.id, exam.examType), true);
                } else if (item instanceof Semester) {
                    Semester semester = (Semester) item;
                    ((MainActivity) requireActivity()).switchFragment(
                            SemesterDetailFragment.newInstance(semester.id, semester.name), true);
                }
            }
        });

        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setAdapter(adapter);

        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), id -> loadHistory(id));
    }

    private void loadHistory(Integer profileId) {
        if (profileId == null || profileId <= 0) {
            showEmpty();
            return;
        }
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) { showEmpty(); return; }

        if (GradingScale.MODE_SCHOOL.equals(profile.activeMode)) {
            viewModel.getExamRecords(profileId).observe(getViewLifecycleOwner(), records -> {
                if (records == null || records.isEmpty()) { showEmpty(); return; }
                List<Object> items = new ArrayList<>(records);
                adapter.submitList(items);
                binding.tvEmptyHistory.setVisibility(View.GONE);
            });
        } else {
            viewModel.getSemesters(profileId).observe(getViewLifecycleOwner(), semesters -> {
                if (semesters == null || semesters.isEmpty()) { showEmpty(); return; }
                List<Object> items = new ArrayList<>(semesters);
                adapter.submitList(items);
                binding.tvEmptyHistory.setVisibility(View.GONE);
            });
        }
    }

    private void showEmpty() {
        adapter.submitList(null);
        binding.tvEmptyHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
