package com.tryp.support.stationsList;

/**
 * Created by cliffroot on 03.03.16.
 */

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.tryp.support.HostActivity;
import com.tryp.support.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class StationsListTest {

    @Rule
    public ActivityTestRule<HostActivity> mActivityRule =
            new ActivityTestRule<>(HostActivity.class);

    @Test
    public void showsNoStationsCloserThan1km(){
        clickPickerAndCheckNumberOfChildren(0.f, 0);
    }

    @Test
    public void showsExactlyOneStationOn3Km(){
        clickPickerAndCheckNumberOfChildren(0.3f, 1);
    }

    @Test
    public void showsTwoStationsOn7Km(){
        clickPickerAndCheckNumberOfChildren(0.7f, 2);
    }

    @Test
    public void hasOneStationFor3rdOption () {
        onView(withText("List")).perform(click());
        onView(withId(R.id.fuel_spinner)).perform(click());
        onData(Matchers.containsString("ГАЗ")).perform(click());
        onView(withId(R.id.recycler_view_stations)).check(matches(hasChildren(Matchers.is(1))));
    }

    @Test
    public void displayCorrectPriceFor3rdOption () {
        onView(withText("List")).perform(click());
        onView(withId(R.id.fuel_spinner)).perform(click());
        onData(Matchers.containsString("ГАЗ")).perform(click());
        onView(withId(R.id.recycler_view_stations)).check(matches(hasChildren(Matchers.is(1))));
        clickPickerAndCheckNumberOfChildren(0.7f, 1);
        onView(withText(Matchers.containsString("5.15UAH"))).check(matches(isDisplayed()));
    }

    @Test
    public void correctlyRotates () {
        onView(withText("List")).perform(click());
        onView(withId(R.id.fuel_spinner)).perform(click());
        onData(Matchers.containsString("A98")).perform(click());
        clickPickerAndCheckNumberOfChildren(0.7f, 2);

        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        onView(withText("A98")).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view_stations)).check(matches(hasChildren(Matchers.is(2))));
        onView(withId(R.id.radius_pick)).check((view, noViewFoundException) -> {
            assertThat(((SeekBar) view).getProgress(), Matchers.is(6));
        });

        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    private void clickPickerAndCheckNumberOfChildren (float ratioWidthFromLeft, int exactlyChildren) {
        onView(withText("List")).perform(click());
        onView(withText(Matchers.containsString("Radius:"))).check(matches(isDisplayed()));
        onView(withId(R.id.radius_pick)).perform(new GeneralClickAction(Tap.SINGLE, view -> {
            final int[] xy = new int[2];
            view.getLocationOnScreen(xy);
            Rect visibleParts = new Rect();
            view.getGlobalVisibleRect(visibleParts);

            float[] coordinates = {xy[0] + (visibleParts.width() * ratioWidthFromLeft), xy[1]};
            return coordinates;
        }, Press.FINGER));
        onView(withId(R.id.recycler_view_stations)).check(matches(hasChildren(Matchers.is(exactlyChildren))));

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