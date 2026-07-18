package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grade_point")
public class GradePoint {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int scaleId;

    @NonNull
    public String letterGrade; // "A+", "A", "A-", "B", ...

    public double pointValue; // e.g. 5.00, 4.00, 3.50 ...

    // Used only for SCHOOL mode, to auto-derive grade from marks entry.
    // Leave both at -1 for UNIVERSITY mode scales where marks aren't used.
    public double minMarks;
    public double maxMarks;

    public GradePoint(int scaleId, @NonNull String letterGrade, double pointValue,
                       double minMarks, double maxMarks) {
        this.scaleId = scaleId;
        this.letterGrade = letterGrade;
        this.pointValue = pointValue;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
    }
}
