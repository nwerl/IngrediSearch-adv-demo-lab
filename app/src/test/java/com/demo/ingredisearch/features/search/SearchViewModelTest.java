package com.demo.ingredisearch.features.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.demo.ingredisearch.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SearchViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // SUT
    SearchViewModel mViewModel;

    @Before
    public void init() {
        mViewModel = new SearchViewModel();
    }

    @Test
    public void search_emptyQuery_triggerEmptyQueryAlertMessage() throws InterruptedException {
        // Arrange (Given)

        // Act (When)
        mViewModel.search("");

        // Assert (Then)
        Boolean isEmpty = LiveDataTestUtil.getOrAwaitValue(mViewModel.isEmpty());
        assertThat(isEmpty, is(true));
    }

    @Test
    public void search_validQuery_triggerNavToSearchResults() throws InterruptedException {
        // Arrange (Given)

        // Act (When)
        mViewModel.search("eggs");

        // Assert (Then)
        String query = LiveDataTestUtil.getOrAwaitValue(mViewModel.navToSearchResults());
        assertThat(query, is("eggs"));
    }
}
