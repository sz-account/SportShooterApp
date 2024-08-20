package com.example.dsp.trainingData.user;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    public User() {}

    public User(String userId)
    {
        this.userId = userId;
    }

    @PrimaryKey
    public Long id;

    public String userId;
}
