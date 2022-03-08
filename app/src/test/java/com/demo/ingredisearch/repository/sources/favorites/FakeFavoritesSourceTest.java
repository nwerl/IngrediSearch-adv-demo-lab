package com.demo.ingredisearch.repository.sources.favorites;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static java.util.Collections.emptyList;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class FakeFavoritesSourceTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // SUT
    FakeFavoritesSource mFavoritesSource;

    @Before
    public void init() {
        mFavoritesSource = new FakeFavoritesSource();
    }

    @Test
    public void getFavorites_noFavorites_returnEmptyList() throws Exception {
        // Arrange (Given)

        // Act (When)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());

        // Assert (Then)
        assertThat(favorites, is(emptyList()));
    }

    @Test
    public void getFavorites_someFavorites_returnAll() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.mFavorites);

        // Act (When)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());

        // Assert (Then)
        assertThat(favorites, is(TestData.mFavorites));
    }

    @Test
    public void addFavorites_noDuplicateId_addToFavoritesWithFavoriteStatusAsTrue() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1);

        // Act (When)
        mFavoritesSource.addFavorite(TestData.recipe2);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, is(List.of(TestData.recipe1_favored, TestData.recipe2_favored)));
    }

    @Test
    public void addFavorites_recipeWithSameIdAlreadyExists_rejectAddition() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1);

        // Act (When)
        mFavoritesSource.addFavorite(TestData.recipe1);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, is(List.of(TestData.recipe1_favored)));
    }

    @Test
    public void removeFavorite_removesRecipeFromFavorites() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.mFavorites);

        // Act (When)
        mFavoritesSource.removeFavorite(TestData.recipe1_favored);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, is(List.of(TestData.recipe2_favored)));
    }

    @Test
    public void clearFavorites_removeAllFavorites() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.mFavorites);

        // Act (When)
        mFavoritesSource.clearFavorites();

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, is(emptyList()));
    }
}