package com.khatibstudio.gpacalc.ui.common;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.databinding.DialogFormBinding;
import com.khatibstudio.gpacalc.databinding.DialogProfileBinding;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;

import java.util.ArrayList;
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
            String mode = binding.rbUniversity.isChecked()
                    ? GradingScale.MODE_UNIVERSITY : GradingScale.MODE_SCHOOL;
            viewModel.createProfile(name, mode, () -> {
                dialog.dismiss();
                if (listener != null) listener.onProfileCreated();
            });
        });
        dialog.show();
    }
}
