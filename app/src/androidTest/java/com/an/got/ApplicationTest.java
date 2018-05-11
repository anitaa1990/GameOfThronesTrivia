package com.an.got;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.an.got.activity.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {


    @Rule
    public ActivityTestRule<HomeActivity> activityTestRule = new ActivityTestRule<>(HomeActivity.class);


    @Test
    public void testGameOne() throws Exception {
        /*
         * Step 1: From main screen -> go to Game one.
         * Step 2: select the second response
         * Output: if second response is correct, it should redirect to next screen else it should finish
         * */
        onView(withId(R.id.content_panel)).perform(click());

        Thread.sleep(1000);
        onView(allOf(withId(R.id.cardView), isCompletelyDisplayed())).perform(click());

        Thread.sleep(15000);
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(6000);
    }



    @Test
    public void testGameTwo() throws Exception {

    }
}