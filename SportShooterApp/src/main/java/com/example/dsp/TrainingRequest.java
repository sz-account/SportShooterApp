package com.example.dsp;

import com.example.dsp.trainingData.hit.Hit;

import java.util.Date;
import java.util.List;

public class TrainingRequest{

    public TrainingRequest() {

    }

    public Date date;
    public int targetSize;
    public int distance;
    public int targets;
    public int gameMode;
    public List<Hit> hits;

}
