package com.demo.ingredisearch.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static java.util.Collections.emptyList;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.favorites.FakeFavoritesSource;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource.DataStatus;
import com.demo.ingredisearch.util.LiveDataTestUtil;
import com.demo.ingredisearch.util.Resource;
import com.demo.ingredisearch.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class RecipeRepositoryTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // SUT
    RecipeRepository mRecipeRepository;

    FakeRemoteDataSource mRemoteDataSource;
    FakeFavoritesSource mFavoritesSource;

    @Before
    public void init() {
        mRemoteDataSource = new FakeRemoteDataSource(new SingleExecutors());
        mFavoritesSource = new FakeFavoritesSource();
        mRecipeRepository = RecipeRepository.getInstance(mFavoritesSource, mRemoteDataSource);
    }

    @After
    public void tearDown() {
        mRecipeRepository.destroy();
    }

    @Test
    public void searchRecipes_whenFailedByNetworkError_returnsErrorResponse() throws InterruptedException {
        // Arrange (Given)
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);

        // Act (When)
        mRecipeRepository.searchRecipes("some query");

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipes());
        assertThat(resource, is(Resource.error("Network Error", null)));
    }

    @Test
    public void searchRecipes_whenFailedWithHTTPError_returnsErrorResponse() throws InterruptedException {
        mRemoteDataSource.setDataStatus(DataStatus.HTTPError);

        // Act (When)
        mRecipeRepository.searchRecipes("some query");

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipes());
        assertThat(resource, is(Resource.error("HTTP Error", null)));
    }

    @Test
    public void searchRecipes_whenFailedWithAuthError_returnsErrorResponse() throws InterruptedException {
        mRemoteDataSource.setDataStatus(DataStatus.AuthError);

        // Act (When)
        mRecipeRepository.searchRecipes("some query");

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipes());
        assertThat(resource, is(Resource.error("Authorization Error", null)));
    }

    @Test
    public void searchRecipes_whenSucceedWithNullResult_returnsEmptyList() throws InterruptedException {

        // Act (When)
        mRecipeRepository.searchRecipes("a valid query causing empty result");

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipes());
        assertThat(resource, is(Resource.success(emptyList())));
    }

    @Test
    public void searchRecipes_whenSucceed_returnsRecipesList() throws InterruptedException {
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);

        // Act (When)
        mRecipeRepository.searchRecipes("a valid query");

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipes());
        assertThat(resource, is(Resource.success(TestData.mRecipes)));
    }

    @Test
    public void searchRecipe_whenFailedByNetworkError_returnsErrorResponse() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);

        // Act (When)
        mRecipeRepository.searchRecipe("some recipe id");

        // Assert (Then)
        Resource<Recipe> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipe());
        assertThat(resource, is(Resource.error("Network Error", null)));
    }

    @Test
    public void searchRecipe_whenFailedWithHTTPError_returnsErrorResponse() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.setDataStatus(DataStatus.HTTPError);

        // Act (When)
        mRecipeRepository.searchRecipe("some recipe id");

        // Assert (Then)
        Resource<Recipe> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipe());
        assertThat(resource, is(Resource.error("HTTP Error", null)));
    }

    @Test
    public void searchRecipe_whenFailedWithAuthError_returnsErrorResponse() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.setDataStatus(DataStatus.AuthError);

        // Act (When)
        mRecipeRepository.searchRecipe("some recipe id");

        // Assert (Then)
        Resource<Recipe> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipe());
        assertThat(resource, is(Resource.error("Authorization Error", null)));
    }

    @Test
    public void searchRecipe_whenSucceedWithNullResult_returnsNull() throws Exception {
        // Arrange (Given)

        // Act (When)
        mRecipeRepository.searchRecipe("a valid id causing null result");

        // Assert (Then)
        Resource<Recipe> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipe());
        assertThat(resource, is(Resource.success(null)));
    }

    @Test
    public void searchRecipe_whenSucceed_returnsRecipe() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.recipeDetails01, TestData.recipeDetails02);

        // Act (When)
        mRecipeRepository.searchRecipe(TestData.recipeDetails01.getRecipeId());

        // Assert (Then)
        Resource<Recipe> resource = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getRecipe());
        assertThat(resource, is(Resource.success(TestData.recipeDetails01)));
    }

    @Test
    public void getFavorites_noFavorites_returnEmptyList() throws Exception {
        // Arrange (Given)

        // Act (When)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getFavorites());

        // Assert (Then)
        assertThat(favorites, is(emptyList()));
    }

    @Test
    public void getFavorites_someFavorites_returnAll() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1, TestData.recipe2);

        // Act (When)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getFavorites());

        // Assert (Then)
        assertThat(favorites, is(TestData.mFavorites));
    }

    @Test
    public void addFavorites_noDuplicateId_addToFavoritesWithFavoriteStatusAsTrue() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1);

        // Act (When)
        mRecipeRepository.addFavorite(TestData.recipe2);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getFavorites());
        assertThat(favorites, is(TestData.mFavorites));
    }

    @Test
    public void addFavorites_recipeWithSameIdAlreadyExists_rejectAddition() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1);

        // Act (When)
        mRecipeRepository.addFavorite(TestData.recipe1);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getFavorites());
        assertThat(favorites, is(List.of(TestData.recipe1_favored)));
    }

    @Test
    public void removeFavorite_removesRecipeFromFavorites() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1);

        // Act (When)
        mRecipeRepository.removeFavorite(TestData.recipe1_favored);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getFavorites());
        assertThat(favorites, is(emptyList()));
    }

    @Test
    public void clearFavorites_removeAllFavorites() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1, TestData.recipe2);

        // Act (When)
        mRecipeRepository.clearFavorites();

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mRecipeRepository.getFavorites());
        assertThat(favorites, is(emptyList()));
    }

}