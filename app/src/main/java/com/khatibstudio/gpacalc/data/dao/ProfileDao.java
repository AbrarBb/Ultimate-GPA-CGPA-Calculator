package com.khatibstudio.gpacalc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.khatibstudio.gpacalc.data.entity.AdmissionCutoff;
import com.khatibstudio.gpacalc.data.entity.Profile;

import java.util.List;

@Dao
public interface ProfileDao {

    @Insert
    long insertProfile(Profile profile);

    @Update
    void updateProfile(Profile profile);

    @Delete
    void deleteProfile(Profile profile);

    @Query("SELECT * FROM profile ORDER BY id ASC")
    LiveData<List<Profile>> getAllProfiles();

    @Query("SELECT * FROM profile WHERE id = :profileId LIMIT 1")
    Profile getProfileById(int profileId);

    @Insert
    void insertCutoffs(List<AdmissionCutoff> cutoffs);

    @Query("SELECT * FROM admission_cutoff WHERE minGpaRequired <= :gpa ORDER BY minGpaRequired DESC")
    List<AdmissionCutoff> getEligiblePrograms(double gpa);

    @Query("SELECT * FROM admission_cutoff ORDER BY universityName ASC")
    LiveData<List<AdmissionCutoff>> getAllCutoffs();
}
