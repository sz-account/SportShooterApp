package com.example.dsp.trainingData;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import java.util.Date;

@Entity
public class Training {

    @PrimaryKey
    public Long id;

    public Long fk_userId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "targetSize")
    public TargetSize targetSize;

    @ColumnInfo(name = "distance")
    public int distance;

    @ColumnInfo(name = "targets")
    public int targets;

    @ColumnInfo(name = "trainingMode")
    public TrainingModeEnum trainingMode;

    @ColumnInfo(name = "timeStamp")
    public Long timeStamp;
}
