package com.demo.ingredisearch.features.favorites;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.demo.ingredisearch.util.CustomViewMatchers.withRecyclerView;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.demo.ingredisearch.BaseTest;
import com.demo.ingredisearch.R;
import com.demo.ingredisearch.TestData;

import org.junit.Test;

public class FavoritesFragmentTest extends BaseTest {

    @Test
    public void favoritesFragmentInView_withFavorites() throws Exception {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.mFavorites);

        // Act (When)
        FragmentScenario.launchInContainer(FavoritesFragment.class, null, R.style.AppTheme);

        // Assert (Then)
        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void favoritesFragmentInView_withNoFavorites() {
        // Given

        // When
        FragmentScenario.launchInContainer(FavoritesFragment.class, null, R.style.AppTheme);

        // Then - No favorites yet
        checkEmptyFavoritesView();
    }

    private void checkEmptyFavoritesView() {
        onView(withId(R.id.noresultsContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.noresultsTitle)).check(matches(isDisplayed()));
        onView(withText(R.string.nofavorites)).check(matches(isDisplayed()));
    }

    @Test
    public void selectRecipeAsNonFavorite_removesTheRecipeFromView() {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.mFavorites);
        FragmentScenario.launchInContainer(FavoritesFragment.class, null, R.style.AppTheme);

        // Act (When)
        onView(withRecyclerView(R.id.list).atPositionOnView(0, R.id.favButton))
                .perform(click());

        // Assert (Then)
        onView(hasDescendant(withText(TestData.recipe1.getTitle()))).check(doesNotExist());
    }

    @Test
    public void selectLastRecipeAsNonFavorite_displaysNoFavorites() {
        // Arrange (Given)
        mFavoritesSource.prepareFavorites(TestData.recipe1);
        FragmentScenario.launchInContainer(FavoritesFragment.class, null, R.style.AppTheme);

        // Act (When)
        onView(withRecyclerView(R.id.list).atPositionOnView(0, R.id.favButton))
                .perform(click());

        // Assert (Then)
        checkEmptyFavoritesView();
    }

    @Test
    public void navigateToRecipeDetailsView() {
        // Arrange (Given)
        NavHostController navHostController = mock(NavHostController.class);
        mFavoritesSource.prepareFavorites(TestData.mFavorites);
        FragmentScenario<FavoritesFragment> scenario =
                FragmentScenario.launchInContainer(FavoritesFragment.class, null, R.style.AppTheme);
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.requireView(), navHostController));

        // Act (When)
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Assert (Then)
        verify(navHostController).navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToRecipeDetailsFragment(TestData.recipe1.getRecipeId()));
    }

}