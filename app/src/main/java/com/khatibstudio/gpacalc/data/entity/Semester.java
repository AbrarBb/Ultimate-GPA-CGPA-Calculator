package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "semester")
public class Semester {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name; // e.g. "Spring 2026"

    public int scaleId;
    public int profileId;

    // order of the semester for trend charting / sequencing
    public int sortOrder;

    public Semester(@NonNull String name, int scaleId, int profileId, int sortOrder) {
        this.name = name;
        this.scaleId = scaleId;
        this.profileId = profileId;
        this.sortOrder = sortOrder;
    }
}
