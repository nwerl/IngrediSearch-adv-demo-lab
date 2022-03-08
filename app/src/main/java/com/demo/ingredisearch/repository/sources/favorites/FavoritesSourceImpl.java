package com.demo.ingredisearch.repository.sources.favorites;

import androidx.lifecycle.LiveData;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.FavoritesSource;
import com.demo.ingredisearch.util.AppExecutors;

import java.util.List;

public class FavoritesSourceImpl implements FavoritesSource {
    private final FavoriteDatabase database;
    private final FavoriteDao dao;
    private final AppExecutors mAppExecutors;

    public FavoritesSourceImpl(FavoriteDatabase database, AppExecutors appExecutors) {
        this.database = database;
        dao = database.favoritesDao();
        mAppExecutors = appExecutors;
    }

    // Ignore error condition just for simplicity
    @Override
    public LiveData<List<Recipe>> getFavorites() {
        return dao.getFavorites();
    }

    @Override
    public void addFavorite(Recipe recipe) {
        Recipe temp = new Recipe(recipe);
        temp.setFavorite(true);

        mAppExecutors.diskIO().execute(() ->
                database.runInTransaction(() -> dao.insert(temp))
        );
    }

    @Override
    public void removeFavorite(Recipe recipe) {
        mAppExecutors.diskIO().execute(() ->
                database.runInTransaction(() -> dao.delete(recipe))
        );
    }

    @Override
    public void clearFavorites() {
        mAppExecutors.diskIO().execute(() ->
                database.runInTransaction(dao::clear)
        );
    }

}

