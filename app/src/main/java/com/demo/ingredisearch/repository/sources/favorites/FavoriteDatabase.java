package com.demo.ingredisearch.repository.sources.favorites;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.util.Converters;

@TypeConverters(Converters.class)
@Database(entities = {Recipe.class}, exportSchema = false, version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {

    abstract FavoriteDao favoritesDao();
}
