package com.khatibstudio.gpacalc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.khatibstudio.gpacalc.data.entity.Course;
import com.khatibstudio.gpacalc.data.entity.Semester;

import java.util.List;

@Dao
public interface UniversityDao {

    @Insert
    long insertSemester(Semester semester);

    @Update
    void updateSemester(Semester semester);

    @Delete
    void deleteSemester(Semester semester);

    @Query("SELECT * FROM semester WHERE profileId = :profileId ORDER BY sortOrder ASC")
    LiveData<List<Semester>> getSemestersForProfile(int profileId);

    @Query("SELECT * FROM semester WHERE profileId = :profileId ORDER BY sortOrder ASC")
    List<Semester> getSemestersForProfileSync(int profileId);

    @Query("SELECT * FROM semester WHERE id = :semesterId LIMIT 1")
    Semester getSemesterById(int semesterId);

    @Insert
    long insertCourse(Course course);

    @Update
    void updateCourse(Course course);

    @Delete
    void deleteCourse(Course course);

    @Query("SELECT * FROM course WHERE semesterId = :semesterId ORDER BY id ASC")
    LiveData<List<Course>> getCoursesForSemester(int semesterId);

    @Query("SELECT * FROM course WHERE semesterId = :semesterId ORDER BY id ASC")
    List<Course> getCoursesForSemesterSync(int semesterId);

    @Query("SELECT c.* FROM course c INNER JOIN semester s ON c.semesterId = s.id WHERE s.profileId = :profileId")
    List<Course> getAllCoursesForProfileSync(int profileId);
}
