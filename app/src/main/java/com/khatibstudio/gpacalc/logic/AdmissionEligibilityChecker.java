package com.khatibstudio.gpacalc.logic;

import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;

import java.util.List;

/**
 * Gap-filling feature: no mainstream BD GPA calculator offers this.
 * Cutoff data (AdmissionCutoff table) should be treated as an editable, updatable
 * local dataset -- NOT hardcoded permanently -- since admission requirements
 * change year to year. Ship a seed list, but allow remote-config or manual
 * in-app update of this table without requiring a full app update.
 */
public final class AdmissionEligibilityChecker {

    private AdmissionEligibilityChecker() {
    }

    public static List<AdmissionCutoff> getEligiblePrograms(List<AdmissionCutoff> allCutoffs, double studentGpa) {
        return allCutoffs.stream()
                .filter(c -> studentGpa >= c.minGpaRequired)
                .sorted((a, b) -> Double.compare(b.minGpaRequired, a.minGpaRequired))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Combined admission score using a user-defined weighted formula, e.g.
     * many BD university admission tests weight SSC GPA, HSC GPA, and a
     * written test score together.
     */
    public static double combinedAdmissionScore(double sscGpa, double sscWeight,
                                                 double hscGpa, double hscWeight,
                                                 double writtenTestScore, double writtenTestWeight) {
        double score = (sscGpa * sscWeight) + (hscGpa * hscWeight) + (writtenTestScore * writtenTestWeight);
        return Math.round(score * 100.0) / 100.0;
    }
}
