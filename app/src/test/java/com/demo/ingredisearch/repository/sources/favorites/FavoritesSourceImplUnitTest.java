package com.demo.ingredisearch.repository.sources.favorites;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static java.util.Collections.emptyList;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.repository.sources.FavoritesSource;
import com.demo.ingredisearch.util.LiveDataTestUtil;
import com.demo.ingredisearch.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class FavoritesSourceImplUnitTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // SUT
    FavoritesSource mFavoritesSource;
    FavoriteDatabase database;

    @Before
    public void init() {
        database = createDatabase(ApplicationProvider.getApplicationContext());
        mFavoritesSource = new FavoritesSourceImpl(database, new SingleExecutors());
    }

    @After
    public void tearDown() {
        database.close();
    }

    private FavoriteDatabase createDatabase(Context context) {
        return Room.inMemoryDatabaseBuilder(
                context,
                FavoriteDatabase.class
        ).allowMainThreadQueries().build();
    }

    @Test
    public void getFavorites_noFavorites_returnEmptyList() {
        // Arrange (Given)
        LiveData<List<Recipe>> liveData = mFavoritesSource.getFavorites();

        Observer<List<Recipe>> observer = new Observer<>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                // Assert (Then)
                assertThat(recipes, is(emptyList()));
                liveData.removeObserver(this);
            }
        };

        // Act (When)
        liveData.observeForever(observer);
    }

    @Test
    public void getFavorites_someFavorites_returnAll() throws InterruptedException {
        // Arrange (Given)
        mFavoritesSource.addFavorite(TestData.recipe1);
        mFavoritesSource.addFavorite(TestData.recipe2);

        // Act (When)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());

        // Assert (Then)
        assertThat(favorites, hasSize(2));
        assertThat(favorites, is(List.of(TestData.recipe1_favored, TestData.recipe2_favored)));
    }

    @Test
    public void addFavorites_noDuplicateId_addToFavoritesWithFavoriteStatusAsTrue() throws InterruptedException {
        // Arrange (Given)
        mFavoritesSource.addFavorite(TestData.recipe1);

        // Act (When)
        mFavoritesSource.addFavorite(TestData.recipe2);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, hasSize(2));
    }

    @Test
    public void addFavorites_recipeWithSameIdAlreadyExists_rejectAddition() throws InterruptedException {
        // Arrange (Given)
        mFavoritesSource.addFavorite(TestData.recipe1);

        // Act (When)
        mFavoritesSource.addFavorite(TestData.recipe1);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, hasSize(1));
    }

    @Test
    public void removeFavorite_removesRecipeFromFavorites() throws InterruptedException {
        // Arrange (Given)
        mFavoritesSource.addFavorite(TestData.recipe1);

        // Act (When)
        mFavoritesSource.removeFavorite(TestData.recipe1);

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, hasSize(0));
    }

    @Test
    public void clearFavorites_removeAllFavorites() throws InterruptedException {
        // Arrange (Given)
        mFavoritesSource.addFavorite(TestData.recipe1);
        mFavoritesSource.addFavorite(TestData.recipe2);

        // Act (When)
        mFavoritesSource.clearFavorites();

        // Assert (Then)
        List<Recipe> favorites = LiveDataTestUtil.getOrAwaitValue(mFavoritesSource.getFavorites());
        assertThat(favorites, hasSize(0));
    }

}
