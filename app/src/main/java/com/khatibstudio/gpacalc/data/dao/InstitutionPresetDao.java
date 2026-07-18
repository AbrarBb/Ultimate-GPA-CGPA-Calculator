package com.khatibstudio.gpacalc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.khatibstudio.gpacalc.data.entity.InstitutionPreset;

import java.util.List;

@Dao
public interface InstitutionPresetDao {

    @Insert
    long insertPreset(InstitutionPreset preset);

    @Insert
    void insertPresets(List<InstitutionPreset> presets);

    @Update
    void updatePreset(InstitutionPreset preset);

    @Delete
    void deletePreset(InstitutionPreset preset);

    @Query("SELECT * FROM institution_preset ORDER BY id ASC")
    LiveData<List<InstitutionPreset>> getAllPresets();

    @Query("SELECT * FROM institution_preset ORDER BY id ASC")
    List<InstitutionPreset> getAllPresetsSync();

    @Query("SELECT * FROM institution_preset WHERE id = :id LIMIT 1")
    InstitutionPreset getPresetById(int id);

    @Query("SELECT * FROM institution_preset WHERE name = :name LIMIT 1")
    InstitutionPreset getPresetByName(String name);
}
