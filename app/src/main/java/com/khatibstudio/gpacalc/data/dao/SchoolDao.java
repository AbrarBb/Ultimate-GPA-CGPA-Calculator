package com.khatibstudio.gpacalc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.khatibstudio.gpacalc.data.entity.ExamRecord;
import com.khatibstudio.gpacalc.data.entity.Subject;

import java.util.List;

@Dao
public interface SchoolDao {

    @Insert
    long insertExamRecord(ExamRecord record);

    @Update
    void updateExamRecord(ExamRecord record);

    @Delete
    void deleteExamRecord(ExamRecord record);

    @Query("SELECT * FROM exam_record WHERE profileId = :profileId ORDER BY id ASC")
    LiveData<List<ExamRecord>> getExamRecordsForProfile(int profileId);

    @Query("SELECT * FROM exam_record WHERE profileId = :profileId AND examType = :examType LIMIT 1")
    ExamRecord getExamRecordByType(int profileId, String examType);

    @Query("SELECT * FROM exam_record WHERE id = :examRecordId LIMIT 1")
    ExamRecord getExamRecordById(int examRecordId);

    @Insert
    long insertSubject(Subject subject);

    @Update
    void updateSubject(Subject subject);

    @Delete
    void deleteSubject(Subject subject);

    @Query("SELECT * FROM subject WHERE examRecordId = :examRecordId ORDER BY id ASC")
    LiveData<List<Subject>> getSubjectsForExam(int examRecordId);

    @Query("SELECT * FROM subject WHERE examRecordId = :examRecordId ORDER BY id ASC")
    List<Subject> getSubjectsForExamSync(int examRecordId);
}
