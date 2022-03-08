package com.demo.ingredisearch.repository.sources;

import androidx.lifecycle.LiveData;

import com.demo.ingredisearch.models.Recipe;

import java.util.List;

public interface FavoritesSource {
    // Ignore error condition just for simplicity
    LiveData<List<Recipe>> getFavorites();

    void addFavorite(Recipe recipe);

    void removeFavorite(Recipe recipe);

    void clearFavorites();
}
