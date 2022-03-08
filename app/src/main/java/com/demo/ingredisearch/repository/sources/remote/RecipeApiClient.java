package com.demo.ingredisearch.repository.sources.remote;

import androidx.annotation.NonNull;

import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.RemoteDataSource;
import com.demo.ingredisearch.repository.sources.ResponseCallback;
import com.demo.ingredisearch.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.Collections.emptyList;

public class RecipeApiClient implements RemoteDataSource {

    private final ServiceGenerator mServiceGenerator = getServiceGenerator();

    @NonNull
    protected ServiceGenerator getServiceGenerator() {
        return new ServiceGenerator();
    }

    @Override
    public void searchRecipes(String query, ResponseCallback<List<Recipe>> callback) {
        Call<RecipeSearchResponse> call = mServiceGenerator.getRecipesService(query);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RecipeSearchResponse> call, @NonNull Response<RecipeSearchResponse> response) {
                RecipeSearchResponse searchResponse = response.body();
                if (response.isSuccessful()) {
                    if (response.code() == 401) { // Unauthorised error
                        callback.onError(Resource.error("Authorization Error", null));
                    } else if (searchResponse == null) {
                        callback.onDataAvailable(Resource.success(emptyList()));
                    } else {
                        callback.onDataAvailable(Resource.success(searchResponse.getRecipes()));
                    }
                } else {
                    callback.onError(Resource.error(response.message() , null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeSearchResponse> call, @NonNull Throwable throwable) {
                callback.onError(Resource.error(throwable.getMessage(), null));
            }
        });
    }

    @Override
    public void searchRecipe(String recipeId, ResponseCallback<Recipe> callback) {
        Call<RecipeResponse> call = mServiceGenerator.getRecipeService(recipeId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                RecipeResponse recipe = response.body();
                if (response.isSuccessful()) {
                    if (response.code() == 401) { // Unauthorised error
                        callback.onError(Resource.error("Authorization Error", null));
                    } else if (recipe == null) {
                        callback.onDataAvailable(Resource.success(null));
                    } else {
                        callback.onDataAvailable(Resource.success(recipe.getRecipe()));
                    }
                } else {
                    callback.onError(Resource.error(response.message() , null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable throwable) {
                callback.onError(Resource.error(throwable.getMessage(), null));
            }
        });
    }

}
