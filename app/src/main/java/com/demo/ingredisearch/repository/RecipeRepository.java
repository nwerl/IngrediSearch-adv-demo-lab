package com.demo.ingredisearch.repository;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.FavoritesSource;
import com.demo.ingredisearch.repository.sources.RemoteDataSource;
import com.demo.ingredisearch.repository.sources.ResponseCallback;
import com.demo.ingredisearch.util.EspressoIdlingResource;
import com.demo.ingredisearch.util.Resource;

import java.util.List;

public class RecipeRepository {

    private final FavoritesSource mFavoritesSource;
    private final RemoteDataSource mRemoteDataSource;

    private RecipeRepository(FavoritesSource favoritesSource, RemoteDataSource remoteDataSource) {
        this.mFavoritesSource = favoritesSource;
        this.mRemoteDataSource = remoteDataSource;
    }

    private static RecipeRepository INSTANCE = null;

    public static RecipeRepository getInstance(FavoritesSource favoritesSource, RemoteDataSource remoteDataSource) {
        if (INSTANCE == null) {
            synchronized (RecipeRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecipeRepository(favoritesSource, remoteDataSource);
                }
            }
        }
        return INSTANCE;
    }

    private final MutableLiveData<Resource<List<Recipe>>> mRecipes =
            new MutableLiveData<>(Resource.loading(null));

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return mRecipes;
    }

    private final MutableLiveData<Resource<Recipe>> mRecipe =
            new MutableLiveData<>(Resource.loading(null));

    public LiveData<Resource<Recipe>> getRecipe() {
        return mRecipe;
    }

    public void searchRecipes(String query) {
        EspressoIdlingResource.increment();
        mRecipes.setValue(Resource.loading(null));

        mRemoteDataSource.searchRecipes(query, new ResponseCallback<>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                mRecipes.postValue(response);
                EspressoIdlingResource.decrement();
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                mRecipes.postValue(response);
                EspressoIdlingResource.decrement();
            }
        });
    }

    public void searchRecipe(String recipeId) {
        EspressoIdlingResource.increment();
        mRecipes.setValue(Resource.loading(null));

        mRemoteDataSource.searchRecipe(recipeId, new ResponseCallback<Recipe>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                mRecipe.postValue(response);
                EspressoIdlingResource.decrement();
            }

            @Override
            public void onError(Resource<Recipe> response) {
                mRecipe.postValue(response);
                EspressoIdlingResource.decrement();
            }
        });
    }

    public LiveData<List<Recipe>> getFavorites() {
        return mFavoritesSource.getFavorites();
    }

    public void addFavorite(Recipe recipe) {
        mFavoritesSource.addFavorite(recipe);
    }

    public void removeFavorite(Recipe recipe) {
        mFavoritesSource.removeFavorite(recipe);
    }

    public void clearFavorites() {
        mFavoritesSource.clearFavorites();
    }

    @VisibleForTesting
    public void destroy() {
        INSTANCE = null;
    }

}
