package com.khatibstudio.gpacalc.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.khatibstudio.gpacalc.data.AppDatabase;
import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;
import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;
import com.khatibstudio.gpacalc.data.entity.Profile;
import com.khatibstudio.gpacalc.data.entity.Semester;
import com.khatibstudio.gpacalc.data.entity.Subject;
import com.khatibstudio.gpacalc.logic.AdmissionEligibilityChecker;
import com.khatibstudio.gpacalc.logic.SchoolGpaCalculator;
import com.khatibstudio.gpacalc.logic.UniversityGpaCalculator;
import com.khatibstudio.gpacalc.util.GradePointUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GpaRepository {

    private static volatile GpaRepository INSTANCE;
    private final AppDatabase db;

    public static GpaRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GpaRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GpaRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private GpaRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    // -------------------------------------------------------------------------
    // Profile
    // -------------------------------------------------------------------------

    public LiveData<List<Profile>> getAllProfiles() {
        return db.profileDao().getAllProfiles();
    }

    public Profile getProfileById(int id) {
        return db.profileDao().getProfileById(id);
    }

    public void insertProfile(Profile profile, ProfileInsertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = db.profileDao().insertProfile(profile);
            if (callback != null) callback.onInserted((int) id);
        });
    }

    public void updateProfile(Profile profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.profileDao().updateProfile(profile));
    }

    public void deleteProfile(Profile profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.profileDao().deleteProfile(profile));
    }

    // -------------------------------------------------------------------------
    // Grading scales
    // -------------------------------------------------------------------------

    public LiveData<List<GradingScale>> getScalesForMode(String mode) {
        return db.gradingScaleDao().getScalesForMode(mode);
    }

    public List<GradingScale> getScalesForModeSync(String mode) {
        return db.gradingScaleDao().getScalesForModeSync(mode);
    }

    public List<GradePoint> getGradePointsSync(int scaleId) {
        return db.gradingScaleDao().getGradePointsForScaleSync(scaleId);
    }

    public GradingScale getScaleById(int scaleId) {
        return db.gradingScaleDao().getScaleById(scaleId);
    }

    // -------------------------------------------------------------------------
    // Institution presets
    // -------------------------------------------------------------------------

    public LiveData<List<InstitutionPreset>> getAllPresets() {
        return db.institutionPresetDao().getAllPresets();
    }

    public List<InstitutionPreset> getAllPresetsSync() {
        return db.institutionPresetDao().getAllPresetsSync();
    }

    public InstitutionPreset getPresetById(int id) {
        return db.institutionPresetDao().getPresetById(id);
    }

    public InstitutionPreset getPresetByName(String name) {
        return db.institutionPresetDao().getPresetByName(name);
    }

    public void updatePreset(InstitutionPreset preset) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.institutionPresetDao().updatePreset(preset));
    }

    // -------------------------------------------------------------------------
    // School (SSC / HSC)
    // -------------------------------------------------------------------------

    public LiveData<List<ExamRecord>> getExamRecords(int profileId) {
        return db.schoolDao().getExamRecordsForProfile(profileId);
    }

    public ExamRecord getExamByType(int profileId, String type) {
        return db.schoolDao().getExamRecordByType(profileId, type);
    }

    public ExamRecord getExamRecordById(int examRecordId) {
        return db.schoolDao().getExamRecordById(examRecordId);
    }

    public void insertExamRecord(ExamRecord record, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.schoolDao().insertExamRecord(record);
            if (onDone != null) onDone.run();
        });
    }

    public void updateExamRecord(ExamRecord record) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.schoolDao().updateExamRecord(record));
    }

    public void deleteExamRecord(ExamRecord record) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.schoolDao().deleteExamRecord(record));
    }

    public LiveData<List<Subject>> getSubjects(int examRecordId) {
        return db.schoolDao().getSubjectsForExam(examRecordId);
    }

    public List<Subject> getSubjectsSync(int examRecordId) {
        return db.schoolDao().getSubjectsForExamSync(examRecordId);
    }

    public void insertSubject(Subject subject) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.schoolDao().insertSubject(subject));
    }

    public void updateSubject(Subject subject) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.schoolDao().updateSubject(subject));
    }

    public void deleteSubject(Subject subject) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.schoolDao().deleteSubject(subject));
    }

    public SchoolGpaCalculator.Result calculateSchoolGpa(int examRecordId) {
        ExamRecord exam = db.schoolDao().getExamRecordById(examRecordId);
        if (exam == null) return new SchoolGpaCalculator.Result(0, 0, false);
        List<Subject> subjects = getSubjectsSync(examRecordId);
        List<GradePoint> points = getGradePointsSync(exam.scaleId);
        return SchoolGpaCalculator.calculate(subjects, GradePointUtil.toMap(points));
    }

    public SchoolSummary calculateSchoolSummary(int profileId) {
        ExamRecord ssc = db.schoolDao().getExamRecordByType(profileId, ExamRecord.TYPE_SSC);
        ExamRecord hsc = db.schoolDao().getExamRecordByType(profileId, ExamRecord.TYPE_HSC);
        double sscWith = 0, hscWith = 0;
        boolean hasSsc = false, hasHsc = false;
        if (ssc != null && !getSubjectsSync(ssc.id).isEmpty()) {
            sscWith = calculateSchoolGpa(ssc.id).gpaWithFourth;
            hasSsc = true;
        }
        if (hsc != null && !getSubjectsSync(hsc.id).isEmpty()) {
            hscWith = calculateSchoolGpa(hsc.id).gpaWithFourth;
            hasHsc = true;
        }
        double combined = (hasSsc && hasHsc)
                ? SchoolGpaCalculator.combinedAverage(sscWith, hscWith) : 0;
        return new SchoolSummary(ssc, hsc, sscWith, hscWith, combined, hasSsc, hasHsc);
    }

    // -------------------------------------------------------------------------
    // University (semesters / courses)
    // -------------------------------------------------------------------------

    public LiveData<List<Semester>> getSemesters(int profileId) {
        return db.universityDao().getSemestersForProfile(profileId);
    }

    public List<Semester> getSemestersSync(int profileId) {
        return db.universityDao().getSemestersForProfileSync(profileId);
    }

    public Semester getSemesterById(int semesterId) {
        return db.universityDao().getSemesterById(semesterId);
    }

    public void insertSemester(Semester semester, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.universityDao().insertSemester(semester);
            if (onDone != null) onDone.run();
        });
    }

    public void updateSemester(Semester semester) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.universityDao().updateSemester(semester));
    }

    public void deleteSemester(Semester semester) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.universityDao().deleteSemester(semester));
    }

    public LiveData<List<Course>> getCourses(int semesterId) {
        return db.universityDao().getCoursesForSemester(semesterId);
    }

    public List<Course> getCoursesSync(int semesterId) {
        return db.universityDao().getCoursesForSemesterSync(semesterId);
    }

    public void insertCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.universityDao().insertCourse(course));
    }

    public void updateCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.universityDao().updateCourse(course));
    }

    public void deleteCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> db.universityDao().deleteCourse(course));
    }

    public double calculateSemesterGpa(int semesterId) {
        Semester semester = db.universityDao().getSemesterById(semesterId);
        if (semester == null) return 0;
        List<Course> courses = getCoursesSync(semesterId);
        List<GradePoint> points = getGradePointsSync(semester.scaleId);
        return UniversityGpaCalculator.calculateSemesterGpa(courses, GradePointUtil.toMap(points));
    }

    public UniversitySummary calculateUniversitySummary(int profileId, int presetId) {
        List<Course> allCourses = db.universityDao().getAllCoursesForProfileSync(profileId);
        List<Semester> semesters = getSemestersSync(profileId);
        Map<String, GradePoint> mergedMap = buildMergedGradeMap(semesters);
        InstitutionPreset preset = presetId > 0 ? getPresetById(presetId) : null;
        double cgpa = UniversityGpaCalculator.calculateCgpa(allCourses, mergedMap, preset);
        double totalCredits = 0;
        for (Course c : allCourses) totalCredits += c.creditHours;
        return new UniversitySummary(cgpa, totalCredits, semesters.size(), allCourses.size());
    }

    private Map<String, GradePoint> buildMergedGradeMap(List<Semester> semesters) {
        Map<String, GradePoint> mergedMap = new HashMap<>();
        for (Semester s : semesters) {
            mergedMap.putAll(GradePointUtil.toMap(getGradePointsSync(s.scaleId)));
        }
        return mergedMap;
    }

    public double calculateRequiredGpa(int profileId, double targetCgpa,
                                        double remainingCredits, int presetId) {
        List<Course> allCourses = db.universityDao().getAllCoursesForProfileSync(profileId);
        List<Semester> semesters = getSemestersSync(profileId);
        Map<String, GradePoint> mergedMap = buildMergedGradeMap(semesters);
        double currentCredits = 0, currentPoints = 0;
        for (Course c : allCourses) {
            GradePoint gp = mergedMap.get(c.letterGrade);
            if (gp == null) continue;
            currentCredits += c.creditHours;
            currentPoints  += gp.pointValue * c.creditHours;
        }
        return UniversityGpaCalculator.requiredGpaForTarget(
                currentPoints, currentCredits, targetCgpa, remainingCredits);
    }

    // -------------------------------------------------------------------------
    // Admission
    // -------------------------------------------------------------------------

    public LiveData<List<AdmissionCutoff>> getAllCutoffs() {
        return db.profileDao().getAllCutoffs();
    }

    public List<AdmissionCutoff> getEligiblePrograms(double gpa) {
        List<AdmissionCutoff> all = db.profileDao().getEligiblePrograms(gpa);
        return AdmissionEligibilityChecker.getEligiblePrograms(all, gpa);
    }

    /**
     * Calculates the combined admission score using user-supplied weights.
     * Weights default to SSC×5, HSC×5, written×0 when the admission formula
     * specifies only GPA-based selection (no written test weight).
     */
    public double calculateAdmissionScore(double sscGpa, double sscWeight,
                                           double hscGpa, double hscWeight,
                                           double writtenScore, double writtenWeight) {
        return AdmissionEligibilityChecker.combinedAdmissionScore(
                sscGpa, sscWeight, hscGpa, hscWeight, writtenScore, writtenWeight);
    }

    // -------------------------------------------------------------------------
    // Statistics helpers
    // -------------------------------------------------------------------------

    /** Returns semester GPA for each semester in order — used by the trend chart. */
    public List<Double> getSemesterGpaTrend(int profileId) {
        List<Semester> semesters = getSemestersSync(profileId);
        List<Double> trend = new java.util.ArrayList<>();
        for (Semester s : semesters) {
            trend.add(calculateSemesterGpa(s.id));
        }
        return trend;
    }

    public int getRetakeCount(int profileId) {
        List<Course> allCourses = db.universityDao().getAllCoursesForProfileSync(profileId);
        int count = 0;
        for (Course c : allCourses) {
            if (c.isRetake) count++;
        }
        return count;
    }

    // -------------------------------------------------------------------------
    // Callbacks & result classes
    // -------------------------------------------------------------------------

    public interface ProfileInsertCallback {
        void onInserted(int id);
    }

    public static class SchoolSummary {
        public final ExamRecord ssc;
        public final ExamRecord hsc;
        public final double sscGpa;
        public final double hscGpa;
        public final double combined;
        public final boolean hasSsc;
        public final boolean hasHsc;

        public SchoolSummary(ExamRecord ssc, ExamRecord hsc,
                             double sscGpa, double hscGpa,
                             double combined, boolean hasSsc, boolean hasHsc) {
            this.ssc = ssc; this.hsc = hsc;
            this.sscGpa = sscGpa; this.hscGpa = hscGpa;
            this.combined = combined; this.hasSsc = hasSsc; this.hasHsc = hasHsc;
        }
    }

    public static class UniversitySummary {
        public final double cgpa;
        public final double totalCredits;
        public final int semesterCount;
        public final int courseCount;

        public UniversitySummary(double cgpa, double totalCredits,
                                 int semesterCount, int courseCount) {
            this.cgpa = cgpa; this.totalCredits = totalCredits;
            this.semesterCount = semesterCount; this.courseCount = courseCount;
        }
    }
}
