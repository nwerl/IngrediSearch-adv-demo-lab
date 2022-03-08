package com.demo.ingredisearch.features.searchresults;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static java.util.Collections.emptyList;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.RecipeRepository;
import com.demo.ingredisearch.repository.sources.favorites.FakeFavoritesSource;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource.DataStatus;
import com.demo.ingredisearch.util.Event;
import com.demo.ingredisearch.util.LiveDataTestUtil;
import com.demo.ingredisearch.util.Resource;
import com.demo.ingredisearch.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // SUT
    SearchResultsViewModel mViewModel;

    FakeFavoritesSource mFavoritesSource;
    FakeRemoteDataSource mRemoteDataSource;
    RecipeRepository mRecipeRepository;

    @Before
    public void init() {
        mFavoritesSource = new FakeFavoritesSource();
        mRemoteDataSource = new FakeRemoteDataSource(new SingleExecutors());
        mRecipeRepository = RecipeRepository.getInstance(mFavoritesSource, mRemoteDataSource);
        mViewModel = new SearchResultsViewModel(mRecipeRepository);
    }

    @After
    public void tearDown() {
        mRecipeRepository.destroy();
    }

    @Test
    public void searchRecipes_allNonFavorites_displayRecipesAsTheyAre() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mViewModel.search("eggs");

        // Act (When)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mViewModel.getRecipes());

        // Assert (Then)
        assertThat(resource, is(Resource.success(TestData.mRecipes)));
    }

    @Test
    public void searchRecipes_emptyRecipes_displayNoRecipes() throws Exception {
        // Arrange (Given)
        mViewModel.search("eggs");

        // Act (When)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mViewModel.getRecipes());

        // Assert (Then)
        assertThat(resource, is(Resource.success(emptyList())));
    }

    @Test
    public void searchRecipes_networkError_displayRetry() throws Exception {
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);
        mViewModel.search("eggs");

        // Act (When)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mViewModel.getRecipes());

        // Assert (Then)
        assertThat(resource, is(Resource.error("Network Error", null)));
    }

    @Test
    public void searchRecipes_withSomeFavorites_displayRecipesAsDecorated() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mFavoritesSource.prepareFavorites(TestData.mFavorites);
        mViewModel.search("eggs");

        // Act (When)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mViewModel.getRecipes());

        // Assert (Then)
        assertThat(status(resource.data), is(List.of(true, true, false, false)));
    }

    private List<Boolean> status(List<Recipe> recipes) {
        return recipes.stream().map(Recipe::isFavorite).collect(Collectors.toList());
    }

    @Test
    public void markFavorite_reloadUpdatedRecipes() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mViewModel.search("eggs");

        // Act (When)
        mViewModel.markFavorite(TestData.recipe1);

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mViewModel.getRecipes());
        assertThat(status(resource.data), is(List.of(true, false, false, false)));
    }

    @Test
    public void unMarkFavorite_reloadUpdatedRecipes() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mFavoritesSource.prepareFavorites(TestData.mFavorites);
        mViewModel.search("eggs");

        // Act (When)
        mViewModel.unmarkFavorite(TestData.recipe1_favored);

        // Assert (Then)
        Resource<List<Recipe>> resource = LiveDataTestUtil.getOrAwaitValue(mViewModel.getRecipes());
        assertThat(status(resource.data), is(List.of(false, true, false, false)));
    }

    @Test
    public void requestToRecipeDetails_shouldTriggerNavToRecipeDetails() throws Exception {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mViewModel.search("eggs");

        // Act (When)
        mViewModel.requestNavToRecipeDetails(TestData.recipe1);

        // Assert (Then)
        Event<String> event = LiveDataTestUtil.getOrAwaitValue(mViewModel.navToRecipeDetails());
        assertThat(event.getContentIfNotHandled(), is(TestData.recipeDetails01.getRecipeId()));
    }

}
