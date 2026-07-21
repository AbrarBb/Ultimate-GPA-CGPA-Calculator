package com.khatibstudio.gpacalc.data.model;

import com.khatibstudio.gpacalc.data.entity.ExamRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static, in-memory catalog of official board subjects for each
 * (EducationSystem, Group) combination.
 *
 * These lists are based on the Bangladesh Education Board's published
 * curriculum. Users never type subject names manually — they pick from
 * the auto-populated list and assign grades via dropdowns.
 *
 * Designed for extensibility: add Cambridge O/A Level, Madrasa (Dakhil/Alim),
 * Diploma, Polytechnic, etc. by registering new entries in {@link #init()}.
 */
public final class SubjectRepository {

    private SubjectRepository() {}

    /** Key format: "SSC_SCIENCE", "HSC_BUSINESS_STUDIES", etc. */
    private static final Map<String, List<SubjectDefinition>> CATALOG = new HashMap<>();

    static {
        init();
    }

    // ---------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------

    /**
     * Returns the official subject list for the given exam type and group.
     *
     * @param examType {@link ExamRecord#TYPE_SSC} or {@link ExamRecord#TYPE_HSC}
     * @param group    {@link ExamRecord#GROUP_SCIENCE}, {@link ExamRecord#GROUP_BUSINESS_STUDIES},
     *                 or {@link ExamRecord#GROUP_HUMANITIES}
     * @return unmodifiable list of subjects; empty if unknown combination
     */
    public static List<SubjectDefinition> getSubjects(String examType, String group) {
        String key = examType + "_" + group;
        List<SubjectDefinition> list = CATALOG.get(key);
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    /**
     * Returns all available groups for a given exam type.
     */
    public static String[] getGroups(String examType) {
        return new String[]{
                ExamRecord.GROUP_SCIENCE,
                ExamRecord.GROUP_BUSINESS_STUDIES,
                ExamRecord.GROUP_HUMANITIES
        };
    }

    // ---------------------------------------------------------------------
    // Subject Data — Official Bangladesh Board Curriculum
    // ---------------------------------------------------------------------

    private static void init() {
        // ==== SSC ====
        registerSscScience();
        registerSscBusinessStudies();
        registerSscHumanities();

        // ==== HSC ====
        registerHscScience();
        registerHscBusinessStudies();
        registerHscHumanities();
    }

    // ---- SSC Science ----
    private static void registerSscScience() {
        List<SubjectDefinition> subjects = new ArrayList<>();
        subjects.add(new SubjectDefinition("Bangla", true, false));
        subjects.add(new SubjectDefinition("English", true, false));
        subjects.add(new SubjectDefinition("Mathematics", true, false));
        subjects.add(new SubjectDefinition("Physics", true, false));
        subjects.add(new SubjectDefinition("Chemistry", true, false));
        subjects.add(new SubjectDefinition("Biology", true, false));
        subjects.add(new SubjectDefinition("ICT", true, false));
        subjects.add(new SubjectDefinition("Religion / Moral Education", true, false));
        subjects.add(new SubjectDefinition("Bangladesh & Global Studies", true, false));
        // Optional 4th subject — user can pick (Higher Math, Agriculture, etc.)
        subjects.add(new SubjectDefinition("Higher Mathematics", false, true));
        CATALOG.put(ExamRecord.TYPE_SSC + "_" + ExamRecord.GROUP_SCIENCE, subjects);
    }

    // ---- SSC Business Studies ----
    private static void registerSscBusinessStudies() {
        List<SubjectDefinition> subjects = new ArrayList<>();
        subjects.add(new SubjectDefinition("Bangla", true, false));
        subjects.add(new SubjectDefinition("English", true, false));
        subjects.add(new SubjectDefinition("Mathematics", true, false));
        subjects.add(new SubjectDefinition("Accounting", true, false));
        subjects.add(new SubjectDefinition("Business Entrepreneurship", true, false));
        subjects.add(new SubjectDefinition("Finance & Banking", true, false));
        subjects.add(new SubjectDefinition("ICT", true, false));
        subjects.add(new SubjectDefinition("Religion / Moral Education", true, false));
        subjects.add(new SubjectDefinition("Bangladesh & Global Studies", true, false));
        // Optional 4th
        subjects.add(new SubjectDefinition("Optional Subject", false, true));
        CATALOG.put(ExamRecord.TYPE_SSC + "_" + ExamRecord.GROUP_BUSINESS_STUDIES, subjects);
    }

    // ---- SSC Humanities ----
    private static void registerSscHumanities() {
        List<SubjectDefinition> subjects = new ArrayList<>();
        subjects.add(new SubjectDefinition("Bangla", true, false));
        subjects.add(new SubjectDefinition("English", true, false));
        subjects.add(new SubjectDefinition("Mathematics", true, false));
        subjects.add(new SubjectDefinition("History of Bangladesh & World Civilization", true, false));
        subjects.add(new SubjectDefinition("Geography & Environment", true, false));
        subjects.add(new SubjectDefinition("Civics & Citizenship", true, false));
        subjects.add(new SubjectDefinition("Economics", true, false));
        subjects.add(new SubjectDefinition("ICT", true, false));
        subjects.add(new SubjectDefinition("Religion / Moral Education", true, false));
        // Optional 4th
        subjects.add(new SubjectDefinition("Optional Subject", false, true));
        CATALOG.put(ExamRecord.TYPE_SSC + "_" + ExamRecord.GROUP_HUMANITIES, subjects);
    }

    // ---- HSC Science ----
    private static void registerHscScience() {
        List<SubjectDefinition> subjects = new ArrayList<>();
        subjects.add(new SubjectDefinition("Bangla", true, false));
        subjects.add(new SubjectDefinition("English", true, false));
        subjects.add(new SubjectDefinition("Physics", true, false));
        subjects.add(new SubjectDefinition("Chemistry", true, false));
        subjects.add(new SubjectDefinition("Mathematics", true, false));
        subjects.add(new SubjectDefinition("Biology", true, false));
        subjects.add(new SubjectDefinition("ICT", true, false));
        // Optional 4th (Higher Mathematics or other)
        subjects.add(new SubjectDefinition("Higher Mathematics", false, true));
        CATALOG.put(ExamRecord.TYPE_HSC + "_" + ExamRecord.GROUP_SCIENCE, subjects);
    }

    // ---- HSC Business Studies ----
    private static void registerHscBusinessStudies() {
        List<SubjectDefinition> subjects = new ArrayList<>();
        subjects.add(new SubjectDefinition("Bangla", true, false));
        subjects.add(new SubjectDefinition("English", true, false));
        subjects.add(new SubjectDefinition("Accounting", true, false));
        subjects.add(new SubjectDefinition("Business Organization & Management", true, false));
        subjects.add(new SubjectDefinition("Finance, Banking & Insurance", true, false));
        subjects.add(new SubjectDefinition("Production Management & Marketing", true, false));
        subjects.add(new SubjectDefinition("ICT", true, false));
        // Optional 4th
        subjects.add(new SubjectDefinition("Optional Subject", false, true));
        CATALOG.put(ExamRecord.TYPE_HSC + "_" + ExamRecord.GROUP_BUSINESS_STUDIES, subjects);
    }

    // ---- HSC Humanities ----
    private static void registerHscHumanities() {
        List<SubjectDefinition> subjects = new ArrayList<>();
        subjects.add(new SubjectDefinition("Bangla", true, false));
        subjects.add(new SubjectDefinition("English", true, false));
        subjects.add(new SubjectDefinition("History", true, false));
        subjects.add(new SubjectDefinition("Civics & Good Governance", true, false));
        subjects.add(new SubjectDefinition("Economics", true, false));
        subjects.add(new SubjectDefinition("Social Work / Logic", true, false));
        subjects.add(new SubjectDefinition("ICT", true, false));
        // Optional 4th
        subjects.add(new SubjectDefinition("Optional Subject", false, true));
        CATALOG.put(ExamRecord.TYPE_HSC + "_" + ExamRecord.GROUP_HUMANITIES, subjects);
    }
}
