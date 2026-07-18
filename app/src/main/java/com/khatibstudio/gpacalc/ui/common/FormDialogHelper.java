package com.khatibstudio.gpacalc.ui.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.AppDatabase;
import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.data.entity.Subject;
import com.khatibstudio.gpacalc.databinding.DialogFormBinding;
import com.khatibstudio.gpacalc.logic.SchoolGpaCalculator;
import com.khatibstudio.gpacalc.util.GradePointUtil;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;

import java.util.ArrayList;
import java.util.List;

public final class FormDialogHelper {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private FormDialogHelper() {
    }

    public interface ExamCallback {
        void onSaved(String group, int scaleId);
    }

    public interface SimpleCallback {
        void onSaved();
    }

    public static void showExamDialog(Context context, GpaViewModel viewModel,
                                       String examType, ExamCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<GradingScale> scales = viewModel.getScalesForModeSync(GradingScale.MODE_SCHOOL);
            mainHandler.post(() -> {
                DialogFormBinding binding = DialogFormBinding.inflate(LayoutInflater.from(context));
                binding.tvDialogTitle.setText(examType.equals(ExamRecord.TYPE_SSC) ? R.string.ssc : R.string.hsc);
                binding.layoutName.setVisibility(android.view.View.GONE);
                binding.layoutGroup.setVisibility(android.view.View.VISIBLE);
                binding.layoutScale.setVisibility(android.view.View.VISIBLE);

                String[] groups = {
                        context.getString(R.string.group_science),
                        context.getString(R.string.group_commerce),
                        context.getString(R.string.group_arts)
                };
                binding.spinnerGroup.setAdapter(new ArrayAdapter<>(context,
                        android.R.layout.simple_dropdown_item_1line, groups));

                List<String> scaleNames = new ArrayList<>();
                for (GradingScale s : scales) scaleNames.add(s.name);
                binding.spinnerScale.setAdapter(new ArrayAdapter<>(context,
                        android.R.layout.simple_dropdown_item_1line, scaleNames));
                if (!scaleNames.isEmpty()) binding.spinnerScale.setText(scaleNames.get(0), false);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context).setView(binding.getRoot());
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
                binding.btnSave.setOnClickListener(v -> {
                    String groupLabel = binding.spinnerGroup.getText().toString();
                    String group = mapGroup(groupLabel, context);
                    int scaleIndex = scaleNames.indexOf(binding.spinnerScale.getText().toString());
                    if (scaleIndex < 0) scaleIndex = 0;
                    int scaleId = scales.get(scaleIndex).id;
                    callback.onSaved(group, scaleId);
                    dialog.dismiss();
                });
                dialog.show();
            });
        });
    }

    public static void showSemesterDialog(Context context, GpaViewModel viewModel,
                                              int profileId, SimpleCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<GradingScale> scales = viewModel.getScalesForModeSync(GradingScale.MODE_UNIVERSITY);
            int sortOrder = viewModel.getSemestersSyncCount(profileId);
            mainHandler.post(() -> {
                DialogFormBinding binding = DialogFormBinding.inflate(LayoutInflater.from(context));
                binding.tvDialogTitle.setText(R.string.add_semester);
                binding.layoutName.setHint(context.getString(R.string.semester_name));
                binding.layoutScale.setVisibility(android.view.View.VISIBLE);

                List<String> scaleNames = new ArrayList<>();
                for (GradingScale s : scales) scaleNames.add(s.name);
                binding.spinnerScale.setAdapter(new ArrayAdapter<>(context,
                        android.R.layout.simple_dropdown_item_1line, scaleNames));
                if (!scaleNames.isEmpty()) binding.spinnerScale.setText(scaleNames.get(0), false);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context).setView(binding.getRoot());
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
                binding.btnSave.setOnClickListener(v -> {
                    String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
                    if (name.isEmpty()) return;
                    int scaleIndex = Math.max(0, scaleNames.indexOf(binding.spinnerScale.getText().toString()));
                    Semester semester = new Semester(name, scales.get(scaleIndex).id, profileId, sortOrder);
                    viewModel.addSemester(semester, () -> {
                        if (callback != null) callback.onSaved();
                        dialog.dismiss();
                    });
                });
                dialog.show();
            });
        });
    }

    public static void showSubjectDialog(Context context, GpaViewModel viewModel,
                                          int examRecordId, int scaleId, SimpleCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<GradePoint> points = viewModel.getGradePointsSync(scaleId);
            mainHandler.post(() -> {
                DialogFormBinding binding = DialogFormBinding.inflate(LayoutInflater.from(context));
                binding.tvDialogTitle.setText(R.string.add_subject);
                binding.layoutMarks.setVisibility(android.view.View.VISIBLE);
                binding.layoutGrade.setVisibility(android.view.View.VISIBLE);
                binding.switchFourth.setVisibility(android.view.View.VISIBLE);

                String[] grades = GradePointUtil.gradeLabels(points);
                binding.etGrade.setAdapter(new ArrayAdapter<>(context,
                        android.R.layout.simple_dropdown_item_1line, grades));

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context).setView(binding.getRoot());
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
                binding.btnSave.setOnClickListener(v -> {
                    String subjectName = binding.etName.getText() != null
                            ? binding.etName.getText().toString().trim() : "";
                    if (subjectName.isEmpty()) return;

                    String grade;
                    String marksText = binding.etMarks.getText() != null
                            ? binding.etMarks.getText().toString().trim() : "";
                    if (!marksText.isEmpty()) {
                        try {
                            grade = SchoolGpaCalculator.gradeFromMarks(Double.parseDouble(marksText), points);
                        } catch (NumberFormatException e) {
                            grade = GradePointUtil.extractGrade(binding.etGrade.getText().toString());
                        }
                    } else {
                        grade = GradePointUtil.extractGrade(binding.etGrade.getText().toString());
                    }

                    Subject subject = new Subject(examRecordId, subjectName, grade, binding.switchFourth.isChecked());
                    viewModel.addSubject(subject);
                    if (callback != null) callback.onSaved();
                    dialog.dismiss();
                });
                dialog.show();
            });
        });
    }

    public static void showCourseDialog(Context context, GpaViewModel viewModel,
                                         int semesterId, int scaleId, SimpleCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<GradePoint> points = viewModel.getGradePointsSync(scaleId);
            mainHandler.post(() -> {
                DialogFormBinding binding = DialogFormBinding.inflate(LayoutInflater.from(context));
                binding.tvDialogTitle.setText(R.string.add_course);
                binding.layoutCredits.setVisibility(android.view.View.VISIBLE);
                binding.layoutGrade.setVisibility(android.view.View.VISIBLE);
                binding.switchRetake.setVisibility(android.view.View.VISIBLE);

                String[] grades = GradePointUtil.gradeLabels(points);
                binding.etGrade.setAdapter(new ArrayAdapter<>(context,
                        android.R.layout.simple_dropdown_item_1line, grades));

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context).setView(binding.getRoot());
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
                binding.btnSave.setOnClickListener(v -> {
                    String courseName = binding.etName.getText() != null
                            ? binding.etName.getText().toString().trim() : "";
                    if (courseName.isEmpty()) return;
                    double credits = 3.0;
                    try {
                        credits = Double.parseDouble(binding.etCredits.getText().toString().trim());
                    } catch (Exception ignored) {
                    }
                    String grade = GradePointUtil.extractGrade(binding.etGrade.getText().toString());
                    Course course = new Course(semesterId, courseName, credits, grade,
                            binding.switchRetake.isChecked(), 0);
                    viewModel.addCourse(course);
                    if (callback != null) callback.onSaved();
                    dialog.dismiss();
                });
                dialog.show();
            });
        });
    }

    private static String mapGroup(String label, Context context) {
        if (label.equals(context.getString(R.string.group_commerce))) return ExamRecord.GROUP_COMMERCE;
        if (label.equals(context.getString(R.string.group_arts))) return ExamRecord.GROUP_ARTS;
        return ExamRecord.GROUP_SCIENCE;
    }
}
