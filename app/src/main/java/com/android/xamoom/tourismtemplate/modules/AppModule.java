package com.android.xamoom.tourismtemplate.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.xamoom.android.xamoomsdk.EnduserApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
  private Context context;

  public AppModule(Context context) {
    this.context = context;
  }

  @Singleton
  @Provides
  public Context provideContext() {
    return context;
  }

  @Singleton
  @Provides
  public SharedPreferences provideSharedPreferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Singleton
  @Provides
  public String provideApikey() {
    return context.getString(R.string.api_key);
  }

  @Singleton
  @Provides
  public ApiUtil provideApiUtil() {
    return ApiUtil.getInstance();
  }

  @Singleton
  @Provides
  public EnduserApi provideEnduserApi(String apikey) {
    return new EnduserApi(apikey, context, true, context.getString(R.string.beacon_major), Globals.BEACON_COOLDOWN_TIME);
  }


  @Singleton
  @Provides
  public Tracker provideGoogleAnalyticsTracker() {
      return GoogleAnalytics.getInstance(context).newTracker(R.xml.global_tracker);
  }
}
