package com.tryp.support.stationsList;

/**
 * Created by cliffroot on 03.03.16.
 */

import android.graphics.Rect;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import com.tryp.support.HostActivity_;
import com.tryp.support.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class StationsListTest {

    @Rule
    public ActivityTestRule<HostActivity_> mActivityRule =
            new ActivityTestRule<>(HostActivity_.class);


    @Test
    public void showsNoStationsCloserThan1km(){
        onView(withText("List")).perform(click());
        onView(withText(Matchers.containsString("Radius:"))).check(matches(isDisplayed()));
        onView(withId(R.id.radius_pick)).perform(new GeneralClickAction(Tap.SINGLE, view -> {
            final int[] xy = new int[2];
            view.getLocationOnScreen(xy);
            Rect visibleParts = new Rect();
            view.getGlobalVisibleRect(visibleParts);
            float[] coordinates = {xy[0], xy[1]};
            return  coordinates;
        }, Press.FINGER));

        onView(withId(R.id.recycler_view_stations)).check(matches(hasChildren(Matchers.lessThan(1))));
    }

    public static Matcher<View> hasChildren(final Matcher<Integer> numChildrenMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                return view instanceof ViewGroup && numChildrenMatcher.matches(((ViewGroup)view).getChildCount());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" a view with # children is ");
                numChildrenMatcher.describeTo(description);
            }
        };
    }

}