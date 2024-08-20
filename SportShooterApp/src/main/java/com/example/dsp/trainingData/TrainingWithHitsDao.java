package com.example.dsp.trainingData;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.dsp.trainingData.hit.Hit;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import java.util.List;

@Dao
public interface TrainingWithHitsDao {

    @Transaction
    @Query("SELECT * FROM Training")
    public List<TrainingWithHits> getTrainingWithHits();

    @Transaction
    @Query("SELECT * FROM User JOIN Training  ON User.id = Training.fk_userId WHERE targetSize = :targetSize and distance = :distance and targets = :targetCount and trainingMode = :gameMode and User.userId = :userId")
    List<TrainingWithHits> getByType(TargetSize targetSize, int distance, int targetCount, TrainingModeEnum gameMode, String userId);

    @Transaction
    @Query("SELECT * FROM User JOIN Training ON User.id = Training.fk_userId WHERE timeStamp IS NULL and User.userId = :userId")
    List<TrainingWithHits> getNew(String userId);

    @Transaction
    @Query("DELETE FROM Training WHERE timeStamp IS NULL AND fk_userId = (SELECT id FROM User as u WHERE u.userId = :userId)")
    void deleteRowsWithNullField(String userId);

    @Insert
    void insertTrainingWithHits(Training training, List<Hit> hits);
}
