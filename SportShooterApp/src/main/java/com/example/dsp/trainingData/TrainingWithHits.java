package com.example.dsp.trainingData;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.dsp.trainingData.hit.Hit;
import java.util.List;

public class TrainingWithHits {

    @Embedded
    public Training training;
    @Relation(
            parentColumn = "id",
            entityColumn = "trainingId"
    )
    public List<Hit> hits;
}
