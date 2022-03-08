package com.demo.ingredisearch.util;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public interface CustomViewActions {

    static ViewAction clickChildWithId(@IdRes final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child View with specified id";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View child = view.findViewById(id);
                child.performClick();
            }
        };
    }

}
