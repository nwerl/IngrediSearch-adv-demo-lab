package com.demo.ingredisearch.features.searchresults;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.RecipeRepository;
import com.demo.ingredisearch.util.Event;
import com.demo.ingredisearch.util.Resource;
import com.demo.ingredisearch.util.Status;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsViewModel extends ViewModel {

    private final RecipeRepository mRecipeRepository;

    public SearchResultsViewModel(RecipeRepository recipeRepository) {
        mRecipeRepository = recipeRepository;
    }

    public void search(String query) {
        mRecipeRepository.searchRecipes(query);
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return Transformations.switchMap(mRecipeRepository.getRecipes(), resource ->
                Transformations.map(mRecipeRepository.getFavorites(), favorites -> {
                    if (resource.status == Status.SUCCESS) {
                        return Resource.success(decorate(resource.data, favorites));
                    } else {
                        return resource;
                    }
                }));
    }

    private List<Recipe> decorate(List<Recipe> recipes, List<Recipe> favorites) {
        return recipes.stream().map(recipe -> {
            if (favorites.stream().anyMatch(f -> f.getRecipeId().equals(recipe.getRecipeId()))) {
                Recipe temp = new Recipe(recipe);
                temp.setFavorite(true);
                return temp;
            } else {
                return recipe;
            }
        }).collect(Collectors.toList());
    }

    public void markFavorite(Recipe recipe) {
        mRecipeRepository.addFavorite(recipe);
    }

    public void unmarkFavorite(Recipe recipe) {
        mRecipeRepository.removeFavorite(recipe);
    }

    private final MutableLiveData<Event<String>> mNavToRecipeDetails = new MutableLiveData<>();
    public LiveData<Event<String>> navToRecipeDetails() {
        return mNavToRecipeDetails;
    }

    public void requestNavToRecipeDetails(Recipe recipe) {
        mNavToRecipeDetails.setValue(new Event<>(recipe.getRecipeId()));
    }
}
