package com.khatibstudio.gpacalc.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.FragmentStatisticsBinding;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private GpaViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                new GpaViewModelFactory(requireActivity().getApplication()))
                .get(GpaViewModel.class);

        setupChart();

        viewModel.getActiveProfileId().observe(getViewLifecycleOwner(), id -> {
            if (id != null && id > 0) loadStats(id);
        });
    }

    private void loadStats(int profileId) {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;

        if (GradingScale.MODE_UNIVERSITY.equals(profile.activeMode)) {
            // Semester trend
            viewModel.loadSemesterTrend(profileId);
            viewModel.getSemesterTrendLive().observe(getViewLifecycleOwner(), trend -> {
                if (trend == null || trend.isEmpty()) {
                    binding.tvNoChartData.setVisibility(View.VISIBLE);
                    binding.chartTrend.setVisibility(View.GONE);
                    binding.tvHighestGpa.setText("—");
                    binding.tvLowestGpa.setText("—");
                    binding.tvAvgGpa.setText("—");
                    return;
                }

                double highest = 0, lowest = 4.00, sum = 0;
                for (double g : trend) {
                    if (g > highest) highest = g;
                    if (g < lowest)  lowest  = g;
                    sum += g;
                }

                binding.tvHighestGpa.setText(String.format("%.2f", highest));
                binding.tvLowestGpa.setText(String.format("%.2f", lowest));
                binding.tvAvgGpa.setText(String.format("%.2f", sum / trend.size()));
                binding.tvNoChartData.setVisibility(View.GONE);
                binding.chartTrend.setVisibility(View.VISIBLE);
                renderLineChart(trend);
            });

            // Semester count + credits
            viewModel.loadUniversitySummary(profileId, -1);
            viewModel.getUniversitySummaryLive().observe(getViewLifecycleOwner(), summary -> {
                if (summary == null) return;
                binding.tvTotalCreditsStat.setText(String.format("%.0f", summary.totalCredits));
                binding.tvSemesterCountStat.setText(String.valueOf(summary.semesterCount));
            });

            // Retake count (runs on DB thread already)
            new Thread(() -> {
                int retakes = viewModel.getRetakeCount(profileId);
                requireActivity().runOnUiThread(() ->
                        binding.tvRetakeCount.setText(String.valueOf(retakes)));
            }).start();
        } else {
            // School mode — show simple SSC vs HSC comparison
            viewModel.getSchoolSummaryLive().observe(getViewLifecycleOwner(), summary -> {
                if (summary == null) return;
                binding.tvHighestGpa.setText(summary.hasSsc || summary.hasHsc
                        ? String.format("%.2f", Math.max(summary.sscGpa, summary.hscGpa)) : "—");
                binding.tvLowestGpa.setText(summary.hasSsc && summary.hasHsc
                        ? String.format("%.2f", Math.min(summary.sscGpa, summary.hscGpa)) : "—");
                binding.tvAvgGpa.setText(summary.hasSsc && summary.hasHsc
                        ? String.format("%.2f", summary.combined) : "—");
            });
            viewModel.loadSchoolSummary(profileId);
            binding.tvTotalCreditsStat.setText("—");
            binding.tvRetakeCount.setText("—");
            binding.tvSemesterCountStat.setText("—");
        }
    }

    private void setupChart() {
        LineChart chart = binding.chartTrend;
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(4.0f);
    }

    private void renderLineChart(List<Double> gpas) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < gpas.size(); i++) {
            entries.add(new Entry(i + 1, gpas.get(i).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.semester_gpa));
        int primaryColor = requireContext().getColor(R.color.primary);
        dataSet.setColor(primaryColor);
        dataSet.setCircleColor(primaryColor);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(primaryColor);
        dataSet.setFillAlpha(30);

        binding.chartTrend.setData(new LineData(dataSet));
        binding.chartTrend.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
