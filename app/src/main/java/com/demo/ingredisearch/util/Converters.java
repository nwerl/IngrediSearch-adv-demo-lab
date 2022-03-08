package com.demo.ingredisearch.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Converters {
    @TypeConverter
    public String arrayToJsonString(String[] values) {
        return new Gson().toJson(values);
    }

    @TypeConverter
    public String[] jsonStringToArray(String value) {
        Type type = new TypeToken<String[]>(){}.getType();

        return new Gson().fromJson(value, type);
    }
}
