package com.demo.ingredisearch;

import android.content.Context;

import androidx.room.Room;

import com.demo.ingredisearch.repository.RecipeRepository;
import com.demo.ingredisearch.repository.sources.favorites.FavoriteDatabase;
import com.demo.ingredisearch.repository.sources.favorites.FavoritesSourceImpl;
import com.demo.ingredisearch.repository.sources.remote.RecipeApiClient;
import com.demo.ingredisearch.util.AppExecutors;

public class Injection {
    private RecipeRepository recipeRepository;
    private final Context context;

    public Injection(Context context) {
        this.context = context;
    }

    public RecipeRepository getRecipeRepository() {
        if (recipeRepository == null) {
            recipeRepository = RecipeRepository.getInstance(
                    new FavoritesSourceImpl(createDatabase(), new AppExecutors()),
                    new RecipeApiClient()
            );
        }
        return recipeRepository;
    }

    private FavoriteDatabase createDatabase() {
        return Room.databaseBuilder(
                context,
                FavoriteDatabase.class,
                "favorites"
        )
                .build();
    }

    public void setRecipeRepository(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }
}
