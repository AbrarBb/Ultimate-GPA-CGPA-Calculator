package com.khatibstudio.gpacalc.ui.calculator.school;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.data.model.SubjectDefinition;
import com.khatibstudio.gpacalc.databinding.ItemBoardSubjectBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView adapter for the auto-loaded subject list.
 * Displays subject names, marks ranges in grade dropdown spinners, and point values on the right.
 */
public class BoardSubjectAdapter extends RecyclerView.Adapter<BoardSubjectAdapter.ViewHolder> {

    /** Bangladesh SSC/HSC GPA 5.00 scale definitions */
    private static final String[] GRADES = {"A+", "A", "A-", "B", "C", "D", "F"};
    private static final double[] POINTS = {5.00, 4.00, 3.50, 3.00, 2.00, 1.00, 0.00};
    private static final String[] MARKS  = {"80-100", "70-79", "60-69", "50-59", "40-49", "33-39", "0-32"};

    private List<SubjectDefinition> subjects;
    private final Map<Integer, String> selectedGrades = new HashMap<>();

    public interface OnGradeChangedListener {
        void onGradeChanged();
    }

    private final OnGradeChangedListener listener;

    public BoardSubjectAdapter(List<SubjectDefinition> subjects, OnGradeChangedListener listener) {
        this.subjects = subjects;
        this.listener = listener;
    }

    public void setSubjects(List<SubjectDefinition> subjects) {
        this.subjects = subjects;
        selectedGrades.clear();
        notifyDataSetChanged();
    }

    /** Returns a map of subject index -> selected letter grade. */
    public Map<Integer, String> getSelectedGrades() {
        return new HashMap<>(selectedGrades);
    }

    /** Clears all selected grades. */
    public void resetGrades() {
        selectedGrades.clear();
        notifyDataSetChanged();
    }

    /** Gets the point value for a letter grade. */
    public static double getPointForGrade(String grade) {
        for (int i = 0; i < GRADES.length; i++) {
            if (GRADES[i].equals(grade)) return POINTS[i];
        }
        return 0.0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBoardSubjectBinding binding = ItemBoardSubjectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(subjects.get(position), position);
    }

    @Override
    public int getItemCount() {
        return subjects != null ? subjects.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBoardSubjectBinding binding;

        ViewHolder(ItemBoardSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SubjectDefinition subject, int position) {
            String displayName = subject.getName();
            if (subject.isOptionalFourth()) {
                displayName += " (4th)";
            }
            binding.tvSubjectName.setText(displayName);

            // Grade dropdown showing mark range instead of point value
            String[] gradeLabels = new String[GRADES.length];
            for (int i = 0; i < GRADES.length; i++) {
                gradeLabels[i] = GRADES[i] + " (" + MARKS[i] + ")";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    binding.getRoot().getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    gradeLabels);
            binding.spinnerGrade.setAdapter(adapter);

            // Restore previous selection
            String existingGrade = selectedGrades.get(position);
            if (existingGrade != null) {
                for (int i = 0; i < GRADES.length; i++) {
                    if (GRADES[i].equals(existingGrade)) {
                        binding.spinnerGrade.setText(gradeLabels[i], false);
                        binding.tvGradePoint.setText(String.format("%.2f", POINTS[i]));
                        break;
                    }
                }
            } else {
                binding.spinnerGrade.setText("", false);
                binding.tvGradePoint.setText("");
            }

            // On grade selection
            binding.spinnerGrade.setOnItemClickListener((parent, view, pos, id) -> {
                selectedGrades.put(position, GRADES[pos]);
                binding.tvGradePoint.setText(String.format("%.2f", POINTS[pos]));
                if (listener != null) listener.onGradeChanged();
            });
        }
    }
}
