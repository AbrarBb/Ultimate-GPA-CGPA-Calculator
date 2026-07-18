package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subject")
public class Subject {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int examRecordId;

    @NonNull
    public String subjectName;

    @NonNull
    public String letterGrade;

    // True only for the optional / 4th subject. Only ONE subject per
    // ExamRecord should have this flag set to true.
    public boolean isOptionalFourth;

    public Subject(int examRecordId, @NonNull String subjectName,
                    @NonNull String letterGrade, boolean isOptionalFourth) {
        this.examRecordId = examRecordId;
        this.subjectName = subjectName;
        this.letterGrade = letterGrade;
        this.isOptionalFourth = isOptionalFourth;
    }
}
