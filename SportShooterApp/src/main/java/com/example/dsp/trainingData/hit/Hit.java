package com.example.dsp.trainingData.hit;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.dsp.trainingData.Training;

@Entity(foreignKeys = @ForeignKey(entity = Training.class,
        parentColumns = "id",
        childColumns = "trainingId",
        onDelete = ForeignKey.CASCADE))
public class Hit {

    @PrimaryKey
    public Long id;

    public Long trainingId;

    @ColumnInfo(name = "time")
    public Long time;
}
