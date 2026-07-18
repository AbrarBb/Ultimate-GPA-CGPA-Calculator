package com.khatibstudio.gpacalc.ui.calculator.university;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.databinding.ItemSemesterBinding;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SemesterAdapter extends ListAdapter<Semester, SemesterAdapter.SemesterViewHolder> {

    public interface OnSemesterClickListener {
        void onSemesterClick(Semester semester);
    }

    private final OnSemesterClickListener listener;
    // Single-thread executor for off-thread GPA/count lookups during bind
    private static final ExecutorService bgExecutor = Executors.newSingleThreadExecutor();
    private int profileId;
    private GpaViewModel viewModel;

    public SemesterAdapter(OnSemesterClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    public void submitList(java.util.List<Semester> list, int profileId, GpaViewModel viewModel) {
        this.profileId = profileId;
        this.viewModel = viewModel;
        submitList(list);
    }

    private static final DiffUtil.ItemCallback<Semester> DIFF = new DiffUtil.ItemCallback<Semester>() {
        @Override
        public boolean areItemsTheSame(@NonNull Semester oldItem, @NonNull Semester newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Semester oldItem, @NonNull Semester newItem) {
            return oldItem.name.equals(newItem.name) && oldItem.sortOrder == newItem.sortOrder;
        }
    };

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSemesterBinding binding = ItemSemesterBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SemesterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SemesterViewHolder extends RecyclerView.ViewHolder {
        private final ItemSemesterBinding binding;

        SemesterViewHolder(ItemSemesterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Semester semester) {
            binding.tvSemesterName.setText(semester.name);
            binding.tvSemesterGpa.setText("…");
            binding.tvCourseCount.setText("");
            binding.getRoot().setOnClickListener(v -> listener.onSemesterClick(semester));

            // Load GPA and course count on a background thread to avoid StrictMode violations
            bgExecutor.execute(() -> {
                if (viewModel == null) return;
                double gpa = viewModel.getSemesterGpa(semester.id);
                int count = com.khatibstudio.gpacalc.repository.GpaRepository
                        .getInstance(binding.getRoot().getContext())
                        .getCoursesSync(semester.id).size();
                binding.getRoot().post(() -> {
                    binding.tvSemesterGpa.setText(
                            binding.getRoot().getContext().getString(R.string.semester_gpa)
                                    + ": " + String.format("%.2f", gpa));
                    binding.tvCourseCount.setText(
                            binding.getRoot().getContext().getString(R.string.courses)
                                    + ": " + count);
                });
            });
        }
    }
}
