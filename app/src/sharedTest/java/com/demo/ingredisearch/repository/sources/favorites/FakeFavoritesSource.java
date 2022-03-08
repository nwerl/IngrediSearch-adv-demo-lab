package com.demo.ingredisearch.repository.sources.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.FavoritesSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FakeFavoritesSource implements FavoritesSource {
    private final List<Recipe> favTable = new ArrayList<>();

    private final MutableLiveData<List<Recipe>> mFavorites
            = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Recipe>> getFavorites() {
        return mFavorites;
    }

    private void refresh() {
        mFavorites.postValue(favTable);
    }

    public void addFavorite(Recipe recipe) {
        if (contains(recipe)) return;

        Recipe newFavorite = new Recipe(recipe);
        newFavorite.setFavorite(true);
        favTable.add(newFavorite);

        refresh();
    }

    private boolean contains(Recipe recipe) {
        return favTable.stream().anyMatch(recipe::isSameAs);
    }

    public void removeFavorite(Recipe recipe) {
        if (favTable.removeIf(recipe::isSameAs)) {
            refresh();
        }
    }

    public void clearFavorites() {
        favTable.clear();
        refresh();
    }

    public void prepareFavorites(List<Recipe> recipes) {
        recipes.forEach(recipe -> {
            Recipe temp = new Recipe(recipe);
            temp.setFavorite(true);
            favTable.add(temp);
        });
        refresh();
    }

    public void prepareFavorites(Recipe... recipes) {
        prepareFavorites(Arrays.asList(recipes));
    }
}
