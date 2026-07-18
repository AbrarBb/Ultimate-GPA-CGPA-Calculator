package com.khatibstudio.gpacalc.ui.calculator.school;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.data.entity.Subject;
import com.khatibstudio.gpacalc.databinding.ItemSubjectBinding;

public class SubjectAdapter extends ListAdapter<Subject, SubjectAdapter.SubjectViewHolder> {

    public interface OnDeleteListener {
        void onDelete(Subject subject);
    }

    private final OnDeleteListener deleteListener;

    public SubjectAdapter(OnDeleteListener deleteListener) {
        super(DIFF);
        this.deleteListener = deleteListener;
    }

    private static final DiffUtil.ItemCallback<Subject> DIFF = new DiffUtil.ItemCallback<Subject>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.subjectName.equals(newItem.subjectName)
                    && oldItem.letterGrade.equals(newItem.letterGrade)
                    && oldItem.isOptionalFourth == newItem.isOptionalFourth;
        }
    };

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSubjectBinding binding = ItemSubjectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final ItemSubjectBinding binding;

        SubjectViewHolder(ItemSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Subject subject) {
            binding.tvSubjectName.setText(subject.subjectName
                    + (subject.isOptionalFourth ? " (4th)" : ""));
            binding.tvSubjectGrade.setText(subject.letterGrade);
            binding.btnDelete.setOnClickListener(v -> deleteListener.onDelete(subject));
        }
    }
}
