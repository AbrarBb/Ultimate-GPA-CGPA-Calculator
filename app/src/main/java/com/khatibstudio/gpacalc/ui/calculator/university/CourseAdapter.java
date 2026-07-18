package com.khatibstudio.gpacalc.ui.calculator.university;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.databinding.ItemCourseBinding;

public class CourseAdapter extends ListAdapter<Course, CourseAdapter.CourseViewHolder> {

    public interface OnDeleteListener {
        void onDelete(Course course);
    }

    private final OnDeleteListener deleteListener;

    public CourseAdapter(OnDeleteListener deleteListener) {
        super(DIFF);
        this.deleteListener = deleteListener;
    }

    private static final DiffUtil.ItemCallback<Course> DIFF = new DiffUtil.ItemCallback<Course>() {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.courseName.equals(newItem.courseName)
                    && oldItem.letterGrade.equals(newItem.letterGrade)
                    && oldItem.creditHours == newItem.creditHours;
        }
    };

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        CourseViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Course course) {
            binding.tvCourseName.setText(course.courseName);
            binding.tvCourseDetail.setText(course.letterGrade + "  •  "
                    + course.creditHours + " cr"
                    + (course.isRetake ? "  •  Retake" : ""));
            binding.btnDelete.setOnClickListener(v -> deleteListener.onDelete(course));
        }
    }
}
