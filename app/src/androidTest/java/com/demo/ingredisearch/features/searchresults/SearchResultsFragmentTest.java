package com.demo.ingredisearch.features.searchresults;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.demo.ingredisearch.util.CustomViewActions.clickChildWithId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.demo.ingredisearch.BaseTest;
import com.demo.ingredisearch.R;
import com.demo.ingredisearch.TestData;
import com.demo.ingredisearch.repository.sources.remote.FakeRemoteDataSource.DataStatus;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchResultsFragmentTest extends BaseTest {

    private SearchResultsFragmentArgs args;

    @Before
    public void init() {
        super.init();
        args = new SearchResultsFragmentArgs.Builder("eggs").build();
    }

    @After
    public void tearDown() {
        mRecipeRepository.destroy();
    }

    @Test
    public void SearchResultsFragmentInView() {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);

        // Act (When)
        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Assert (Then)
        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void searchRecipes_queryWithNoResults_displayNoRecipesView() {
        // Arrange (Given)

        // Act (When)
        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Assert (Then)
        onView(withId(R.id.noresultsContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.noresultsTitle)).check(matches(isDisplayed()));
        onView(withText(R.string.noresults)).check(matches(isDisplayed()));
    }

    @Test
    public void searchRecipes_errorOnConnection_displayRetryView() {
        // Arrange (Given)
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);

        // Act (When)
        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Assert (Then)
        onView(withId(R.id.errorContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.retry)).check(matches(isDisplayed()));
        onView(withText(R.string.retry)).check(matches(isDisplayed()));
    }

    @Test
    public void searchRecipes_errorOnConnection_displayRetryView_andThen_retry() {
        // Arrange (Given)
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mRemoteDataSource.setDataStatus(DataStatus.NetworkError);

        // Act (When)
        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Assert (Then)
        onView(withId(R.id.errorContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.retry)).check(matches(isDisplayed()));
        onView(withText(R.string.retry)).check(matches(isDisplayed()));

        // Act (When)
        mRemoteDataSource.setDataStatus(DataStatus.Success);
        onView(withId(R.id.retry)).perform(click());

        // Assert (Then)
        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void selectRecipeAsFavorite_markItsStatusAsFavorite() throws Exception {
        // Given
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Act (When)
        onView(getFavButton(R.drawable.ic_favorite_border_24dp, TestData.recipe1.getTitle())).perform(click());

        // Assert (Then)
        onView(getFavButton(R.drawable.ic_favorite_24dp, TestData.recipe1.getTitle())).check(matches(isDisplayed()));
    }

    @Test
    public void selectRecipeAsFavorite_markItsStatusAsFavorite2() throws Exception {
        // Given
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Act (When)
//        onView(withRecyclerView(R.id.list).atPositionOnView(0, R.id.favButton)).perform(click());
        onView(withId(R.id.list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, clickChildWithId(R.id.favButton))
        );

        // Assert (Then)
        onView(getFavButton(R.drawable.ic_favorite_24dp, TestData.recipe1.getTitle())).check(matches(isDisplayed()));
    }

    @NonNull
    private Matcher<View> getFavButton(int id, String title) {
        return allOf(
                withId(R.id.favButton),
                withTagValue(is(id)),
                hasSibling(withText(title))
        );
    }

    @Test
    public void selectRecipeAsUnFavorite_markItsStatusAsUnFavorite() throws Exception {
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        mFavoritesSource.prepareFavorites(TestData.recipe1);

        FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);

        // Act (When)
        onView(getFavButton(R.drawable.ic_favorite_24dp, TestData.recipe1.getTitle())).perform(click());

        // Assert (Then)
        onView(getFavButton(R.drawable.ic_favorite_border_24dp, TestData.recipe1.getTitle())).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToRecipeDetailsView() {
        // Arrange (Given)
        NavHostController navHostController = mock(NavHostController.class);
        mRemoteDataSource.prepareRecipes(TestData.mRecipes);
        FragmentScenario<SearchResultsFragment> scenario =
                FragmentScenario.launchInContainer(SearchResultsFragment.class, args.toBundle(), R.style.AppTheme);
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.requireView(), navHostController));

        // Act (When)
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Assert (Then)
        verify(navHostController).navigate(
                SearchResultsFragmentDirections.actionSearchResultsFragmentToRecipeDetailsFragment(TestData.recipe1.getRecipeId()));
    }
}