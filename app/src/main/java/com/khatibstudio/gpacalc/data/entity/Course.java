package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "course")
public class Course {

    /** Most recent attempt fully replaces the old attempt — used by EWU, BRAC, NU. */
    public static final String RETAKE_RULE_REPLACE = "REPLACE";

    /**
     * Replace only if the original grade is in the institution's retakeEligibleGrades list.
     * Grades above the threshold keep the original. Used by NSU.
     */
    public static final String RETAKE_RULE_REPLACE_CONDITIONAL = "REPLACE_CONDITIONAL";

    /** All attempts are averaged into one synthetic grade — uncommon, kept for custom scales. */
    public static final String RETAKE_RULE_AVERAGE = "AVERAGE";

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int semesterId;

    @NonNull
    public String courseName;

    public double creditHours;

    @NonNull
    public String letterGrade;

    public boolean isRetake;

    // If isRetake is true, references the id of the original course being retaken.
    // 0 means no previous record linked (first-time retake entry or unknown history).
    public int originalCourseId;

    // Raw marks stored alongside the letter grade for institutions that support
    // marks entry (NU, BRAC). -1 means marks were not entered.
    public double rawMarks;

    public Course(int semesterId, @NonNull String courseName, double creditHours,
                  @NonNull String letterGrade, boolean isRetake, int originalCourseId) {
        this.semesterId = semesterId;
        this.courseName = courseName;
        this.creditHours = creditHours;
        this.letterGrade = letterGrade;
        this.isRetake = isRetake;
        this.originalCourseId = originalCourseId;
        this.rawMarks = -1;
    }
}
