package com.demo.ingredisearch.features.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.RecipeRepository;
import com.demo.ingredisearch.util.Event;
import com.demo.ingredisearch.util.Resource;

import java.util.List;

public class FavoritesViewModel extends ViewModel {
    private final RecipeRepository mRecipeRepository;

    public FavoritesViewModel(RecipeRepository mRecipeRepository) {
        this.mRecipeRepository = mRecipeRepository;
    }

    private final MutableLiveData<Resource<List<Recipe>>> mFavorites =
            new MutableLiveData<>(Resource.loading(null));

    public LiveData<Resource<List<Recipe>>> getFavorites() {
        LiveData<List<Recipe>> favorites = mRecipeRepository.getFavorites();
        return Transformations.switchMap(favorites, resource -> {
            mFavorites.setValue(Resource.success(resource));
            return mFavorites;
        });
    }

    public void removeFavorite(Recipe recipe) {
        mRecipeRepository.removeFavorite(recipe);
    }

    public void clearFavorites() {
        mRecipeRepository.clearFavorites();
    }

    public void requestToNavToDetails(String recipeId) {
        mNavToDetails.setValue(new Event<>(recipeId));
    }

    private final MutableLiveData<Event<String>> mNavToDetails = new MutableLiveData<>();

    public LiveData<Event<String>> navToRecipeDetails() {
        return mNavToDetails;
    }
}
