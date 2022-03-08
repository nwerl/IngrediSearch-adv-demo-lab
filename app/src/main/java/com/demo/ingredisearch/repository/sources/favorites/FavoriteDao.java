package com.demo.ingredisearch.repository.sources.favorites;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.demo.ingredisearch.models.Recipe;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM recipe")
    LiveData<List<Recipe>> getFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe favorite);

    @Delete
    void delete(Recipe favorite);

    @Query("DELETE FROM recipe")
    void clear();
}
