package com.example.dsp.ui.shootingMenuFragment;

public class TrainingMode {

    public String gameModeCode;
    public String description;
    public int maxTargets;

    public TrainingMode(String gameModeCode, String description, int maxTargets)
    {
        this.gameModeCode = gameModeCode;
        this.description = description;
        this.maxTargets = maxTargets;
    }
}
