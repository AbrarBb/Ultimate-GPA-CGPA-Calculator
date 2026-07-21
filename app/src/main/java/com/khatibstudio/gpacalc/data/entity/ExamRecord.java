package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exam_record")
public class ExamRecord {

    public static final String TYPE_SSC = "SSC";
    public static final String TYPE_HSC = "HSC";

    public static final String GROUP_SCIENCE = "SCIENCE";
    public static final String GROUP_BUSINESS_STUDIES = "BUSINESS_STUDIES";
    public static final String GROUP_HUMANITIES = "HUMANITIES";

    // Backward-compat aliases for old constant names
    /** @deprecated Use {@link #GROUP_BUSINESS_STUDIES} instead. */
    @Deprecated
    public static final String GROUP_COMMERCE = GROUP_BUSINESS_STUDIES;
    /** @deprecated Use {@link #GROUP_HUMANITIES} instead. */
    @Deprecated
    public static final String GROUP_ARTS = GROUP_HUMANITIES;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String examType; // TYPE_SSC or TYPE_HSC

    @NonNull
    public String group; // GROUP_SCIENCE / BUSINESS_STUDIES / HUMANITIES

    public int scaleId;
    public int profileId;

    public ExamRecord(@NonNull String examType, @NonNull String group, int scaleId, int profileId) {
        this.examType = examType;
        this.group = group;
        this.scaleId = scaleId;
        this.profileId = profileId;
    }
}
