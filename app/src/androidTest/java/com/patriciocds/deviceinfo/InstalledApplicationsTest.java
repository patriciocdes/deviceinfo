package com.patriciocds.deviceinfo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.hamcrest.Matchers.greaterThan;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InstalledApplicationsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testRecyclerViewIsLoaded() {
        // Verifica se a RecyclerView está visível
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // Verifica se a RecyclerView possui pelo menos 1 item
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(greaterThan(0)));
    }
}
