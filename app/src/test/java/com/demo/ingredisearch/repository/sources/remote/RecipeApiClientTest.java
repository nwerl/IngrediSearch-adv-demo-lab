package com.demo.ingredisearch.repository.sources.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static java.util.Collections.emptyList;

import androidx.annotation.NonNull;

import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.RemoteDataSource;
import com.demo.ingredisearch.repository.sources.ResponseCallback;
import com.demo.ingredisearch.util.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeApiClientTest {

    private class MyRecipeApiClient extends RecipeApiClient {
        @NonNull
        @Override
        protected ServiceGenerator getServiceGenerator() {
            return serviceGenerator;
        }
    }

    @Mock
    ServiceGenerator serviceGenerator;

    @Mock
    Call<RecipeSearchResponse> recipeSearchResponseCall;

    @Mock
    Response<RecipeSearchResponse> recipeSearchResponseResponse;

    @Mock
    Call<RecipeResponse> recipeResponseCall;

    @Mock
    Response<RecipeResponse> recipeResponseResponse;

    @Mock
    ResponseCallback<Recipe> recipeResponseCallback;

    // SUT
    RemoteDataSource mRemoteDataSource;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(serviceGenerator.getRecipesService(anyString()))
                .thenReturn(recipeSearchResponseCall);

        when(serviceGenerator.getRecipeService(anyString()))
                .thenReturn(recipeResponseCall);

        mRemoteDataSource = new MyRecipeApiClient();
    }

    @Test
    public void searchRecipes_whenFailedByNetworkError_returnsErrorResponse() {
        // Arrange (Given)
        doAnswer(invocation -> {
            Callback<RecipeSearchResponse> callback = invocation.getArgument(0);
            callback.onFailure(recipeSearchResponseCall, new IOException("Network Error"));
            return null;
        }).when(recipeSearchResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<List<Recipe>>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                fail("should not be called");
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                // Assert (Then)
                assertThat(response, is(Resource.error("Network Error", null)));
            }
        });
    }

    @Test
    public void searchRecipes_whenFailedWithHTTPError_returnsErrorResponse() {
        // Arrange (Given)
        when(recipeSearchResponseResponse.isSuccessful()).thenReturn(false);
        when(recipeSearchResponseResponse.message()).thenReturn("HTTP Error");
        doAnswer(invocation -> {
            Callback<RecipeSearchResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeSearchResponseCall, recipeSearchResponseResponse);
            return null;
        }).when(recipeSearchResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<List<Recipe>>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                fail("should not be called");
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                // Assert (Then)
                assertThat(response, is(Resource.error("HTTP Error", null)));
            }
        });

    }

    @Test
    public void searchRecipes_whenFailedWithAuthError_returnsErrorResponse() {
        // Arrange (Given)
        when(recipeSearchResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeSearchResponseResponse.code()).thenReturn(401);
        doAnswer(invocation -> {
            Callback<RecipeSearchResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeSearchResponseCall, recipeSearchResponseResponse);
            return null;
        }).when(recipeSearchResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<List<Recipe>>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                fail("should not be called");
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                // Assert (Then)
                assertThat(response, is(Resource.error("Authorization Error", null)));
            }
        });
    }

    @Test
    public void searchRecipes_whenSucceedWithNullResult_returnsEmptyList() {
        // Arrange (Given)
        when(recipeSearchResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeSearchResponseResponse.body()).thenReturn(null);
        doAnswer(invocation -> {
            Callback<RecipeSearchResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeSearchResponseCall, recipeSearchResponseResponse);
            return null;
        }).when(recipeSearchResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<List<Recipe>>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                assertThat(response, is(Resource.success(emptyList())));
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                // Assert (Then)
                fail("should not be called");
            }
        });
    }

    @Test
    public void searchRecipes_whenSucceed_returnsRecipesList() {
        // Arrange (Given)
        when(recipeSearchResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeSearchResponseResponse.body()).thenReturn(new RecipeSearchResponse(TestData.mRecipes));
        doAnswer(invocation -> {
            Callback<RecipeSearchResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeSearchResponseCall, recipeSearchResponseResponse);
            return null;
        }).when(recipeSearchResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<List<Recipe>>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                // Assert (Then)
                assertThat(response, is(Resource.success(TestData.mRecipes)));
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                fail("should not be called");
            }
        });
    }

    @Test
    public void searchRecipe_whenFailedByNetworkError_returnsErrorResponse() {
        // Arrange (Given)
        doAnswer(invocation -> {
            Callback<RecipeResponse> callback = invocation.getArgument(0);
            callback.onFailure(recipeResponseCall, new IOException("Network Error"));
            return null;
        }).when(recipeResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipe("some query id", new ResponseCallback<Recipe>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                fail("should not be called");
            }

            @Override
            public void onError(Resource<Recipe> response) {
                // Assert (Then)
                assertThat(response, is(Resource.error("Network Error", null)));
            }
        });
    }

    @Test
    public void searchRecipe_whenFailedWithHTTPError_returnsErrorResponse() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(false); // can omit
        when(recipeResponseResponse.message()).thenReturn("HTTP Error");
        doAnswer(invocation -> {
            Callback<RecipeResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeResponseCall, recipeResponseResponse);
            return null;
        }).when(recipeResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipe("some query id", new ResponseCallback<Recipe>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                fail("should not be called");
            }

            @Override
            public void onError(Resource<Recipe> response) {
                // Assert (Then)
                assertThat(response, is(Resource.error("HTTP Error", null)));
            }
        });
    }

    @Test
    public void searchRecipe_whenFailedWithAuthError_returnsErrorResponse() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeResponseResponse.code()).thenReturn(401);
        doAnswer(invocation -> {
            Callback<RecipeResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeResponseCall, recipeResponseResponse);
            return null;
        }).when(recipeResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipe("some query id", new ResponseCallback<>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                fail("should not be called");
            }

            @Override
            public void onError(Resource<Recipe> response) {
                // Assert (Then)
                assertThat(response, is(Resource.error("Authorization Error", null)));
            }
        });
    }

    @Test
    public void searchRecipe_whenSucceedWithNullResult_returnsNull() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeResponseResponse.body()).thenReturn(null);
        doAnswer(invocation -> {
            Callback<RecipeResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeResponseCall, recipeResponseResponse);
            return null;
        }).when(recipeResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipe(TestData.recipe1.getRecipeId(), new ResponseCallback<Recipe>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                assertThat(response, is(Resource.success(null)));
            }

            @Override
            public void onError(Resource<Recipe> response) {
                // Assert (Then)
                fail("should not be called");
            }
        });
    }



    @Test
    public void searchRecipe_whenSucceed_returnsRecipe() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeResponseResponse.body()).thenReturn(new RecipeResponse(TestData.recipeDetails01));
        doAnswer(invocation -> {
            Callback<RecipeResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeResponseCall, recipeResponseResponse);
            return null;
        }).when(recipeResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipe(TestData.recipe1.getRecipeId(), new ResponseCallback<Recipe>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                assertThat(response, is(Resource.success(TestData.recipeDetails01)));
            }

            @Override
            public void onError(Resource<Recipe> response) {
                // Assert (Then)
                fail("should not be called");
            }
        });
    }

    @Test
    public void searchRecipe_whenSucceed_returnsRecipe2() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeResponseResponse.body()).thenReturn(new RecipeResponse(TestData.recipeDetails01));

        doAnswer(invocation -> {
            Callback<RecipeResponse> callback = invocation.getArgument(0);
            callback.onResponse(recipeResponseCall, recipeResponseResponse);
            return null;
        }).when(recipeResponseCall).enqueue(isA(Callback.class));

        // Act (When)
        mRemoteDataSource.searchRecipe("valid query", recipeResponseCallback);

        // Assert (Then)
        verify(recipeResponseCallback).onDataAvailable(Resource.success(TestData.recipeDetails01));
        verifyNoMoreInteractions(recipeResponseCallback);
    }

    @Captor
    ArgumentCaptor<Callback<RecipeResponse>> mCaptor;

    @Test
    public void searchRecipe_whenSucceed_returnsRecipe3() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeResponseResponse.body()).thenReturn(new RecipeResponse(TestData.recipeDetails01));
        doNothing().when(recipeResponseCall).enqueue(mCaptor.capture());

        // Act (When)
        mRemoteDataSource.searchRecipe(TestData.recipe1.getRecipeId(), recipeResponseCallback);

        // Assert (Then)
        mCaptor.getValue().onResponse(recipeResponseCall, recipeResponseResponse);
        verify(recipeResponseCallback).onDataAvailable(Resource.success(TestData.recipeDetails01));
        verifyNoMoreInteractions(recipeResponseCallback);
    }

    @Test
    public void searchRecipe_whenSucceed_returnsRecipe4() {
        // Arrange (Given)
        when(recipeResponseResponse.isSuccessful()).thenReturn(true);
        when(recipeResponseResponse.body()).thenReturn(new RecipeResponse(TestData.recipeDetails01));

        // Act (When)
        mRemoteDataSource.searchRecipe(TestData.recipe1.getRecipeId(), recipeResponseCallback);

        // Assert (Then)
        verify(recipeResponseCall).enqueue(mCaptor.capture());
        mCaptor.getValue().onResponse(recipeResponseCall, recipeResponseResponse);
        verify(recipeResponseCallback).onDataAvailable(Resource.success(TestData.recipeDetails01));
        verifyNoMoreInteractions(recipeResponseCallback);
    }
}
