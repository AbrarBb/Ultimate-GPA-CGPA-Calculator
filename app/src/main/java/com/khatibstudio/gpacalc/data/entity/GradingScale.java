package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grading_scale")
public class GradingScale {

    public static final String MODE_SCHOOL = "SCHOOL";
    public static final String MODE_UNIVERSITY = "UNIVERSITY";

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name; // e.g. "SSC/HSC Standard", "EWU", "NSU", "Custom - My Scale"

    @NonNull
    public String mode; // MODE_SCHOOL or MODE_UNIVERSITY

    public boolean isCustom;

    public GradingScale(@NonNull String name, @NonNull String mode, boolean isCustom) {
        this.name = name;
        this.mode = mode;
        this.isCustom = isCustom;
    }
}
