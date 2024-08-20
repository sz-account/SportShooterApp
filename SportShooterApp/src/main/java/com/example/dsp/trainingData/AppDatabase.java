package com.example.dsp.trainingData;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.dsp.Converters;
import com.example.dsp.trainingData.hit.Hit;
import com.example.dsp.trainingData.hit.HitDao;
import com.example.dsp.trainingData.user.User;
import com.example.dsp.trainingData.user.UserDao;

@Database(entities = { Hit.class, Training.class, User.class}, version = 28, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract TrainingWithHitsDao trainingWithHitsDao();
    public abstract TrainingDao trainingDao();
    public abstract HitDao hitsDao();
    public abstract UserDao userDao();

    protected AppDatabase(){};

    private static final String DB_NAME = "database";
    private static volatile AppDatabase instance;

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                        context,
                        AppDatabase.class,
                        DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }
}