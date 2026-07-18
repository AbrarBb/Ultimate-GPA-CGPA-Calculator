package com.khatibstudio.gpacalc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.khatibstudio.gpacalc.data.entity.GradePoint;
import com.khatibstudio.gpacalc.data.entity.GradingScale;

import java.util.List;

@Dao
public interface GradingScaleDao {

    @Insert
    long insertScale(GradingScale scale);

    @Update
    void updateScale(GradingScale scale);

    @Delete
    void deleteScale(GradingScale scale);

    @Query("SELECT * FROM grading_scale WHERE mode = :mode ORDER BY isCustom ASC, name ASC")
    LiveData<List<GradingScale>> getScalesForMode(String mode);

    @Query("SELECT * FROM grading_scale WHERE mode = :mode ORDER BY isCustom ASC, name ASC")
    List<GradingScale> getScalesForModeSync(String mode);

    @Query("SELECT * FROM grading_scale WHERE id = :scaleId LIMIT 1")
    GradingScale getScaleById(int scaleId);

    @Insert
    long insertGradePoint(GradePoint gradePoint);

    @Insert
    void insertGradePoints(List<GradePoint> gradePoints);

    @Update
    void updateGradePoint(GradePoint gradePoint);

    @Delete
    void deleteGradePoint(GradePoint gradePoint);

    @Query("SELECT * FROM grade_point WHERE scaleId = :scaleId ORDER BY pointValue DESC")
    LiveData<List<GradePoint>> getGradePointsForScale(int scaleId);

    @Query("SELECT * FROM grade_point WHERE scaleId = :scaleId ORDER BY pointValue DESC")
    List<GradePoint> getGradePointsForScaleSync(int scaleId);

    @Query("DELETE FROM grade_point WHERE scaleId = :scaleId")
    void deleteGradePointsForScale(int scaleId);
}
