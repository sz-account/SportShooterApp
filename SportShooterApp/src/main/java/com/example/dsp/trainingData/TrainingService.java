package com.example.dsp.trainingData;

import android.content.Context;
import com.example.dsp.MainActivity;
import com.example.dsp.trainingData.hit.Hit;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import com.example.dsp.trainingData.user.User;
import java.util.Date;
import java.util.List;

public class TrainingService {

    AppDatabase instance;


    public TrainingService(Context context) {
        instance = AppDatabase.getInstance(context);
    }

    public void insert(TargetSize targetSize, int distance, TrainingModeEnum trainingMode, int numberOfTargets, List<Hit> hitList)
    {
        Long userId = instance.userDao().getIdOfUser(MainActivity.account.getId());

        if (userId == null)
            userId = instance.userDao().insertUser(new User(MainActivity.account.getId()));

        Training training = new Training();
        training.fk_userId = userId;
        training.date = new Date();
        training.targetSize = targetSize;
        training.distance = distance;
        training.trainingMode = trainingMode;
        training.targets = numberOfTargets;

        Long trainingId = instance.trainingDao().insertTraining(training);

        for (Hit h: hitList) {
            h.trainingId = trainingId;
        }

        instance.hitsDao().insertAll(hitList);
    }

}
