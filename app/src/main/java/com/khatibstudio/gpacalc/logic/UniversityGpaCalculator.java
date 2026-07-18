package com.khatibstudio.gpacalc.logic;

import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Credit-weighted GPA/CGPA calculation for university mode with full support for
 * all retake rules used by Bangladeshi institutions:
 *
 *  - REPLACE             — most recent attempt fully replaces the old one (EWU, BRAC, NU)
 *  - REPLACE_CONDITIONAL — replace only if original grade is in the eligible set (NSU)
 *  - AVERAGE             — all attempts are averaged (custom/uncommon)
 */
public final class UniversityGpaCalculator {

    private UniversityGpaCalculator() {}

    // -------------------------------------------------------------------------
    // Semester GPA
    // -------------------------------------------------------------------------

    /**
     * Credit-weighted GPA for a single semester's course list.
     *
     * @param courses         all courses within one semester
     * @param gradeToPointMap letterGrade → GradePoint for the semester's active scale
     */
    public static double calculateSemesterGpa(List<Course> courses,
                                              Map<String, GradePoint> gradeToPointMap) {
        double weightedSum = 0;
        double totalCredits = 0;
        for (Course c : courses) {
            GradePoint gp = gradeToPointMap.get(c.letterGrade);
            if (gp == null) continue;
            weightedSum  += gp.pointValue * c.creditHours;
            totalCredits += c.creditHours;
        }
        return totalCredits == 0 ? 0.0 : round2(weightedSum / totalCredits);
    }

    // -------------------------------------------------------------------------
    // CGPA — retake-aware
    // -------------------------------------------------------------------------

    /**
     * Calculates CGPA across ALL courses for a profile using the institution's
     * retake rule.
     *
     * @param allCourses      every course across every semester for the profile
     * @param gradeToPointMap merged letterGrade → GradePoint map across all scales used
     * @param preset          the active InstitutionPreset (provides retake rule + eligible grades)
     */
    public static double calculateCgpa(List<Course> allCourses,
                                       Map<String, GradePoint> gradeToPointMap,
                                       InstitutionPreset preset) {
        List<Course> effective = resolveRetakes(allCourses, gradeToPointMap, preset);
        double weightedSum = 0;
        double totalCredits = 0;
        for (Course c : effective) {
            GradePoint gp = gradeToPointMap.get(c.letterGrade);
            if (gp == null) continue;
            weightedSum  += gp.pointValue * c.creditHours;
            totalCredits += c.creditHours;
        }
        return totalCredits == 0 ? 0.0 : round2(weightedSum / totalCredits);
    }

    /**
     * Overload that accepts a plain rule string (for backward compatibility with
     * callers that don't have an InstitutionPreset yet — uses REPLACE/AVERAGE only).
     */
    public static double calculateCgpa(List<Course> allCourses,
                                       Map<String, GradePoint> gradeToPointMap,
                                       String retakeRule) {
        InstitutionPreset stub = new InstitutionPreset(
                "stub", 0, InstitutionPreset.CALC_MODE_SEMESTER,
                retakeRule, "", false, 2.00, false, true);
        return calculateCgpa(allCourses, gradeToPointMap, stub);
    }

    // -------------------------------------------------------------------------
    // Target / reverse calculator
    // -------------------------------------------------------------------------

    /**
     * Given current earned points + credits and a target CGPA, returns the GPA
     * required in the remaining credits.
     *
     *   requiredGPA = (targetCGPA × totalFutureCredits − currentTotalPoints)
     *                 / remainingCredits
     */
    public static double requiredGpaForTarget(double currentTotalPoints,
                                              double currentTotalCredits,
                                              double targetCgpa,
                                              double remainingCredits) {
        if (remainingCredits <= 0) return 0.0;
        double totalFuture = currentTotalCredits + remainingCredits;
        return round2((targetCgpa * totalFuture - currentTotalPoints) / remainingCredits);
    }

    // -------------------------------------------------------------------------
    // Retake resolution — private
    // -------------------------------------------------------------------------

    private static List<Course> resolveRetakes(List<Course> allCourses,
                                               Map<String, GradePoint> gradeToPointMap,
                                               InstitutionPreset preset) {
        String rule = preset != null ? preset.retakeRule : Course.RETAKE_RULE_REPLACE;

        // Build a quick lookup: original course id → the original Course object
        Map<Integer, Course> idToCourse = new HashMap<>();
        for (Course c : allCourses) {
            idToCourse.put(c.id, c);
        }

        // Group retake attempts by their originalCourseId
        Map<Integer, List<Course>> retakeGroups = new HashMap<>();
        for (Course c : allCourses) {
            if (c.isRetake && c.originalCourseId != 0) {
                retakeGroups.computeIfAbsent(c.originalCourseId, k -> new ArrayList<>()).add(c);
            }
        }

        Set<Integer> supersededOriginalIds = retakeGroups.keySet();

        // For REPLACE_CONDITIONAL: build the set of eligible grades
        Set<String> eligibleGrades = new HashSet<>();
        if (Course.RETAKE_RULE_REPLACE_CONDITIONAL.equals(rule)
                && preset != null
                && !preset.retakeEligibleGrades.isEmpty()) {
            eligibleGrades.addAll(Arrays.asList(preset.retakeEligibleGrades.split(",")));
        }

        // Collect non-retake courses, excluding those that have been superseded
        List<Course> result = new ArrayList<>();
        for (Course c : allCourses) {
            if (c.isRetake && c.originalCourseId != 0) continue;   // skip retake records
            if (supersededOriginalIds.contains(c.id)) {
                // This original course has retake(s) — decide whether to include or exclude
                if (Course.RETAKE_RULE_REPLACE_CONDITIONAL.equals(rule)) {
                    // Only exclude if the original grade is in the eligible set
                    boolean originalEligible = eligibleGrades.isEmpty()
                            || eligibleGrades.contains(c.letterGrade);

                    if (!originalEligible) {
                        // Grade not retake-eligible — keep the original, ignore the retake
                        result.add(c);
                        retakeGroups.remove(c.id); // don't process this retake group below
                    }
                    // else: original IS eligible → exclude it; retake group handled below
                }
                // For REPLACE / AVERAGE: always exclude the original
            } else {
                result.add(c);
            }
        }

        // Process retake groups
        for (Map.Entry<Integer, List<Course>> entry : retakeGroups.entrySet()) {
            List<Course> attempts = entry.getValue();
            if (attempts.isEmpty()) continue;

            switch (rule) {
                case Course.RETAKE_RULE_REPLACE_CONDITIONAL:
                case Course.RETAKE_RULE_REPLACE: {
                    // Keep the most recent attempt (highest id)
                    Course latest = attempts.get(0);
                    for (Course c : attempts) {
                        if (c.id > latest.id) latest = c;
                    }

                    // NSU special case: F grade requires formal approval before exclusion
                    if (Course.RETAKE_RULE_REPLACE_CONDITIONAL.equals(rule)
                            && preset != null
                            && preset.retakeRequiresApprovalForF) {
                        Course original = idToCourse.get(entry.getKey());
                        if (original != null && "F".equals(original.letterGrade)) {
                            // F stays in CGPA until explicitly approved — skip this retake
                            result.add(original);
                            continue;
                        }
                    }
                    result.add(latest);
                    break;
                }

                case Course.RETAKE_RULE_AVERAGE: {
                    // Average all attempts into one synthetic entry
                    double pointSum = 0;
                    int count = 0;
                    double credits = attempts.get(0).creditHours;
                    for (Course c : attempts) {
                        GradePoint gp = gradeToPointMap.get(c.letterGrade);
                        if (gp != null) { pointSum += gp.pointValue; count++; }
                    }
                    if (count > 0) {
                        String syntheticGrade = closestGradeForPoint(
                                pointSum / count, gradeToPointMap);
                        Course synthetic = new Course(
                                attempts.get(0).semesterId,
                                attempts.get(0).courseName,
                                credits, syntheticGrade, false, 0);
                        result.add(synthetic);
                    }
                    break;
                }

                default:
                    // Unknown rule — keep the latest attempt as a safe fallback
                    Course latest = attempts.get(0);
                    for (Course c : attempts) {
                        if (c.id > latest.id) latest = c;
                    }
                    result.add(latest);
            }
        }

        return result;
    }

    private static String closestGradeForPoint(double point,
                                               Map<String, GradePoint> gradeToPointMap) {
        String best = "F";
        double bestDiff = Double.MAX_VALUE;
        for (GradePoint gp : gradeToPointMap.values()) {
            double diff = Math.abs(gp.pointValue - point);
            if (diff < bestDiff) { bestDiff = diff; best = gp.letterGrade; }
        }
        return best;
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
