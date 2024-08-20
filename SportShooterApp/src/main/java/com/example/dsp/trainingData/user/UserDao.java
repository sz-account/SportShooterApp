package com.example.dsp.trainingData.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Query("SELECT id FROM User WHERE userId = :userId")
    Long getIdOfUser(String userId);

    @Insert
    Long insertUser(User user);
}
