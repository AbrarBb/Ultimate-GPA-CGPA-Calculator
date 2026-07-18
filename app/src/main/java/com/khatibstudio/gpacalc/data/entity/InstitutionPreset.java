package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a known academic institution with its grading rules.
 *
 * All fields are user-editable at runtime (isEditable may be false for built-in presets
 * to signal "official data", but the UI should still allow overrides — universities
 * revise policy and a wrong hardcoded rule is worse than no rule).
 */
@Entity(tableName = "institution_preset")
public class InstitutionPreset {

    /** Semester-based CGPA (most private and public universities). */
    public static final String CALC_MODE_SEMESTER = "SEMESTER";

    /** Year-based GPA (National University Honours/Degree/Masters). */
    public static final String CALC_MODE_YEAR = "YEAR";

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Display name, e.g. "EWU", "NSU", "BRAC University", "National University". */
    @NonNull
    public String name;

    /** FK into grading_scale — the default scale auto-loaded for this institution. */
    public int defaultScaleId;

    /** CALC_MODE_SEMESTER or CALC_MODE_YEAR. */
    @NonNull
    public String calcMode;

    /**
     * Retake rule: Course.RETAKE_RULE_REPLACE, RETAKE_RULE_REPLACE_CONDITIONAL, or
     * RETAKE_RULE_AVERAGE.
     */
    @NonNull
    public String retakeRule;

    /**
     * Comma-separated list of letter grades that are eligible for retake under
     * REPLACE_CONDITIONAL (e.g. "B,B-,C+,C,C-,D+,D,F").
     * Empty string means all grades are eligible.
     * Ignored unless retakeRule == REPLACE_CONDITIONAL.
     */
    @NonNull
    public String retakeEligibleGrades;

    /**
     * True if F grades require a formal institutional approval before the retake
     * replaces them in the CGPA calculation (NSU behaviour).
     */
    public boolean retakeRequiresApprovalForF;

    /** Minimum CGPA to remain in good standing (e.g. 2.00 for BRAC). */
    public double minPassingCgpa;

    /**
     * True if this institution has a verified, official marks-to-grade table.
     * Only show the marks entry field in the UI when this is true.
     * Currently true for: National University, BRAC University.
     * False for: EWU, NSU (no official table confirmed).
     */
    public boolean supportsMarksEntry;

    /**
     * False for built-in official presets (communicates "this is verified data"),
     * but in-app editing must still be allowed — the flag is informational only.
     */
    public boolean isEditable;

    public InstitutionPreset(@NonNull String name, int defaultScaleId,
                              @NonNull String calcMode, @NonNull String retakeRule,
                              @NonNull String retakeEligibleGrades,
                              boolean retakeRequiresApprovalForF,
                              double minPassingCgpa, boolean supportsMarksEntry,
                              boolean isEditable) {
        this.name = name;
        this.defaultScaleId = defaultScaleId;
        this.calcMode = calcMode;
        this.retakeRule = retakeRule;
        this.retakeEligibleGrades = retakeEligibleGrades;
        this.retakeRequiresApprovalForF = retakeRequiresApprovalForF;
        this.minPassingCgpa = minPassingCgpa;
        this.supportsMarksEntry = supportsMarksEntry;
        this.isEditable = isEditable;
    }
}
