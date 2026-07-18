package com.khatibstudio.gpacalc.ui.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.FragmentCalculatorHostBinding;
import com.khatibstudio.gpacalc.ui.calculator.school.SchoolCalculatorFragment;
import com.khatibstudio.gpacalc.ui.calculator.university.TargetGpaFragment;
import com.khatibstudio.gpacalc.ui.calculator.university.UniversityCalculatorFragment;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class CalculatorHostFragment extends Fragment {

    private FragmentCalculatorHostBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCalculatorHostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);
        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), id -> setupTabs());
        setupTabs();
    }

    private void setupTabs() {
        Profile profile = viewModel.getActiveProfile();
        binding.tabLayout.removeAllTabs();
        if (profile == null) {
            showFragment(new com.khatibstudio.gpacalc.ui.home.HomeFragment());
            return;
        }

        if (GradingScale.MODE_SCHOOL.equals(profile.activeMode)) {
            binding.tabLayout.setVisibility(View.GONE);
            showChild(new SchoolCalculatorFragment());
        } else {
            binding.tabLayout.setVisibility(View.VISIBLE);
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.semesters));
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.target_gpa));
            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) showChild(new UniversityCalculatorFragment());
                    else showChild(new TargetGpaFragment());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            showChild(new UniversityCalculatorFragment());
        }
    }

    private void showChild(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.calculator_container, fragment)
                .commit();
    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void refreshUniversity() {
        Fragment f = getChildFragmentManager().findFragmentById(R.id.calculator_container);
        if (f instanceof UniversityCalculatorFragment) {
            ((UniversityCalculatorFragment) f).refreshData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
