package com.khatibstudio.gpacalc.ui.calculator.school;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.databinding.FragmentGpaRevealBinding;
import com.khatibstudio.gpacalc.ui.MainActivity;

/**
 * Centered GPA reveal screen.
 * Displays a large GPA number with a count-up animation (the signature moment),
 * pass/fail status pills, and detailed breakdown metadata.
 */
public class GpaRevealFragment extends Fragment {

    private static final String ARG_EXAM_TYPE = "exam_type";
    private static final String ARG_GROUP = "group";
    private static final String ARG_GPA_WITH_FOURTH = "gpa_with_fourth";
    private static final String ARG_GPA_WITHOUT_FOURTH = "gpa_without_fourth";
    private static final String ARG_HAS_FOURTH = "has_fourth";
    private static final String ARG_IS_FAILED = "is_failed";
    private static final String ARG_TOTAL_SUBJECTS = "total_subjects";
    private static final String ARG_COMPULSORY_COUNT = "compulsory_count";
    private static final String ARG_OPTIONAL_BONUS = "optional_bonus";
    private static final String ARG_HIGHEST_GRADE = "highest_grade";
    private static final String ARG_LOWEST_GRADE = "lowest_grade";

    private FragmentGpaRevealBinding binding;

    public static GpaRevealFragment newInstance(String examType, String group,
                                                double gpaWithFourth, double gpaWithoutFourth,
                                                boolean hasFourth, boolean isFailed,
                                                int totalSubjects, int compulsoryCount,
                                                double optionalBonus,
                                                String highestGrade, String lowestGrade) {
        GpaRevealFragment fragment = new GpaRevealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXAM_TYPE, examType);
        args.putString(ARG_GROUP, group);
        args.putDouble(ARG_GPA_WITH_FOURTH, gpaWithFourth);
        args.putDouble(ARG_GPA_WITHOUT_FOURTH, gpaWithoutFourth);
        args.putBoolean(ARG_HAS_FOURTH, hasFourth);
        args.putBoolean(ARG_IS_FAILED, isFailed);
        args.putInt(ARG_TOTAL_SUBJECTS, totalSubjects);
        args.putInt(ARG_COMPULSORY_COUNT, compulsoryCount);
        args.putDouble(ARG_OPTIONAL_BONUS, optionalBonus);
        args.putString(ARG_HIGHEST_GRADE, highestGrade);
        args.putString(ARG_LOWEST_GRADE, lowestGrade);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentGpaRevealBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        String examType = args.getString(ARG_EXAM_TYPE);
        double gpaWithFourth = args.getDouble(ARG_GPA_WITH_FOURTH);
        double gpaWithoutFourth = args.getDouble(ARG_GPA_WITHOUT_FOURTH);
        boolean isFailed = args.getBoolean(ARG_IS_FAILED);

        // Header label
        binding.tvExamLabel.setText(getString(R.string.your_gpa) + " (" + examType + ")");

        // Pass/Fail badge colors and status text
        if (isFailed) {
            binding.tvStatus.setText(R.string.fail);
            binding.cardStatus.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.signal_red)));
        } else {
            binding.tvStatus.setText(R.string.pass);
            binding.cardStatus.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.signal_green)));
        }

        // Subtext / detailed GPA
        if (args.getBoolean(ARG_HAS_FOURTH)) {
            binding.tvGpaWithoutFourth.setText(getString(R.string.gpa_without_fourth) + ": " + String.format("%.2f", gpaWithoutFourth));
            binding.tvGpaWithoutFourth.setVisibility(View.VISIBLE);
        } else {
            binding.tvGpaWithoutFourth.setVisibility(View.GONE);
        }

        // Metadata grid values
        binding.tvTotalSubjects.setText(String.valueOf(args.getInt(ARG_TOTAL_SUBJECTS)));
        double bonus = args.getDouble(ARG_OPTIONAL_BONUS);
        binding.tvOptionalBonus.setText(bonus > 0 ? "+" + String.format("%.2f", bonus) : "—");
        binding.tvHighestGrade.setText(args.getString(ARG_HIGHEST_GRADE));
        binding.tvLowestGrade.setText(args.getString(ARG_LOWEST_GRADE));

        // GPA Count-up Animation
        animateGpa(isFailed ? 0.0 : gpaWithFourth);

        // Save to History button (for now just takes user back to home/history, can be extended to save record)
        binding.btnSaveHistory.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).switchFragment(new com.khatibstudio.gpacalc.ui.home.HomeFragment(), false);
            }
        });

        // Go home button
        binding.btnGoHome.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).switchFragment(new com.khatibstudio.gpacalc.ui.home.HomeFragment(), false);
            }
        });
    }

    private void animateGpa(double targetGpa) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, (float) targetGpa);
        animator.setDuration(600); // 600ms count-up
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            if (binding != null) {
                binding.tvGpaNumber.setText(String.format("%.2f", animatedValue));
            }
        });
        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
