package com.example.dsp.trainingData.hit;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HitDao {

    @Query("SELECT * FROM Hit")
    List<Hit> getAllHits();

    @Insert
    void insertHit(Hit hit);

    @Insert
    void insertAll(List<Hit> hitList);
}
