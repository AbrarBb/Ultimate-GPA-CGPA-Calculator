package com.khatibstudio.gpacalc.data.model;

import com.khatibstudio.gpacalc.data.entity.ExamRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Static, in-memory catalog of official board subjects and choice-based subjects.
 * Matches the official Bangladesh Board curriculum (Science & Business Studies streams) exactly.
 */
public final class SubjectRepository {

    private SubjectRepository() {}

    /**
     * Returns the compulsory core subjects for a given exam and group.
     */
    public static List<SubjectDefinition> getCompulsorySubjects(String examType, String group) {
        List<SubjectDefinition> list = new ArrayList<>();
        if (ExamRecord.TYPE_SSC.equals(examType)) {
            list.add(new SubjectDefinition("Bangla", true, false));
            list.add(new SubjectDefinition("English", true, false));
            list.add(new SubjectDefinition("Mathematics", true, false));
            list.add(new SubjectDefinition("ICT", true, false));
            list.add(new SubjectDefinition("Religion & Moral Education", true, false));
            list.add(new SubjectDefinition("Bangladesh & Global Studies", true, false));

            if (ExamRecord.GROUP_SCIENCE.equals(group)) {
                list.add(new SubjectDefinition("Physics", true, false));
                list.add(new SubjectDefinition("Chemistry", true, false));
            } else if (ExamRecord.GROUP_BUSINESS_STUDIES.equals(group)) {
                list.add(new SubjectDefinition("Accounting", true, false));
                list.add(new SubjectDefinition("Business Entrepreneurship", true, false));
                list.add(new SubjectDefinition("Finance & Banking", true, false));
            } else if (ExamRecord.GROUP_HUMANITIES.equals(group)) {
                list.add(new SubjectDefinition("History of Bangladesh & World Civilization", true, false));
                list.add(new SubjectDefinition("Geography & Environment", true, false));
                list.add(new SubjectDefinition("Civics & Citizenship", true, false));
                list.add(new SubjectDefinition("Economics", true, false));
            }
        } else if (ExamRecord.TYPE_HSC.equals(examType)) {
            list.add(new SubjectDefinition("Bengali (Ban)", true, false));
            list.add(new SubjectDefinition("English (ENG)", true, false));
            list.add(new SubjectDefinition("Information and Communication Technology (ICT)", true, false));

            if (ExamRecord.GROUP_SCIENCE.equals(group)) {
                list.add(new SubjectDefinition("Physics (Phy)", true, false));
                list.add(new SubjectDefinition("Chemistry (Chy)", true, false));
            } else if (ExamRecord.GROUP_BUSINESS_STUDIES.equals(group)) {
                list.add(new SubjectDefinition("Business Organization and Management (BOM)", true, false));
                list.add(new SubjectDefinition("Accounting (ACC)", true, false));
            } else if (ExamRecord.GROUP_HUMANITIES.equals(group)) {
                list.add(new SubjectDefinition("History", true, false));
                list.add(new SubjectDefinition("Civics & Good Governance", true, false));
                list.add(new SubjectDefinition("Economics", true, false));
            }
        }
        return list;
    }

    /**
     * Returns the list of possible choices for the 3rd subject.
     * Empty if no choice is required.
     */
    public static List<String> getThirdSubjectOptions(String examType, String group) {
        if (ExamRecord.TYPE_HSC.equals(examType)) {
            if (ExamRecord.GROUP_SCIENCE.equals(group)) {
                return Arrays.asList("Biology (BIO)", "Higher Mathematics (H.Math)");
            } else if (ExamRecord.GROUP_BUSINESS_STUDIES.equals(group)) {
                return Arrays.asList("Finance, Banking & Insurance (FBI)", "Production Management & Marketing (PMM)");
            }
        } else if (ExamRecord.TYPE_SSC.equals(examType)) {
            if (ExamRecord.GROUP_SCIENCE.equals(group)) {
                return Arrays.asList("Biology", "Higher Mathematics");
            }
        }
        return Collections.emptyList();
    }

    /**
     * Returns the list of possible choices for the 4th (optional) subject.
     */
    public static List<String> getFourthSubjectOptions(String examType, String group) {
        if (ExamRecord.TYPE_HSC.equals(examType)) {
            if (ExamRecord.GROUP_SCIENCE.equals(group)) {
                return Arrays.asList("Biology (BIO)", "Higher Mathematics (H.Math)", "Statistics (Stat)");
            } else if (ExamRecord.GROUP_BUSINESS_STUDIES.equals(group)) {
                return Arrays.asList("Finance, Banking & Insurance (FBI)", "Production Management & Marketing (PMM)", "Statistics (Stat)", "Economics (ECO)", "Home Science (HSc)");
            } else if (ExamRecord.GROUP_HUMANITIES.equals(group)) {
                return Arrays.asList("Logic", "Social Work", "Home Science (HSc)", "Statistics (Stat)", "Economics (ECO)");
            }
        } else if (ExamRecord.TYPE_SSC.equals(examType)) {
            if (ExamRecord.GROUP_SCIENCE.equals(group)) {
                return Arrays.asList("Higher Mathematics", "Biology", "Agricultural Studies");
            } else if (ExamRecord.GROUP_BUSINESS_STUDIES.equals(group) || ExamRecord.GROUP_HUMANITIES.equals(group)) {
                return Arrays.asList("Agricultural Studies", "Home Science");
            }
        }
        return Collections.emptyList();
    }

    /**
     * Legacy method for simple/general listing.
     */
    public static List<SubjectDefinition> getSubjects(String examType, String group) {
        List<SubjectDefinition> list = getCompulsorySubjects(examType, group);
        List<String> thirds = getThirdSubjectOptions(examType, group);
        if (!thirds.isEmpty()) {
            list.add(new SubjectDefinition(thirds.get(0), true, false));
        }
        List<String> fourths = getFourthSubjectOptions(examType, group);
        if (!fourths.isEmpty()) {
            list.add(new SubjectDefinition(fourths.get(0), false, true));
        }
        return list;
    }
}
