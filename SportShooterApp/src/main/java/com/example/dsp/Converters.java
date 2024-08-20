package com.example.dsp;

import androidx.room.TypeConverter;

import com.example.dsp.trainingData.enums.TargetSize;

import java.util.Date;

public class Converters {

    @TypeConverter
    public static Date fromLong(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static TargetSize fromString(String value) {
        return value == null ? null : TargetSize.valueOf(value);
    }

    @TypeConverter
    public static String colorToString(TargetSize targetSize) {
        return targetSize == null ? null : targetSize.name();
    }
}