package com.demo.ingredisearch.repository.sources.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import static java.util.Collections.emptyList;

import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.ResponseCallback;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource.DataStatus;
import com.demo.ingredisearch.util.Resource;
import com.demo.ingredisearch.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class FakeRemoteDataSourceTest {
    // SUT
    FakeRemoteDataSource mRemoteDataSource;

    @Before
    public void init() {
        mRemoteDataSource = new FakeRemoteDataSource(new SingleExecutors());
    }

    @Test
    public void searchRecipes_whenFailedByNetworkError_returnsErrorResponse() {
        // Arrange (Given)
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<>() {
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
        mRemoteDataSource.setDataStatus(DataStatus.HTTPError);

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<>() {
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
        mRemoteDataSource.setDataStatus(DataStatus.AuthError);

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<>() {
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

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<>() {
            @Override
            public void onDataAvailable(Resource<List<Recipe>> response) {
                // Assert (Then)
                assertThat(response, is(Resource.success(emptyList())));
            }

            @Override
            public void onError(Resource<List<Recipe>> response) {
                fail("should not be called");
            }
        });
    }

    @Test
    public void searchRecipes_whenSucceed_returnsRecipesList() {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);

        // Act (When)
        mRemoteDataSource.searchRecipes("some query", new ResponseCallback<>() {
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
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);

        // Act (When)
        mRemoteDataSource.searchRecipe("some recipe id", new ResponseCallback<>() {
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
        mRemoteDataSource.setDataStatus(DataStatus.HTTPError);

        // Act (When)
        mRemoteDataSource.searchRecipe("some recipe id", new ResponseCallback<>() {
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
        mRemoteDataSource.setDataStatus(DataStatus.AuthError);

        // Act (When)
        mRemoteDataSource.searchRecipe("some recipe id", new ResponseCallback<>() {
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

        // Act (When)
        mRemoteDataSource.searchRecipe("some recipe id", new ResponseCallback<>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                // Assert (Then)
                assertThat(response, is(Resource.success(null)));
            }

            @Override
            public void onError(Resource<Recipe> response) {
                fail("should not be called");
            }
        });
    }

    @Test
    public void searchRecipe_whenSucceed_returnsRecipe() {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.recipeDetails01, TestData.recipeDetails02);

        // Act (When)
        mRemoteDataSource.searchRecipe(TestData.recipeDetails01.getRecipeId(), new ResponseCallback<>() {
            @Override
            public void onDataAvailable(Resource<Recipe> response) {
                // Assert (Then)
                assertThat(response, is(Resource.success(TestData.recipeDetails01)));
            }

            @Override
            public void onError(Resource<Recipe> response) {
                fail("should not be called");
            }
        });
    }

}