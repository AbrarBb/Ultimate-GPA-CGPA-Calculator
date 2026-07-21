package com.khatibstudio.gpacalc.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;
import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.data.entity.Subject;
import com.khatibstudio.gpacalc.logic.SchoolGpaCalculator;
import com.khatibstudio.gpacalc.repository.GpaRepository;
import com.khatibstudio.gpacalc.util.PreferencesHelper;

import java.util.List;

public class GpaViewModel extends AndroidViewModel {

    private final GpaRepository repository;
    private final MutableLiveData<Integer> activeProfileId  = new MutableLiveData<>();
    private final MutableLiveData<Double>  requiredGpaResult  = new MutableLiveData<>();
    private final MutableLiveData<Double>  admissionScoreResult = new MutableLiveData<>();
    private final MutableLiveData<List<AdmissionCutoff>> eligiblePrograms = new MutableLiveData<>();
    private final MutableLiveData<GpaRepository.UniversitySummary> universitySummaryLive = new MutableLiveData<>();
    private final MutableLiveData<GpaRepository.SchoolSummary> schoolSummaryLive = new MutableLiveData<>();
    private final MutableLiveData<List<Double>> semesterTrendLive = new MutableLiveData<>();

    public GpaViewModel(@NonNull Application application) {
        super(application);
        repository = GpaRepository.getInstance(application);
        activeProfileId.setValue(PreferencesHelper.getActiveProfileId(application));
    }

    // -------------------------------------------------------------------------
    // Profile
    // -------------------------------------------------------------------------

    public LiveData<List<Profile>> getAllProfiles() {
        return repository.getAllProfiles();
    }

    public LiveData<Integer> getActiveProfileId() {
        return activeProfileId;
    }

    public Profile getActiveProfile() {
        Integer id = activeProfileId.getValue();
        if (id == null || id <= 0) return null;
        return repository.getProfileById(id);
    }

    public void setActiveProfileId(int profileId) {
        PreferencesHelper.setActiveProfileId(getApplication(), profileId);
        activeProfileId.setValue(profileId);
    }

    public void updateProfile(Profile profile) {
        repository.updateProfile(profile);
    }

    public void createProfile(String name, String mode, Runnable onDone) {
        createProfile(name, mode, null, null, onDone);
    }

    public void createProfile(String name, String mode, String degreeType, String major, Runnable onDone) {
        Profile profile = new Profile(name, mode);
        profile.degreeType = degreeType;
        profile.major = major;
        repository.insertProfile(profile, id -> {
            if (PreferencesHelper.getActiveProfileId(getApplication()) <= 0) {
                setActiveProfileId(id);
            }
            if (onDone != null) onDone.run();
        });
    }

    public void deleteProfile(Profile profile) {
        repository.deleteProfile(profile);
        if (profile.id == PreferencesHelper.getActiveProfileId(getApplication())) {
            PreferencesHelper.setActiveProfileId(getApplication(), -1);
            activeProfileId.setValue(-1);
        }
    }

    // -------------------------------------------------------------------------
    // Grading scales
    // -------------------------------------------------------------------------

    public LiveData<List<GradingScale>> getScalesForMode(String mode) {
        return repository.getScalesForMode(mode);
    }

    public List<GradingScale> getScalesForModeSync(String mode) {
        return repository.getScalesForModeSync(mode);
    }

    public List<GradePoint> getGradePointsSync(int scaleId) {
        return repository.getGradePointsSync(scaleId);
    }

    // -------------------------------------------------------------------------
    // Institution presets
    // -------------------------------------------------------------------------

    public LiveData<List<InstitutionPreset>> getAllPresets() {
        return repository.getAllPresets();
    }

    public List<InstitutionPreset> getAllPresetsSync() {
        return repository.getAllPresetsSync();
    }

    public InstitutionPreset getPresetById(int id) {
        return repository.getPresetById(id);
    }

    public void updatePreset(InstitutionPreset preset) {
        repository.updatePreset(preset);
    }

    // -------------------------------------------------------------------------
    // School (SSC / HSC)
    // -------------------------------------------------------------------------

    public LiveData<List<ExamRecord>> getExamRecords(int profileId) {
        return repository.getExamRecords(profileId);
    }

    public void createExamRecord(int profileId, String examType,
                                  String group, int scaleId, Runnable onDone) {
        repository.insertExamRecord(
                new ExamRecord(examType, group, scaleId, profileId), onDone);
    }

    public void deleteExamRecord(ExamRecord record) {
        repository.deleteExamRecord(record);
    }

    public LiveData<List<Subject>> getSubjects(int examRecordId) {
        return repository.getSubjects(examRecordId);
    }

    public void addSubject(Subject subject) {
        repository.insertSubject(subject);
    }

    public void deleteSubject(Subject subject) {
        repository.deleteSubject(subject);
    }

    public SchoolGpaCalculator.Result calculateSchoolGpa(int examRecordId) {
        return repository.calculateSchoolGpa(examRecordId);
    }

    /** Async — observe getSchoolSummaryLive() for the result. */
    public void loadSchoolSummary(int profileId) {
        runAsync(() -> schoolSummaryLive.postValue(
                repository.calculateSchoolSummary(profileId)));
    }

    public LiveData<GpaRepository.SchoolSummary> getSchoolSummaryLive() {
        return schoolSummaryLive;
    }

    // -------------------------------------------------------------------------
    // University (semesters / courses)
    // -------------------------------------------------------------------------

    public LiveData<List<Semester>> getSemesters(int profileId) {
        return repository.getSemesters(profileId);
    }

    public void addSemester(Semester semester, Runnable onDone) {
        repository.insertSemester(semester, onDone);
    }

    public void deleteSemester(Semester semester) {
        repository.deleteSemester(semester);
    }

    public LiveData<List<Course>> getCourses(int semesterId) {
        return repository.getCourses(semesterId);
    }

    public void addCourse(Course course) {
        repository.insertCourse(course);
    }

    public void updateCourse(Course course) {
        repository.updateCourse(course);
    }

    public void deleteCourse(Course course) {
        repository.deleteCourse(course);
    }

    public double getSemesterGpa(int semesterId) {
        return repository.calculateSemesterGpa(semesterId);
    }

    public int getSemestersSyncCount(int profileId) {
        return repository.getSemestersSync(profileId).size();
    }

    /** Async — observe getUniversitySummaryLive() for the result. */
    public void loadUniversitySummary(int profileId, int presetId) {
        runAsync(() -> universitySummaryLive.postValue(
                repository.calculateUniversitySummary(profileId, presetId)));
    }

    public LiveData<GpaRepository.UniversitySummary> getUniversitySummaryLive() {
        return universitySummaryLive;
    }

    /** Sync shortcut used by the home screen (already on background thread via runAsync). */
    public GpaRepository.UniversitySummary getUniversitySummarySync(int profileId, int presetId) {
        return repository.calculateUniversitySummary(profileId, presetId);
    }

    /** Sync school summary — call only from a background thread. */
    public GpaRepository.SchoolSummary getSchoolSummarySync(int profileId) {
        return repository.calculateSchoolSummary(profileId);
    }

    // -------------------------------------------------------------------------
    // Target CGPA calculator
    // -------------------------------------------------------------------------

    public LiveData<Double> getRequiredGpaResult() {
        return requiredGpaResult;
    }

    public void calculateRequiredGpa(double targetCgpa, double remainingCredits, int presetId) {
        Profile profile = getActiveProfile();
        if (profile == null) return;
        runAsync(() -> {
            double result = repository.calculateRequiredGpa(
                    profile.id, targetCgpa, remainingCredits, presetId);
            requiredGpaResult.postValue(result);
        });
    }

    // -------------------------------------------------------------------------
    // Admission
    // -------------------------------------------------------------------------

    public LiveData<List<AdmissionCutoff>> getEligibleProgramsLive() {
        return eligiblePrograms;
    }

    public void checkEligibility(double gpa) {
        runAsync(() -> eligiblePrograms.postValue(repository.getEligiblePrograms(gpa)));
    }

    public LiveData<Double> getAdmissionScoreResult() {
        return admissionScoreResult;
    }

    /** Calculates admission score with user-supplied weights. */
    public void calculateAdmissionScore(double sscGpa, double sscWeight,
                                         double hscGpa, double hscWeight,
                                         double writtenScore, double writtenWeight) {
        admissionScoreResult.setValue(repository.calculateAdmissionScore(
                sscGpa, sscWeight, hscGpa, hscWeight, writtenScore, writtenWeight));
    }

    public LiveData<List<AdmissionCutoff>> getAllCutoffs() {
        return repository.getAllCutoffs();
    }

    // -------------------------------------------------------------------------
    // Statistics
    // -------------------------------------------------------------------------

    public LiveData<List<Double>> getSemesterTrendLive() {
        return semesterTrendLive;
    }

    public void loadSemesterTrend(int profileId) {
        runAsync(() -> semesterTrendLive.postValue(
                repository.getSemesterGpaTrend(profileId)));
    }

    public int getRetakeCount(int profileId) {
        return repository.getRetakeCount(profileId);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void runAsync(Runnable task) {
        com.khatibstudio.gpacalc.data.AppDatabase.databaseWriteExecutor.execute(task);
    }
}
