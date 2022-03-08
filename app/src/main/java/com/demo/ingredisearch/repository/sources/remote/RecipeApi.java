package com.demo.ingredisearch.repository.sources.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {
    @GET("api/search")
    Call<RecipeSearchResponse> search(
            @Query("key") String key,
            @Query("q") String query
    );

    @GET("api/get")
    Call<RecipeResponse> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id
    );
}
