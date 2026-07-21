package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class Profile {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    @NonNull
    public String activeMode; // GradingScale.MODE_SCHOOL or MODE_UNIVERSITY

    public String degreeType; // "Honours" / "Masters" / null
    public String major; // e.g. "CSE", "Accounting" / null

    public Profile(@NonNull String name, @NonNull String activeMode) {
        this.name = name;
        this.activeMode = activeMode;
    }
}
