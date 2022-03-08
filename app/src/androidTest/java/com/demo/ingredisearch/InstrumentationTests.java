package com.demo.ingredisearch;

import com.demo.ingredisearch.features.details.RecipeDetailsFragmentTest;
import com.demo.ingredisearch.features.favorites.FavoritesFragmentTest;
import com.demo.ingredisearch.features.search.SearchFragmentTest;
import com.demo.ingredisearch.features.searchresults.SearchResultsFragmentTest;
import com.demo.ingredisearch.repository.sources.favorites.FavoritesSourceImplTest;
import com.demo.ingredisearch.ui.MainActivityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FavoritesSourceImplTest.class,
        SearchFragmentTest.class,
        SearchResultsFragmentTest.class,
        FavoritesFragmentTest.class,
        RecipeDetailsFragmentTest.class,

        MainActivityTest.class
})
public class InstrumentationTests {
}
