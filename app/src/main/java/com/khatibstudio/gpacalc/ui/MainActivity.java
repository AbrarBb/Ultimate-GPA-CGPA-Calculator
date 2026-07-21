package com.khatibstudio.gpacalc.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import androidx.appcompat.widget.PopupMenu;
import com.khatibstudio.gpacalc.R;
import com.khatibstudio.gpacalc.ads.AdManager;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.databinding.ActivityMainBinding;
import com.khatibstudio.gpacalc.ui.admission.AdmissionFragment;
import com.khatibstudio.gpacalc.ui.calculator.CalculatorHostFragment;
import com.khatibstudio.gpacalc.ui.common.FormDialogHelper;
import com.khatibstudio.gpacalc.ui.common.ProfileDialogHelper;
import com.khatibstudio.gpacalc.ui.history.HistoryFragment;
import com.khatibstudio.gpacalc.ui.home.HomeFragment;
import com.khatibstudio.gpacalc.ui.settings.SettingsFragment;
import com.khatibstudio.gpacalc.ui.statistics.StatisticsFragment;
import com.khatibstudio.gpacalc.util.PreferencesHelper;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModel;
import com.khatibstudio.gpacalc.viewmodel.GpaViewModelFactory;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_EXAM_TYPE = "exam_type";

    private ActivityMainBinding binding;
    private GpaViewModel viewModel;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        // Initialize AdMob (uses test IDs — replace before release)
        AdManager.getInstance().initialize(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new GpaViewModelFactory(getApplication()))
                .get(GpaViewModel.class);

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), false);
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
        }

        binding.bottomNav.setOnItemSelectedListener(this::onNavItemSelected);
        binding.fabAdd.setOnClickListener(v -> showFabMenu());

        viewModel.getAllProfiles().observe(this, profiles -> {
            if (profiles == null || profiles.isEmpty()) {
                ProfileDialogHelper.showCreateProfileDialog(this, viewModel, null);
            } else if (PreferencesHelper.getActiveProfileId(this) <= 0) {
                viewModel.setActiveProfileId(profiles.get(0).id);
            }
        });
    }

    private void applyTheme() {
        if (PreferencesHelper.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_calculator) {
            fragment = new CalculatorHostFragment();
        } else if (id == R.id.nav_admission) {
            fragment = new AdmissionFragment();
        } else if (id == R.id.nav_history) {
            fragment = new HistoryFragment();
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        } else {
            return false;
        }
        switchFragment(fragment, false);
        updateFabVisibility(fragment);
        return true;
    }

    public void switchFragment(Fragment fragment, boolean addToBackStack) {
        currentFragment = fragment;
        var tx = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (addToBackStack) tx.addToBackStack(null);
        tx.commit();
        updateFabVisibility(fragment);
    }

    public GpaViewModel getSharedViewModel() {
        return viewModel;
    }

    private void updateFabVisibility(Fragment fragment) {
        Profile profile = viewModel.getActiveProfile();
        boolean showFab = fragment instanceof CalculatorHostFragment && profile != null 
                && GradingScale.MODE_UNIVERSITY.equals(profile.activeMode);
        binding.fabAdd.setVisibility(showFab ? View.VISIBLE : View.GONE);
    }

    private void showFabMenu() {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;

        PopupMenu menu = new PopupMenu(this, binding.fabAdd);
        if (GradingScale.MODE_SCHOOL.equals(profile.activeMode)) {
            menu.getMenu().add(0, 1, 0, R.string.fab_add_ssc);
            menu.getMenu().add(0, 2, 1, R.string.fab_add_hsc);
        } else {
            menu.getMenu().add(0, 3, 0, R.string.fab_add_semester);
        }

        menu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                createExamRecord(com.khatibstudio.gpacalc.data.entity.ExamRecord.TYPE_SSC);
            } else if (item.getItemId() == 2) {
                createExamRecord(com.khatibstudio.gpacalc.data.entity.ExamRecord.TYPE_HSC);
            } else if (item.getItemId() == 3) {
                createSemester(profile);
            }
            return true;
        });
        menu.show();
    }

    private void createExamRecord(String examType) {
        Profile profile = viewModel.getActiveProfile();
        if (profile == null) return;
        if (viewModel.getScalesForModeSync(GradingScale.MODE_SCHOOL).isEmpty()) return;

        com.khatibstudio.gpacalc.data.entity.ExamRecord existing =
                GpaRepositoryAccessor.getExamByType(this, profile.id, examType);
        if (existing != null) {
            openExamDetail(existing.id, examType);
            return;
        }

        FormDialogHelper.showExamDialog(this, viewModel, examType, (group, scaleId) ->
                viewModel.createExamRecord(profile.id, examType, group, scaleId, () -> {
                    com.khatibstudio.gpacalc.data.entity.ExamRecord record =
                            GpaRepositoryAccessor.getExamByType(this, profile.id, examType);
                    if (record != null) openExamDetail(record.id, examType);
                }));
    }

    private void createSemester(Profile profile) {
        FormDialogHelper.showSemesterDialog(this, viewModel, profile.id, () -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (f instanceof CalculatorHostFragment) {
                ((CalculatorHostFragment) f).refreshUniversity();
            }
        });
    }

    private void openExamDetail(int examRecordId, String examType) {
        com.khatibstudio.gpacalc.ui.calculator.school.ExamDetailFragment fragment =
                com.khatibstudio.gpacalc.ui.calculator.school.ExamDetailFragment.newInstance(examRecordId, examType);
        switchFragment(fragment, true);
    }

    public void navigateToCalculator() {
        binding.bottomNav.setSelectedItemId(R.id.nav_calculator);
    }

    public void navigateToAdmission() {
        binding.bottomNav.setSelectedItemId(R.id.nav_admission);
    }

    public void showCreateProfileDialog() {
        ProfileDialogHelper.showCreateProfileDialog(this, viewModel, null);
    }

    public static class GpaRepositoryAccessor {
        public static com.khatibstudio.gpacalc.data.entity.ExamRecord getExamByType(
                android.content.Context context, int profileId, String type) {
            return com.khatibstudio.gpacalc.repository.GpaRepository.getInstance(context)
                    .getExamByType(profileId, type);
        }
    }
}
