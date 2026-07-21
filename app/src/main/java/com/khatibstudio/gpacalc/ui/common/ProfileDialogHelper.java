package com.khatibstudio.gpacalc.ui.common;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.DialogProfileBinding;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;

import java.util.Arrays;
import java.util.List;

public final class ProfileDialogHelper {

    private ProfileDialogHelper() {
    }

    public interface ProfileCreatedListener {
        void onProfileCreated();
    }

    public static void showCreateProfileDialog(Context context, GpaViewModel viewModel,
                                                ProfileCreatedListener listener) {
        DialogProfileBinding binding = DialogProfileBinding.inflate(
                android.view.LayoutInflater.from(context));
        binding.tvDialogTitle.setText(R.string.create_profile);

        // Options for majors
        List<String> honorsMajors = Arrays.asList(
                "Management", "Accounting", "Marketing", "Finance & Banking",
                "English", "Economics", "BBA Professional", "CSE"
        );
        List<String> mastersMajors = Arrays.asList(
                "Management", "Accounting", "Marketing", "Finance & Banking",
                "English", "Economics", "MBA Professional"
        );

        // Degree Types spinner setup
        List<String> degreeTypes = Arrays.asList("Honors", "Masters");
        binding.spinnerDegreeType.setAdapter(new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, degreeTypes));
        binding.spinnerDegreeType.setText(degreeTypes.get(0), false);

        // Initialize Major spinner with default (Honors)
        binding.spinnerMajor.setAdapter(new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, honorsMajors));
        binding.spinnerMajor.setText(honorsMajors.get(0), false);

        // Dynamically update Major choices when Degree Type changes
        binding.spinnerDegreeType.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = degreeTypes.get(position);
            List<String> currentMajors = "Masters".equals(selectedType) ? mastersMajors : honorsMajors;
            binding.spinnerMajor.setAdapter(new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line, currentMajors));
            binding.spinnerMajor.setText(currentMajors.get(0), false);
        });

        // Toggle university options visibility based on profile mode selection
        binding.rgProfileMode.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isUniversity = checkedId == R.id.rb_university;
            binding.layoutUniversityOptions.setVisibility(isUniversity ? View.VISIBLE : View.GONE);
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot())
                .setCancelable(false);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        binding.btnCancel.setOnClickListener(v -> {
            if (viewModel.getActiveProfile() == null) {
                // keep dialog open on first launch
                return;
            }
            dialog.dismiss();
        });

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etProfileName.getText() != null
                    ? binding.etProfileName.getText().toString().trim() : "";
            if (name.isEmpty()) {
                binding.etProfileName.setError(context.getString(R.string.profile_name));
                return;
            }

            boolean isUniversity = binding.rbUniversity.isChecked();
            String mode = isUniversity ? GradingScale.MODE_UNIVERSITY : GradingScale.MODE_SCHOOL;
            String degreeType = isUniversity ? binding.spinnerDegreeType.getText().toString() : null;
            String major = isUniversity ? binding.spinnerMajor.getText().toString() : null;

            viewModel.createProfile(name, mode, degreeType, major, () -> {
                dialog.dismiss();
                if (listener != null) listener.onProfileCreated();
            });
        });

        dialog.show();
    }
}
