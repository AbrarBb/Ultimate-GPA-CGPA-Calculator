package com.khatibstudio.gpacalc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admission_cutoff")
public class AdmissionCutoff {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String universityName;

    @NonNull
    public String programName;

    public double minGpaRequired;

    public String formulaNote; // e.g. "SSC x5 + HSC x5 + Written test /100"

    public AdmissionCutoff(@NonNull String universityName, @NonNull String programName,
                            double minGpaRequired, String formulaNote) {
        this.universityName = universityName;
        this.programName = programName;
        this.minGpaRequired = minGpaRequired;
        this.formulaNote = formulaNote;
    }
}
