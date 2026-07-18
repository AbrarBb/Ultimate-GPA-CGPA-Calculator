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
 * DATA INTEGRITY RULES:
 * - SSC/HSC scale: verified against current (2026) Bangladesh board rules.
 * - National University scale: verified from official NU table (10 grade levels, 4.00 max).
 * - BRAC University scale: verified from bracu.ac.bd, effective Fall 2020 (13 grade levels).
 * - EWU / NSU: letter-grade entry ONLY — no officially published marks table; minMarks/maxMarks = -1.
 * - Generic UGC: fallback for DU, BUET, RUET, KUET, CUET, SUST, etc. until confirmed.
 *
 * All institution data is editable at runtime — universities revise policy and a
 * wrong hardcoded rule that can't be corrected in-app is worse than no rule at all.
 */
public final class DefaultScaleSeeder {

    private DefaultScaleSeeder() {}

    public static void seed(AppDatabase db) {
        long sscHscScaleId = seedSscHscScale(db);
        long nuScaleId     = seedNationalUniversityScale(db);
        long bracScaleId   = seedBracScale(db);
        long ewuScaleId    = seedEwuScale(db);
        long nsuScaleId    = seedNsuScale(db);
        long genericScaleId = seedGenericUgcScale(db);

        seedInstitutionPresets(db,
                (int) nuScaleId,
                (int) bracScaleId,
                (int) ewuScaleId,
                (int) nsuScaleId,
                (int) genericScaleId);

        seedAdmissionCutoffs(db);
    }

    // -------------------------------------------------------------------------
    // 1. SSC / HSC — GPA scale 5.00 (verified, general + Dakhil boards, 2026)
    // -------------------------------------------------------------------------
    private static long seedSscHscScale(AppDatabase db) {
        GradingScale scale = new GradingScale(
                "SSC/HSC (GPA 5.00)", GradingScale.MODE_SCHOOL, false);
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
    // 2. National University — verified official table (Honours/Degree/Masters)
    //    Source: National University Bangladesh official grading policy.
    //    10 distinct grade levels, max 4.00. Pass mark: 40 (D).
    // -------------------------------------------------------------------------
    private static long seedNationalUniversityScale(AppDatabase db) {
        GradingScale scale = new GradingScale(
                "National University (4.00)", GradingScale.MODE_UNIVERSITY, false);
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
    // 3. BRAC University — verified from bracu.ac.bd, effective Fall 2020.
    //    13 grade levels, A+ and A both = 4.00. Min passing CGPA: 2.00.
    //    Marks ranges use integer boundaries (official ranges use < notation).
    // -------------------------------------------------------------------------
    private static long seedBracScale(AppDatabase db) {
        GradingScale scale = new GradingScale(
                "BRAC University (4.00)", GradingScale.MODE_UNIVERSITY, false);
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
    // 4. EWU — standard UGC-style 4.00 scale, letter-grade entry ONLY.
    //    No officially published marks-to-grade table confirmed — minMarks/maxMarks = -1.
    // -------------------------------------------------------------------------
    private static long seedEwuScale(AppDatabase db) {
        GradingScale scale = new GradingScale(
                "EWU (UGC 4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);
        db.gradingScaleDao().insertGradePoints(ugcLetterGradePoints((int) id));
        return id;
    }

    // -------------------------------------------------------------------------
    // 5. NSU — standard UGC-style 4.00 scale with +/- increments, letter-grade ONLY.
    //    No officially published marks table confirmed — minMarks/maxMarks = -1.
    // -------------------------------------------------------------------------
    private static long seedNsuScale(AppDatabase db) {
        GradingScale scale = new GradingScale(
                "NSU (UGC 4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);
        db.gradingScaleDao().insertGradePoints(ugcLetterGradePoints((int) id));
        return id;
    }

    // -------------------------------------------------------------------------
    // 6. Generic UGC Public — fallback for DU, BUET, RUET, KUET, CUET, SUST, etc.
    //    Use until each institution's specific rules are confirmed.
    // -------------------------------------------------------------------------
    private static long seedGenericUgcScale(AppDatabase db) {
        GradingScale scale = new GradingScale(
                "Generic Public University (UGC 4.00)", GradingScale.MODE_UNIVERSITY, false);
        long id = db.gradingScaleDao().insertScale(scale);
        db.gradingScaleDao().insertGradePoints(ugcLetterGradePoints((int) id));
        return id;
    }

    /** Common UGC-style 4.00 letter-grade table (no marks ranges). */
    private static List<GradePoint> ugcLetterGradePoints(int scaleId) {
        List<GradePoint> pts = new ArrayList<>();
        pts.add(new GradePoint(scaleId, "A+", 4.00, -1, -1));
        pts.add(new GradePoint(scaleId, "A",  3.75, -1, -1));
        pts.add(new GradePoint(scaleId, "A-", 3.50, -1, -1));
        pts.add(new GradePoint(scaleId, "B+", 3.25, -1, -1));
        pts.add(new GradePoint(scaleId, "B",  3.00, -1, -1));
        pts.add(new GradePoint(scaleId, "B-", 2.75, -1, -1));
        pts.add(new GradePoint(scaleId, "C+", 2.50, -1, -1));
        pts.add(new GradePoint(scaleId, "C",  2.25, -1, -1));
        pts.add(new GradePoint(scaleId, "D",  2.00, -1, -1));
        pts.add(new GradePoint(scaleId, "F",  0.00, -1, -1));
        return pts;
    }

    // -------------------------------------------------------------------------
    // Institution presets — link scales to institution-specific rules.
    // retakeEligibleGrades: empty = all grades eligible for retake.
    // -------------------------------------------------------------------------
    private static void seedInstitutionPresets(AppDatabase db,
                                               int nuScaleId, int bracScaleId,
                                               int ewuScaleId, int nsuScaleId,
                                               int genericScaleId) {
        List<InstitutionPreset> presets = new ArrayList<>();

        // National University — REPLACE rule, year-based, supports marks entry
        presets.add(new InstitutionPreset(
                "National University",
                nuScaleId,
                InstitutionPreset.CALC_MODE_YEAR,
                Course.RETAKE_RULE_REPLACE,
                "",       // all grades eligible
                false,
                2.00,
                true,     // verified marks table exists
                false));

        // BRAC University — REPLACE rule, semester-based, supports marks entry
        presets.add(new InstitutionPreset(
                "BRAC University",
                bracScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",       // all grades eligible
                false,
                2.00,
                true,     // verified marks table exists
                false));

        // EWU — REPLACE rule, semester-based, letter-grade only
        presets.add(new InstitutionPreset(
                "East West University (EWU)",
                ewuScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                false,    // no verified marks table
                false));

        // NSU — REPLACE_CONDITIONAL: only B or lower eligible; F requires approval
        presets.add(new InstitutionPreset(
                "North South University (NSU)",
                nsuScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE_CONDITIONAL,
                "B,B-,C+,C,C-,D+,D,F",  // grades ≤ B are eligible
                true,     // F grades need formal approval to be replaced
                2.00,
                false,    // no verified marks table
                false));

        // Generic Public University — REPLACE rule, semester-based, letter-grade only
        presets.add(new InstitutionPreset(
                "Public University (DU/BUET/RUET etc.)",
                genericScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                false,
                true));   // editable — users should customise for their specific institution

        // Custom — placeholder for user-created institutions
        presets.add(new InstitutionPreset(
                "Custom Institution",
                genericScaleId,
                InstitutionPreset.CALC_MODE_SEMESTER,
                Course.RETAKE_RULE_REPLACE,
                "",
                false,
                2.00,
                false,
                true));

        db.institutionPresetDao().insertPresets(presets);
    }

    // -------------------------------------------------------------------------
    // Admission cutoff seeds — editable, not hardcoded permanently.
    // Requirements change yearly; treat this as a default starting dataset.
    // -------------------------------------------------------------------------
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
