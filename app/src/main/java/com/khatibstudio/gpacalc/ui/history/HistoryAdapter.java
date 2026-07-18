package com.khatibstudio.gpacalc.ui.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.databinding.ItemHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    private final OnItemClickListener listener;
    private List<Object> items = new ArrayList<>();

    public HistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Object> newItems) {
        items = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = items.get(position);
        if (item instanceof ExamRecord) {
            ExamRecord exam = (ExamRecord) item;
            holder.binding.tvHistoryTitle.setText(exam.examType + " — " + exam.group);
            holder.binding.tvHistorySubtitle.setText("School record");
        } else if (item instanceof Semester) {
            Semester sem = (Semester) item;
            holder.binding.tvHistoryTitle.setText(sem.name);
            holder.binding.tvHistorySubtitle.setText("Semester");
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemHistoryBinding binding;
        ViewHolder(ItemHistoryBinding b) {
            super(b.getRoot());
            this.binding = b;
        }
    }
}
