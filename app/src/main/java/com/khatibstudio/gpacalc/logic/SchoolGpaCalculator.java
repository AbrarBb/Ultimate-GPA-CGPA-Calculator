package com.khatibstudio.gpacalc.logic;

import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.Subject;

import java.util.List;
import java.util.Map;

/**
 * Implements verified SSC/HSC GPA calculation rules:
 * - GPA scale out of 5.00
 * - GPA without 4th subject = average of grade points of compulsory subjects
 * - GPA with 4th subject = GPA without 4th subject + max(0, fourthSubjectPoint - 2.00),
 *   capped at 5.00
 */
public final class SchoolGpaCalculator {

    private SchoolGpaCalculator() {
    }

    public static class Result {
        public final double gpaWithoutFourth;
        public final double gpaWithFourth;
        public final boolean hasFourthSubject;

        public Result(double gpaWithoutFourth, double gpaWithFourth, boolean hasFourthSubject) {
            this.gpaWithoutFourth = gpaWithoutFourth;
            this.gpaWithFourth = gpaWithFourth;
            this.hasFourthSubject = hasFourthSubject;
        }
    }

    /**
     * @param subjects        all subjects for one exam record (SSC or HSC)
     * @param gradeToPointMap map of letterGrade -> GradePoint for the active scale
     */
    public static Result calculate(List<Subject> subjects, Map<String, GradePoint> gradeToPointMap) {
        double compulsorySum = 0;
        int compulsoryCount = 0;
        Double fourthSubjectPoint = null;

        for (Subject s : subjects) {
            GradePoint gp = gradeToPointMap.get(s.letterGrade);
            if (gp == null) continue; // unknown grade, skip defensively

            if (s.isOptionalFourth) {
                fourthSubjectPoint = gp.pointValue;
            } else {
                compulsorySum += gp.pointValue;
                compulsoryCount++;
            }
        }

        double gpaWithoutFourth = compulsoryCount == 0 ? 0.0 : compulsorySum / compulsoryCount;

        double gpaWithFourth = gpaWithoutFourth;
        boolean hasFourth = fourthSubjectPoint != null;
        if (hasFourth) {
            double bonus = Math.max(0.0, fourthSubjectPoint - 2.00);
            gpaWithFourth = Math.min(5.00, gpaWithoutFourth + bonus);
        }

        return new Result(round2(gpaWithoutFourth), round2(gpaWithFourth), hasFourth);
    }

    /**
     * Combined SSC+HSC average — a value-add feature not commonly found in
     * existing calculators, useful for admission formulas that require it.
     */
    public static double combinedAverage(double sscGpaWithFourth, double hscGpaWithFourth) {
        return round2((sscGpaWithFourth + hscGpaWithFourth) / 2.0);
    }

    /**
     * Derives a letter grade from raw marks using the active scale's
     * min/max marks ranges (school mode only).
     */
    public static String gradeFromMarks(double marks, List<GradePoint> scalePoints) {
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
