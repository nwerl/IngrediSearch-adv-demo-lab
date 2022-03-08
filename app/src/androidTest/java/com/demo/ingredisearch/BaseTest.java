package com.demo.ingredisearch;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;

import com.demo.ingredisearch.repository.RecipeRepository;
import com.demo.ingredisearch.repository.sources.favorites.FakeFavoritesSource;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource;
import com.demo.ingredisearch.util.AppExecutors;
import com.demo.ingredisearch.util.EspressoIdlingResource;
import com.demo.ingredisearch.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;

public class BaseTest {

    protected FakeFavoritesSource mFavoritesSource;
    protected FakeRemoteDataSource mRemoteDataSource;
    protected RecipeRepository mRecipeRepository;

    @Before
    public void init() {
        mFavoritesSource = new FakeFavoritesSource();
        mRemoteDataSource = new FakeRemoteDataSource(new SingleExecutors());
//        mRemoteDataSource = new FakeRemoteDataSource(new AppExecutors());
//        FakeRemoteDataSource.FAKE_NETWORK_DELAY = 1000L;
        mRecipeRepository = RecipeRepository.getInstance(mFavoritesSource, mRemoteDataSource);

        RecipeApplication app = ApplicationProvider.getApplicationContext();
        app.getInjection().setRecipeRepository(mRecipeRepository);

        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() {
        mRecipeRepository.destroy();

        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

}
