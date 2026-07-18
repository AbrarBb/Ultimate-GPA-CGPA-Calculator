package com.khatibstudio.gpacalc.ui.admission;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;
import com.khatibstudio.gpacalc.databinding.ItemAdmissionCutoffBinding;

public class AdmissionCutoffAdapter
        extends ListAdapter<AdmissionCutoff, AdmissionCutoffAdapter.ViewHolder> {

    public AdmissionCutoffAdapter() {
        super(DIFF);
    }

    private static final DiffUtil.ItemCallback<AdmissionCutoff> DIFF =
            new DiffUtil.ItemCallback<AdmissionCutoff>() {
                @Override
                public boolean areItemsTheSame(@NonNull AdmissionCutoff a,
                                               @NonNull AdmissionCutoff b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull AdmissionCutoff a,
                                                  @NonNull AdmissionCutoff b) {
                    return a.universityName.equals(b.universityName)
                            && a.programName.equals(b.programName);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAdmissionCutoffBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdmissionCutoff cutoff = getItem(position);
        holder.binding.tvUniversity.setText(cutoff.universityName);
        holder.binding.tvProgram.setText(cutoff.programName);
        holder.binding.tvMinGpa.setText(
                String.format("Min GPA: %.2f", cutoff.minGpaRequired));
        holder.binding.tvFormula.setText(cutoff.formulaNote);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAdmissionCutoffBinding binding;
        ViewHolder(ItemAdmissionCutoffBinding b) {
            super(b.getRoot());
            this.binding = b;
        }
    }
}
