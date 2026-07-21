package com.khatibstudio.gpacalc.logic;

import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.Subject;

import java.util.List;
import java.util.Map;

/**
 * Implements verified SSC/HSC GPA calculation rules (Bangladesh Education Board):
 * - GPA scale out of 5.00
 * - GPA without 4th subject = average of grade points of compulsory subjects
 * - GPA with 4th subject = GPA without 4th subject + max(0, fourthSubjectPoint - 2.00),
 *   capped at 5.00
 * - Any F grade in a compulsory subject = overall FAIL regardless of other grades
 */
public final class SchoolGpaCalculator {

    private SchoolGpaCalculator() {
    }

    // -------------------------------------------------------------------------
    // Result
    // -------------------------------------------------------------------------

    public static class Result {
        public final double gpaWithoutFourth;
        public final double gpaWithFourth;
        public final boolean hasFourthSubject;
        public final boolean isFailed;
        public final int totalSubjects;
        public final int compulsoryCount;
        public final double optionalBonus;
        public final String highestGrade;
        public final String lowestGrade;

        public Result(double gpaWithoutFourth, double gpaWithFourth,
                      boolean hasFourthSubject, boolean isFailed,
                      int totalSubjects, int compulsoryCount, double optionalBonus,
                      String highestGrade, String lowestGrade) {
            this.gpaWithoutFourth = gpaWithoutFourth;
            this.gpaWithFourth = gpaWithFourth;
            this.hasFourthSubject = hasFourthSubject;
            this.isFailed = isFailed;
            this.totalSubjects = totalSubjects;
            this.compulsoryCount = compulsoryCount;
            this.optionalBonus = optionalBonus;
            this.highestGrade = highestGrade;
            this.lowestGrade = lowestGrade;
        }

        public Result(double gpaWithoutFourth, double gpaWithFourth, boolean hasFourthSubject) {
            this(gpaWithoutFourth, gpaWithFourth, hasFourthSubject, false, 0, 0, 0.0, "", "");
        }
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    public static class ValidationResult {
        public final boolean isValid;
        public final String errorMessage;

        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
    }

    /**
     * Validates that all compulsory subjects have valid grades assigned.
     *
     * @param subjects        all subjects for one exam record
     * @param gradeToPointMap map of letterGrade → GradePoint for the active scale
     * @return validation result with error message if invalid
     */
    public static ValidationResult validate(List<Subject> subjects,
                                             Map<String, GradePoint> gradeToPointMap) {
        if (subjects == null || subjects.isEmpty()) {
            return ValidationResult.invalid("No subjects found. Please add subjects first.");
        }
        if (gradeToPointMap == null || gradeToPointMap.isEmpty()) {
            return ValidationResult.invalid("Grading scale not found.");
        }

        for (Subject s : subjects) {
            if (s.isOptionalFourth) continue; // optional can be ungraded
            if (s.letterGrade == null || s.letterGrade.isEmpty()
                    || !gradeToPointMap.containsKey(s.letterGrade)) {
                return ValidationResult.invalid(
                        "Please select grades for all required subjects. Missing: " + s.subjectName);
            }
        }
        return ValidationResult.valid();
    }

    // -------------------------------------------------------------------------
    // Calculation
    // -------------------------------------------------------------------------

    /**
     * Calculates GPA for a single SSC or HSC exam record.
     *
     * @param subjects        all subjects for one exam record (SSC or HSC)
     * @param gradeToPointMap map of letterGrade → GradePoint for the active scale
     */
    public static Result calculate(List<Subject> subjects, Map<String, GradePoint> gradeToPointMap) {
        if (subjects == null || subjects.isEmpty() || gradeToPointMap == null) {
            return new Result(0, 0, false, false, 0, 0, 0, "", "");
        }

        double compulsorySum = 0;
        int compulsoryCount = 0;
        Double fourthSubjectPoint = null;
        boolean hasFailed = false;
        double highestPoint = -1;
        double lowestPoint = Double.MAX_VALUE;
        String highestGrade = "";
        String lowestGrade = "";
        int totalSubjects = 0;

        for (Subject s : subjects) {
            GradePoint gp = gradeToPointMap.get(s.letterGrade);
            if (gp == null) continue; // unknown grade, skip defensively

            totalSubjects++;

            // Track highest and lowest
            if (gp.pointValue > highestPoint) {
                highestPoint = gp.pointValue;
                highestGrade = gp.letterGrade;
            }
            if (gp.pointValue < lowestPoint) {
                lowestPoint = gp.pointValue;
                lowestGrade = gp.letterGrade;
            }

            if (s.isOptionalFourth) {
                fourthSubjectPoint = gp.pointValue;
            } else {
                compulsorySum += gp.pointValue;
                compulsoryCount++;
                // Bangladesh Board rule: any F in compulsory = overall FAIL
                if ("F".equalsIgnoreCase(s.letterGrade) || gp.pointValue == 0.0) {
                    hasFailed = true;
                }
            }
        }

        // Division-by-zero guard
        double gpaWithoutFourth = compulsoryCount == 0 ? 0.0 : compulsorySum / compulsoryCount;

        double gpaWithFourth = gpaWithoutFourth;
        double optionalBonus = 0;
        boolean hasFourth = fourthSubjectPoint != null;
        if (hasFourth) {
            optionalBonus = Math.max(0.0, fourthSubjectPoint - 2.00);
            gpaWithFourth = Math.min(5.00, gpaWithoutFourth + optionalBonus);
            optionalBonus = round2(optionalBonus);
        }

        return new Result(
                round2(gpaWithoutFourth),
                round2(gpaWithFourth),
                hasFourth,
                hasFailed,
                totalSubjects,
                compulsoryCount,
                optionalBonus,
                highestGrade,
                lowestGrade
        );
    }

    /**
     * Combined SSC+HSC average — useful for admission formulas.
     */
    public static double combinedAverage(double sscGpaWithFourth, double hscGpaWithFourth) {
        return round2((sscGpaWithFourth + hscGpaWithFourth) / 2.0);
    }

    /**
     * Derives a letter grade from raw marks using the active scale's
     * min/max marks ranges (school mode only).
     */
    public static String gradeFromMarks(double marks, List<GradePoint> scalePoints) {
        if (scalePoints == null) return "F";
        for (GradePoint gp : scalePoints) {
            if (marks >= gp.minMarks && marks <= gp.maxMarks) {
                return gp.letterGrade;
            }
        }
        return "F";
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
