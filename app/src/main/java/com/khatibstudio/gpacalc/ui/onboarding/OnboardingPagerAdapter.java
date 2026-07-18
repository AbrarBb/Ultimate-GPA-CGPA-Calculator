package com.khatibstudio.gpacalc.ui.onboarding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khatibstudio.gpacalc.databinding.ItemOnboardingPageBinding;

public class OnboardingPagerAdapter
        extends RecyclerView.Adapter<OnboardingPagerAdapter.PageViewHolder> {

    private final int[] emojis;
    private final int[] titles;
    private final int[] descriptions;

    public OnboardingPagerAdapter(int[] emojis, int[] titles, int[] descriptions) {
        this.emojis       = emojis;
        this.titles       = titles;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PageViewHolder(ItemOnboardingPageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.binding.tvEmoji.setText(emojis[position]);
        holder.binding.tvTitle.setText(titles[position]);
        holder.binding.tvDescription.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        final ItemOnboardingPageBinding binding;

        PageViewHolder(ItemOnboardingPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
