package com.demo.ingredisearch.util;

import static org.hamcrest.Matchers.is;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public interface CustomViewMatchers {

    static Matcher<View> withToolbarTitle(@IdRes int titleId) {
        String title = ApplicationProvider.getApplicationContext().getString(titleId);
        return withToolbarTitle(title);
    }

    static Matcher<View> withToolbarTitle(String title) {
        return withToolbarTitle(is(title));
    }

//    static Matcher<View> withToolbarTitle(Matcher<String> matcher) {
//        return new TypeSafeMatcher<>() {
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("with toolbar title");
//                matcher.describeTo(description);
//            }
//
//            @Override
//            protected boolean matchesSafely(View view) {
//                if (view instanceof Toolbar) {
//                    Toolbar toolbar = (Toolbar) view;
//                    return matcher.matches(toolbar.getTitle().toString());
//                }
//                return false;
//            }
//        };
//    }

    static Matcher<View> withToolbarTitle(Matcher<String> matcher) {
        return new WithToolbarTitle(matcher);
    }

    class WithToolbarTitle extends BoundedMatcher<View, Toolbar> {
        private final Matcher<String> matcher;

        public WithToolbarTitle(Matcher<String> matcher) {
            super(Toolbar.class);
            this.matcher = matcher;
        }

        @Override
        protected boolean matchesSafely(Toolbar toolbar) {
            return matcher.matches(toolbar.getTitle().toString());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("withTitle:");
            matcher.matches(description);
        }
    }


    static RecyclerViewMatcher withRecyclerView(@IdRes final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    /*
     * Using the RecyclerViewMatcher, you can not only perform actions on an
     * item at a specific position in a RecyclerView, but also check that some
     * content is contained within a descendant of a specific item.
     */
    class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(@IdRes final int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, @IdRes final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    int id = targetViewId == -1 ? recyclerViewId : targetViewId;
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(id);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)", id);
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                        }
                        else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }
                }
            };
        }
    }

}
