package com.khatibstudio.gpacalc.data;

import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;
import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.GradingScale;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds default grading scales and institution presets into a fresh database.
 *
 * DATA INTEGRITY RULES (UPDATED 2026):
 * - SSC/HSC scale: 5.00 max, official Bangladesh board rules.
 * - National University: 10 grade levels, max 4.00.
 * - Dhaka University / Jahangirnagar University: DU standard 4.00 scale.
 * - BRAC University: 13 grade levels, A+/A both 4.00.
 * - UIU: 4.00 scale, A is 90-100 (4.00).
 * - NSU: 4.00 scale, A is 93-100 (4.00).
 * - EWU: 4.00 scale, A+ is 80%+ (4.00).
 * - AIUB: Corrected monotonic scale (4.00 to 2.25).
 * - IUB: 4.00 scale, A is 90-100 (4.00).
 * - AUST / DIU / IUBAT: UGC 4.00 scale (A+ is 80%+).
 * - ULAB: 9 grade levels, no C- or D+ bands.
 */
public final class DefaultScaleSeeder {

    private DefaultScaleSeeder() {}

    public static void seed(AppDatabase db) {
        long sscHscScaleId = seedSscHscScale(db);
        long nuScaleId     = seedNationalUniversityScale(db);
        long duScaleId     = seedDhakaUniversityScale(db);
        long bracScaleId   = seedBracScale(db);
        long uiuScaleId    = seedUiuScale(db);
        long nsuScaleId    = seedNsuScale(db);
        long ewuScaleId    = seedEwuScale(db);
        long aiubScaleId   = seedAiubScale(db);
        long iubScaleId    = seedIubScale(db);
        long austScaleId   = seedAustDiuScale(db);
        long iubatScaleId  = seedIubatScale(db);
        long ulabScaleId   = seedUlabScale(db);

        seedInstitutionPresets(db,
                (int) nuScaleId,
                (int) duScaleId,
                (int) bracScaleId,
                (int) uiuScaleId,
                (int) nsuScaleId,
                (int) ewuScaleId,
                (int) aiubScaleId,
                (int) iubScaleId,
                (int) austScaleId,
                (int) iubatScaleId,
                (int) ulabScaleId);

        seedAdmissionCutoffs(db);
    }

    // -------------------------------------------------------------------------
    // 1. SSC / HSC
    // -------------------------------------------------------------------------
    private static long seedSscHscScale(AppDatabase db) {
        GradingScale scale = new GradingScale("SSC/HSC (GPA 5.00)", GradingScale.MODE_SCHOOL, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 5.00,  80, 100));
        pts.add(new GradePoint((int) id, "A",  4.00,  70,  79));
        pts.add(new GradePoint((int) id, "A-", 3.50,  60,  69));
        pts.add(new GradePoint((int) id, "B",  3.00,  50,  59));
        pts.add(new GradePoint((int) id, "C",  2.00,  40,  49));
        pts.add(new GradePoint((int) id, "D",  1.00,  33,  39));
        pts.add(new GradePoint((int) id, "F",  0.00,   0,  32));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 2. National University (4.00)
    // -------------------------------------------------------------------------
    private static long seedNationalUniversityScale(AppDatabase db) {
        GradingScale scale = new GradingScale("National University (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 80, 100));
        pts.add(new GradePoint((int) id, "A",  3.75, 75,  79));
        pts.add(new GradePoint((int) id, "A-", 3.50, 70,  74));
        pts.add(new GradePoint((int) id, "B+", 3.25, 65,  69));
        pts.add(new GradePoint((int) id, "B",  3.00, 60,  64));
        pts.add(new GradePoint((int) id, "B-", 2.75, 55,  59));
        pts.add(new GradePoint((int) id, "C+", 2.50, 50,  54));
        pts.add(new GradePoint((int) id, "C",  2.25, 45,  49));
        pts.add(new GradePoint((int) id, "D",  2.00, 40,  44));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  39));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 3. Dhaka University / Jahangirnagar University (4.00)
    // -------------------------------------------------------------------------
    private static long seedDhakaUniversityScale(AppDatabase db) {
        GradingScale scale = new GradingScale("Dhaka / Jahangirnagar University (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 80, 100));
        pts.add(new GradePoint((int) id, "A",  3.75, 75,  79));
        pts.add(new GradePoint((int) id, "A-", 3.50, 70,  74));
        pts.add(new GradePoint((int) id, "B+", 3.25, 65,  69));
        pts.add(new GradePoint((int) id, "B",  3.00, 60,  64));
        pts.add(new GradePoint((int) id, "B-", 2.75, 55,  59));
        pts.add(new GradePoint((int) id, "C+", 2.50, 50,  54));
        pts.add(new GradePoint((int) id, "C",  2.25, 45,  49));
        pts.add(new GradePoint((int) id, "D",  2.00, 40,  44));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  39));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 4. BRAC University (4.00)
    // -------------------------------------------------------------------------
    private static long seedBracScale(AppDatabase db) {
        GradingScale scale = new GradingScale("BRAC University (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 97, 100));
        pts.add(new GradePoint((int) id, "A",  4.00, 90,  96));
        pts.add(new GradePoint((int) id, "A-", 3.70, 85,  89));
        pts.add(new GradePoint((int) id, "B+", 3.30, 80,  84));
        pts.add(new GradePoint((int) id, "B",  3.00, 75,  79));
        pts.add(new GradePoint((int) id, "B-", 2.70, 70,  74));
        pts.add(new GradePoint((int) id, "C+", 2.30, 65,  69));
        pts.add(new GradePoint((int) id, "C",  2.00, 60,  64));
        pts.add(new GradePoint((int) id, "C-", 1.70, 57,  59));
        pts.add(new GradePoint((int) id, "D+", 1.30, 55,  56));
        pts.add(new GradePoint((int) id, "D",  1.00, 52,  54));
        pts.add(new GradePoint((int) id, "D-", 0.70, 50,  51));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  49));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 5. UIU (4.00)
    // -------------------------------------------------------------------------
    private static long seedUiuScale(AppDatabase db) {
        GradingScale scale = new GradingScale("United International University (UIU)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A",  4.00, 90, 100));
        pts.add(new GradePoint((int) id, "A-", 3.67, 86,  89));
        pts.add(new GradePoint((int) id, "B+", 3.33, 82,  85));
        pts.add(new GradePoint((int) id, "B",  3.00, 78,  81));
        pts.add(new GradePoint((int) id, "B-", 2.67, 74,  77));
        pts.add(new GradePoint((int) id, "C+", 2.33, 70,  73));
        pts.add(new GradePoint((int) id, "C",  2.00, 66,  69));
        pts.add(new GradePoint((int) id, "C-", 1.67, 62,  65));
        pts.add(new GradePoint((int) id, "D+", 1.33, 58,  61));
        pts.add(new GradePoint((int) id, "D",  1.00, 55,  57));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  54));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 6. NSU (4.00)
    // -------------------------------------------------------------------------
    private static long seedNsuScale(AppDatabase db) {
        GradingScale scale = new GradingScale("North South University (NSU)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A",  4.00, 93, 100));
        pts.add(new GradePoint((int) id, "A-", 3.70, 90,  92));
        pts.add(new GradePoint((int) id, "B+", 3.30, 87,  89));
        pts.add(new GradePoint((int) id, "B",  3.00, 83,  86));
        pts.add(new GradePoint((int) id, "B-", 2.70, 80,  82));
        pts.add(new GradePoint((int) id, "C+", 2.30, 77,  79));
        pts.add(new GradePoint((int) id, "C",  2.00, 73,  76));
        pts.add(new GradePoint((int) id, "C-", 1.70, 70,  72));
        pts.add(new GradePoint((int) id, "D+", 1.30, 67,  69));
        pts.add(new GradePoint((int) id, "D",  1.00, 60,  66));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  59));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 7. EWU (4.00)
    // -------------------------------------------------------------------------
    private static long seedEwuScale(AppDatabase db) {
        GradingScale scale = new GradingScale("East West University (EWU)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 80, 100));
        pts.add(new GradePoint((int) id, "A",  3.75, 75,  79));
        pts.add(new GradePoint((int) id, "A-", 3.50, 70,  74));
        pts.add(new GradePoint((int) id, "B+", 3.25, 65,  69));
        pts.add(new GradePoint((int) id, "B",  3.00, 60,  64));
        pts.add(new GradePoint((int) id, "B-", 2.75, 55,  59));
        pts.add(new GradePoint((int) id, "C+", 2.50, 50,  54));
        pts.add(new GradePoint((int) id, "C",  2.25, 45,  49));
        pts.add(new GradePoint((int) id, "D",  2.00, 40,  44));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  39));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 8. AIUB (4.00)
    // -------------------------------------------------------------------------
    private static long seedAiubScale(AppDatabase db) {
        GradingScale scale = new GradingScale("AIUB (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 90, 100));
        pts.add(new GradePoint((int) id, "A",  3.75, 85,  89));
        pts.add(new GradePoint((int) id, "B+", 3.50, 80,  84));
        pts.add(new GradePoint((int) id, "B",  3.25, 75,  79));
        pts.add(new GradePoint((int) id, "C+", 3.00, 70,  74));
        pts.add(new GradePoint((int) id, "C",  2.75, 65,  69));
        pts.add(new GradePoint((int) id, "D+", 2.50, 60,  64));
        pts.add(new GradePoint((int) id, "D",  2.25, 50,  59));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  49));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 9. IUB (4.00)
    // -------------------------------------------------------------------------
    private static long seedIubScale(AppDatabase db) {
        GradingScale scale = new GradingScale("Independent University, Bangladesh (IUB)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A",  4.00, 90, 100));
        pts.add(new GradePoint((int) id, "A-", 3.70, 85,  89));
        pts.add(new GradePoint((int) id, "B+", 3.30, 80,  84));
        pts.add(new GradePoint((int) id, "B",  3.00, 75,  79));
        pts.add(new GradePoint((int) id, "B-", 2.70, 70,  74));
        pts.add(new GradePoint((int) id, "C+", 2.30, 65,  69));
        pts.add(new GradePoint((int) id, "C",  2.00, 60,  64));
        pts.add(new GradePoint((int) id, "C-", 1.70, 55,  59));
        pts.add(new GradePoint((int) id, "D+", 1.30, 50,  54));
        pts.add(new GradePoint((int) id, "D",  1.00, 45,  49));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  44));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 10. AUST / DIU (4.00)
    // -------------------------------------------------------------------------
    private static long seedAustDiuScale(AppDatabase db) {
        GradingScale scale = new GradingScale("AUST / DIU (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 80, 100));
        pts.add(new GradePoint((int) id, "A",  3.75, 75,  79));
        pts.add(new GradePoint((int) id, "A-", 3.50, 70,  74));
        pts.add(new GradePoint((int) id, "B+", 3.25, 65,  69));
        pts.add(new GradePoint((int) id, "B",  3.00, 60,  64));
        pts.add(new GradePoint((int) id, "B-", 2.75, 55,  59));
        pts.add(new GradePoint((int) id, "C+", 2.50, 50,  54));
        pts.add(new GradePoint((int) id, "C",  2.25, 45,  49));
        pts.add(new GradePoint((int) id, "D",  2.00, 40,  44));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  39));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 11. IUBAT (4.00)
    // -------------------------------------------------------------------------
    private static long seedIubatScale(AppDatabase db) {
        GradingScale scale = new GradingScale("IUBAT (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 80, 100));
        pts.add(new GradePoint((int) id, "A",  3.75, 75,  79));
        pts.add(new GradePoint((int) id, "A-", 3.50, 70,  74));
        pts.add(new GradePoint((int) id, "B+", 3.25, 65,  69));
        pts.add(new GradePoint((int) id, "B",  3.00, 60,  64));
        pts.add(new GradePoint((int) id, "B-", 2.75, 55,  59));
        pts.add(new GradePoint((int) id, "C+", 2.50, 50,  54));
        pts.add(new GradePoint((int) id, "C",  2.25, 45,  49));
        pts.add(new GradePoint((int) id, "D",  2.00, 40,  44));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  39));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // 12. ULAB (4.00)
    // -------------------------------------------------------------------------
    private static long seedUlabScale(AppDatabase db) {
        GradingScale scale = new GradingScale("ULAB (4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);

        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint((int) id, "A+", 4.00, 95, 100));
        pts.add(new GradePoint((int) id, "A",  4.00, 90,  94));
        pts.add(new GradePoint((int) id, "A-", 3.80, 85,  89));
        pts.add(new GradePoint((int) id, "B+", 3.30, 80,  84));
        pts.add(new GradePoint((int) id, "B",  3.00, 75,  79));
        pts.add(new GradePoint((int) id, "B-", 2.80, 70,  74));
        pts.add(new GradePoint((int) id, "C+", 2.50, 65,  69));
        pts.add(new GradePoint((int) id, "C",  2.20, 60,  64));
        pts.add(new GradePoint((int) id, "D",  1.50, 50,  59));
        pts.add(new GradePoint((int) id, "F",  0.00,  0,  49));
        db.gradingScaleDao().insertGradePoints(pts);
        return id;
    }

    // -------------------------------------------------------------------------
    // Presets
    // -------------------------------------------------------------------------
    private static void seedInstitutionPresets(AppDatabase db,
                                               int nuScaleId, int duScaleId,
                                               int bracScaleId, int uiuScaleId,
                                               int nsuScaleId, int ewuScaleId,
                                               int aiubScaleId, int iubScaleId,
                                               int austScaleId, int iubatScaleId,
                                               int ulabScaleId) {
        List<InstitutionPreset> presets = new ArrayList<>();

        // National University
        presets.add(new InstitutionPreset(
                "National University",
                nuScaleId,
                InstitutionPreset.CALC_MODE_YEAR,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // Dhaka / Jahangirnagar
        presets.add(new InstitutionPreset(
                "Dhaka & Jahangirnagar University",
                duScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE_CONDITIONAL,
                "C+,C,D,F", // Below B- (disallowing B-)
                false,
                2.00,
                true,
                false));

        // BRAC University
        presets.add(new InstitutionPreset(
                "BRAC University",
                bracScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // UIU
        presets.add(new InstitutionPreset(
                "United International University (UIU)",
                uiuScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // NSU
        presets.add(new InstitutionPreset(
                "North South University (NSU)",
                nsuScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE_CONDITIONAL,
                "B,B-,C+,C,C-,D+,D,F",
                true, // F requires approval
                2.00,
                true,
                false));

        // EWU
        presets.add(new InstitutionPreset(
                "East West University (EWU)",
                ewuScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // AIUB
        presets.add(new InstitutionPreset(
                "AIUB",
                aiubScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // IUB
        presets.add(new InstitutionPreset(
                "Independent University, Bangladesh (IUB)",
                iubScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // AUST / DIU
        presets.add(new InstitutionPreset(
                "AUST / DIU",
                austScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // IUBAT
        presets.add(new InstitutionPreset(
                "IUBAT",
                iubatScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        // ULAB
        presets.add(new InstitutionPreset(
                "ULAB",
                ulabScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                true,
                false));

        db.institutionPresetDao().insertPresets(presets);
    }

    private static void seedAdmissionCutoffs(AppDatabase db) {
        List<AdmissionCutoff> cutoffs = new ArrayList<>();
        cutoffs.add(new AdmissionCutoff("University of Dhaka", "A Unit (Science)", 4.50, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("University of Dhaka", "B Unit (Humanities)", 3.50, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("University of Dhaka", "C Unit (Commerce)", 3.50, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("BUET", "Engineering", 4.80, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("RUET", "Engineering", 4.50, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("KUET", "Engineering", 4.50, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("CUET", "Engineering", 4.50, "SSC×5 + HSC×5"));
        cutoffs.add(new AdmissionCutoff("SUST", "Science & Engineering", 4.00, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("North South University (NSU)", "General Admission", 3.00, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("BRAC University", "General Admission", 3.20, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("East West University (EWU)", "General Admission", 2.50, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("National University", "Honours (General)", 2.00, "SSC/HSC GPA"));
        cutoffs.add(new AdmissionCutoff("AIUB", "General Admission", 2.75, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("DIU", "General Admission", 2.50, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("UIU", "General Admission", 2.75, "SSC + HSC avg"));
        cutoffs.add(new AdmissionCutoff("IUB", "General Admission", 2.50, "SSC + HSC avg"));
        db.profileDao().insertCutoffs(cutoffs);
    }
}
