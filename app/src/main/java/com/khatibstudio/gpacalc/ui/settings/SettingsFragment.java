package com.khatibstudio.gpacalc.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.khatibstudio.gpacalc.databinding.FragmentSettingsBinding;
import com.khatibstudio.gpacalc.ui.common.ProfileDialogHelper;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        // Dark mode
        binding.switchDarkMode.setChecked(PreferencesHelper.isDarkMode(requireContext()));
        binding.switchDarkMode.setOnCheckedChangeListener((btn, checked) -> {
            PreferencesHelper.setDarkMode(requireContext(), checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });

        // Manage profiles
        binding.btnManageProfiles.setOnClickListener(v ->
                ProfileDialogHelper.showCreateProfileDialog(
                        requireActivity(), viewModel, null));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
