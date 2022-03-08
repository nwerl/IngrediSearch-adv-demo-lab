package com.demo.ingredisearch.features.details;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.demo.ingredisearch.BaseTest;
import com.demo.ingredisearch.R;
import com.demo.ingredisearch.TestData;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class RecipeDetailsFragmentTest extends BaseTest {

    @Test
    public void recipeDetailsFragmentInView() {
        // Arrange (Assert)
        mRemoteDataSource.prepareRecipes(TestData.recipeDetails01);

        String recipeId = TestData.recipeDetails01.getRecipeId();
        Bundle args = new RecipeDetailsFragmentArgs.Builder(recipeId).build().toBundle();

        // Act (When)
        FragmentScenario.launchInContainer(RecipeDetailsFragment.class, args, R.style.AppTheme);

        // Assert (Then)
        onView(withId(R.id.recipe_image)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_title)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_social_score)).check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_title)).check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_container)).check(matches(isDisplayed()));
    }

}