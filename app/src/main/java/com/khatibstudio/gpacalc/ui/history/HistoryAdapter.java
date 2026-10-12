package com.khatibstudio.gpacalc.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.databinding.ItemHistoryBinding;
import com.khatibstudio.gpacalc.databinding.ItemNativeAdBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * History list adapter supporting two view types:
 *  - VIEW_TYPE_CONTENT (0): regular ExamRecord / Semester cards
 *  - VIEW_TYPE_AD     (1): native ad placeholder inserted every AD_INTERVAL items
 *
 * Native ads appear at positions 5, 11, 17… (every AD_INTERVAL content items).
 * The ad slot occupies its own index, so the insertion pattern is:
 *   0-4  → content, 5 → ad, 6-10 → content, 11 → ad, …
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CONTENT = 0;
    private static final int VIEW_TYPE_AD      = 1;

    /** One native ad after every this many content items. */
    private static final int AD_INTERVAL = 5;

    /** Sentinel object inserted into the mixed list to mark ad slots. */
    private static final Object AD_PLACEHOLDER = new Object();

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    private final OnItemClickListener listener;

    /** Mixed list containing content objects interleaved with AD_PLACEHOLDER sentinels. */
    private List<Object> mixedItems = new ArrayList<>();

    /** The loaded NativeAd. May be null if not yet loaded or failed. */
    private NativeAd nativeAd;

    public HistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Set the loaded native ad. Call from HistoryFragment once the ad loads.
     * Triggers a rebind only for the ad slot(s) if the list is already populated.
     */
    public void setNativeAd(NativeAd ad) {
        this.nativeAd = ad;
        // Rebind ad view holders
        for (int i = 0; i < mixedItems.size(); i++) {
            if (mixedItems.get(i) == AD_PLACEHOLDER) {
                notifyItemChanged(i);
            }
        }
    }

    /**
     * Submit a new content list. Inserts AD_PLACEHOLDER sentinels every AD_INTERVAL items.
     */
    public void submitList(List<Object> newItems) {
        mixedItems = buildMixedList(newItems);
        notifyDataSetChanged();
    }

    private List<Object> buildMixedList(List<Object> contentItems) {
        List<Object> mixed = new ArrayList<>();
        if (contentItems == null) return mixed;

        int contentCount = 0;
        for (Object item : contentItems) {
            mixed.add(item);
            contentCount++;
            // Insert an ad placeholder after every AD_INTERVAL content items
            if (contentCount % AD_INTERVAL == 0) {
                mixed.add(AD_PLACEHOLDER);
            }
        }
        return mixed;
    }

    @Override
    public int getItemViewType(int position) {
        return (mixedItems.get(position) == AD_PLACEHOLDER) ? VIEW_TYPE_AD : VIEW_TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_AD) {
            ItemNativeAdBinding adBinding = ItemNativeAdBinding.inflate(inflater, parent, false);
            return new NativeAdViewHolder(adBinding);
        } else {
            ItemHistoryBinding contentBinding = ItemHistoryBinding.inflate(inflater, parent, false);
            return new ContentViewHolder(contentBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NativeAdViewHolder) {
            ((NativeAdViewHolder) holder).bind(nativeAd);
        } else if (holder instanceof ContentViewHolder) {
            Object item = mixedItems.get(position);
            ((ContentViewHolder) holder).bind(item, listener);
        }
    }

    @Override
    public int getItemCount() {
        return mixedItems.size();
    }

    // -------------------------------------------------------------------------
    // Content ViewHolder
    // -------------------------------------------------------------------------

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        final ItemHistoryBinding binding;

        ContentViewHolder(ItemHistoryBinding b) {
            super(b.getRoot());
            this.binding = b;
        }

        void bind(Object item, OnItemClickListener listener) {
            if (item instanceof ExamRecord) {
                ExamRecord exam = (ExamRecord) item;
                binding.tvHistoryTitle.setText(exam.examType + " — " + exam.group);
                binding.tvHistorySubtitle.setText("School record");
            } else if (item instanceof Semester) {
                Semester sem = (Semester) item;
                binding.tvHistoryTitle.setText(sem.name);
                binding.tvHistorySubtitle.setText("Semester");
            }
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    // -------------------------------------------------------------------------
    // Native Ad ViewHolder
    // -------------------------------------------------------------------------

    static class NativeAdViewHolder extends RecyclerView.ViewHolder {
        final ItemNativeAdBinding binding;

        NativeAdViewHolder(ItemNativeAdBinding b) {
            super(b.getRoot());
            this.binding = b;
        }

        void bind(NativeAd ad) {
            NativeAdView adView = binding.nativeAdView;

            if (ad == null) {
                // Ad not loaded yet — hide the whole row
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                return;
            }

            // Restore visibility in case it was hidden previously
            itemView.setVisibility(View.VISIBLE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // Headline
            adView.setHeadlineView(binding.nativeAdHeadline);
            if (ad.getHeadline() != null) {
                binding.nativeAdHeadline.setText(ad.getHeadline());
                binding.nativeAdHeadline.setVisibility(View.VISIBLE);
            } else {
                binding.nativeAdHeadline.setVisibility(View.GONE);
            }

            // Body
            adView.setBodyView(binding.nativeAdBody);
            if (ad.getBody() != null) {
                binding.nativeAdBody.setText(ad.getBody());
                binding.nativeAdBody.setVisibility(View.VISIBLE);
            } else {
                binding.nativeAdBody.setVisibility(View.GONE);
            }

            // Icon
            adView.setIconView(binding.nativeAdIcon);
            if (ad.getIcon() != null) {
                binding.nativeAdIcon.setImageDrawable(ad.getIcon().getDrawable());
                binding.nativeAdIcon.setVisibility(View.VISIBLE);
            } else {
                binding.nativeAdIcon.setVisibility(View.GONE);
            }

            // CTA
            adView.setCallToActionView(binding.nativeAdCta);
            if (ad.getCallToAction() != null) {
                binding.nativeAdCta.setText(ad.getCallToAction());
                binding.nativeAdCta.setVisibility(View.VISIBLE);
            } else {
                binding.nativeAdCta.setVisibility(View.GONE);
            }

            // Register the native ad — must be called last
            adView.setNativeAd(ad);
        }
    }
}
