package com.android.xamoom.tourismtemplate;

import android.Manifest;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest2 {

  @ClassRule
  public static final LocaleTestRule localeTestRule = new LocaleTestRule();

  @Rule
  public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<>(HomeActivity.class);
  @Rule
  public GrantPermissionRule permissionRuleLocationFine = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
  @Rule
  public GrantPermissionRule permissionRuleLocationCoarse = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

  @Before
  public void setup() {
    Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
  }

  @Test
  public void test() {
    Assert.assertTrue(true);
  }

  /**

  @Test
  public void homeActivityTest() throws Exception {
    onView(isRoot()).perform(waitFor(8000));
    Screengrab.screenshot("1");
  }

  @Test
  public void mapActivityTest() {
    onView(withId(R.id.tab_map)).perform(click());
    try {
      Thread.sleep(8000);
    } catch (InterruptedException exc) {
      Log.e("InterruptedException", exc.getLocalizedMessage());
    }
    Screengrab.screenshot("2");
  }

 @Test
 public void infoActivityTest() {
   onView(withId(R.id.tab_info)).perform(click()).perform(waitFor(8000));
   try {
     Thread.sleep(3000);
   } catch (InterruptedException exc) {
     Log.e("InterruptedException", exc.getLocalizedMessage());
   }
   Screengrab.screenshot("3");
 }
 */

  /**
   * Perform action of waiting for a specific time.
   */
  public static ViewAction waitFor(final long millis) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return isRoot();
      }

      @Override
      public String getDescription() {
        return "Wait for " + millis + " milliseconds.";
      }

      @Override
      public void perform(UiController uiController, final View view) {
        uiController.loopMainThreadForAtLeast(millis);
      }
    };
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
