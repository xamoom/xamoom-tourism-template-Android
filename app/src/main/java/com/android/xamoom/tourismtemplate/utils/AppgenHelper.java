package com.android.xamoom.tourismtemplate.utils;

import android.content.Context;
import android.graphics.Color;
import androidx.core.graphics.ColorUtils;

import com.android.xamoom.tourismtemplate.R;

/**
 * Created by raphaelseher on 01/09/2017.
 */

public class AppgenHelper {
  private static AppgenHelper instance;
  private final static int DARK_UNSELECTED_COLOR = Color.argb(153, 0, 0, 0);
  private final static int LIGHT_UNSELECTED_COLOR = Color.argb(153, 255, 255, 255);

  private Context context;
  private int primaryColor = Color.WHITE;
  private int primaryDarkColor = Color.LTGRAY;
  private int accentColor = Color.BLUE;
  private int barFontColor = Color.BLACK;
  private int tabBarSelectedColor = Color.BLACK;
  private int tabbarUnselectedColor = DARK_UNSELECTED_COLOR;
  private int nearbyRowIndex = 1;

  public static AppgenHelper getInstance(Context context) {
    if (instance == null) {
      instance = new AppgenHelper();
      instance.context = context;
      instance.initProperties();
    }
    return instance;
  }

  private void initProperties() {
    primaryColor = context.getResources().getColor(R.color.color_primary);
    primaryDarkColor = context.getResources().getColor(R.color.color_primary_dark);
    accentColor = context.getResources().getColor(R.color.color_accent);

    barFontColor = isDark(primaryColor) ? Color.WHITE : Color.BLACK;
    tabBarSelectedColor = isDark(primaryColor) ? Color.WHITE : Color.BLACK;
    tabbarUnselectedColor = isDark(primaryColor) ? LIGHT_UNSELECTED_COLOR : DARK_UNSELECTED_COLOR;
    nearbyRowIndex = context.getResources().getString(R.string.enable_quiz_feature).equals("true") ? 2 : 1;
  }

  private boolean isDark(int color){
    return (ColorUtils.calculateLuminance(color) < 0.5);
  }

  public int getPrimaryColor() {
    return primaryColor;
  }

  public int getPrimaryDarkColor() {
    return primaryDarkColor;
  }

  public int getAccentColor() {
    return accentColor;
  }

  public int getBarFontColor() {
    return barFontColor;
  }

  public int getTabBarSelectedColor() {
    return tabBarSelectedColor;
  }

  public int getTabbarUnselectedColor() {
    return tabbarUnselectedColor;
  }

  public int getNearbyRowIndex() {
    return nearbyRowIndex;
  }
}
