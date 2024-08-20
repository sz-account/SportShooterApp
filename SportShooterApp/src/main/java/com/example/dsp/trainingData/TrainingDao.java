package com.example.dsp.trainingData;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TrainingDao {

    @Query("SELECT * FROM Training")
    List<Training> getAllTrainings();

    @Query("SELECT COUNT(*) FROM Training")
    long getRowCount();

    @Query("SELECT * FROM User JOIN Training ON User.id = Training.fk_userId WHERE User.userId = :userId ORDER BY timestamp DESC LIMIT 1")
    Training getEntityWithBiggerTimestamp(String userId);

    @Insert
    long insertTraining(Training training);
}
