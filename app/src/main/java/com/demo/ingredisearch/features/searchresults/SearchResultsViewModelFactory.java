package com.demo.ingredisearch.features.searchresults;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.demo.ingredisearch.repository.RecipeRepository;

public class SearchResultsViewModelFactory implements ViewModelProvider.Factory {

    public SearchResultsViewModelFactory(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /* TODO */
    private final RecipeRepository recipeRepository;

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(SearchResultsViewModel.class))
            throw new IllegalArgumentException("No such viewmodel exists");

        return (T) new SearchResultsViewModel(recipeRepository);
    }
}
