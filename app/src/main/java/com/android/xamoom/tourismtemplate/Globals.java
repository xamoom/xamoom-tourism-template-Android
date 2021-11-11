package com.android.xamoom.tourismtemplate;

public class Globals {
  public static final String APIKEY = "25c5606b-a7bf-42e2-85c3-39db1753bc05";

  public final static int BEACON_COOLDOWN_TIME = 30 * 60 * 1000;

  public final static String TOP_TIP_TAG = "x-top-tip";
  public final static String APP_CONFIG_TAG = "x-app-config";
  public final static String INFO_TAG = "x-info";
  public final static String VOUCHER_TAG = "x-voucher";
  public final static String QUIZ_TAG = "x-quiz";

  public final static String TOP_TIP_CUSTOM_META_KEY = "top-tip";

  public static final String PREF_NAVIGATION_PREFERRED_TYPE = "pref_navigation_type";
  public static final int PREF_NAVIGATION_TYPE_WALKING = 0;
  public static final int PREF_NAVIGATION_TYPE_BIKE = 1;
  public static final int PREF_NAVIGATION_TYPE_CAR = 2;
  public static final int PREF_NAVIGATION_TYPE_PUBLIC_TRANSPORT = 3;
  public static final String PREF_NOTIFICATIONS_ON = "pref_notification_on";
  public static final String PREF_NOTIFICATION_SOUND_ON = "pref_notification_sound_on";
  public static final String PREF_NOTIFICATIONS_VIBRATION_ON = "pref_notification_vibration_on";
  public static final String ONBOARDING_TAG = "X-ONBOARDING";
  public final static String NEARBY_TAG = "nearby";
  public final static String GEOFENCE_TAG = "x-geofence";
  public final static String GUIDE_TAG = "guide";

  public final static String[] FILTER_TAGS_SORTED = new String[]{"architektur", "ausflugsziel",
          "denkmal", "kirche", "galmus", "natur", "sightseeing", "shopping"};

  public final static String ANALYTICS_CONTENT_TYPE_SCREEN = "App Screen";
  public final static String MAPBOX_STYLE_URL = "mapbox://styles/xamoom-georg/ck4zb0mei1l371coyi41snaww";

  public final static String RELOAD_AFTER_BACK = "reload_after_back";

  public final static String IS_SOCIAL_SHARING_ENABLED_KEY = "is_social_sharing_enabled";
}
