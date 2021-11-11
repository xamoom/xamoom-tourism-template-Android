package com.android.xamoom.tourismtemplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDexApplication;

import com.android.xamoom.tourismtemplate.modules.AppComponent;
import com.android.xamoom.tourismtemplate.modules.AppModule;
import com.android.xamoom.tourismtemplate.modules.DaggerAppComponent;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.Analytics.FabricAnalyticsSender;
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender;
import com.android.xamoom.tourismtemplate.utils.BeaconUtil;
import com.android.xamoom.tourismtemplate.utils.NotificationUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.xamoom.android.xamoomsdk.APIPasswordCallback;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Enums.ContentReason;
import com.xamoom.android.xamoomsdk.Helpers.XamoomBeaconService;
import com.xamoom.android.xamoomsdk.Resource.Content;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import at.rags.morpheus.Error;

public class MainApp extends MultiDexApplication {
  private static final String TAG = MainApp.class.getSimpleName();

  private static MainApp app;
  private static FirebaseAnalytics firebaseAnalytics;
  private AppComponent appComponent;

  @Inject SharedPreferences sharedPreferences;
  @Inject EnduserApi api;

  @Override
  public void onCreate() {
    super.onCreate();
    app = this;

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    System.out.println("PROD ANALYTICS");
    Tracker tracker = GoogleAnalytics.getInstance(getApplicationContext()).newTracker(R.xml.global_tracker);
    AnalyticsUtil.Companion.registerSender(new GoogleAnalyticsSender(tracker));
    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    AnalyticsUtil.Companion.registerSender(new FabricAnalyticsSender(firebaseAnalytics));

    appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(getApplicationContext()))
        .build();
    appComponent.inject(this);

    XamoomBeaconService.getInstance(getApplicationContext(), api)
        .startBeaconService();
    XamoomBeaconService.getInstance(getApplicationContext(), api).automaticRanging = true;

    LocalBroadcastManager.getInstance(this).registerReceiver(mFoundBeaconsReceiver,
            new IntentFilter(XamoomBeaconService.FOUND_BEACON_BROADCAST));
    LocalBroadcastManager.getInstance(this).registerReceiver(mBeaconReady,
            new IntentFilter(XamoomBeaconService.BEACON_SERVICE_CONNECT_BROADCAST));
    LocalBroadcastManager.getInstance(this).registerReceiver(mBeaconEnter,
            new IntentFilter(XamoomBeaconService.BEACON_SERVICE_CONNECT_BROADCAST));

    LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationReceived,
            new IntentFilter("xamoom-push-notification-received"));

    LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        NotificationUtil.deleteNotification(context);
      }
    }, new IntentFilter(XamoomBeaconService.EXIT_REGION_BROADCAST));

    LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationReceived,
            new IntentFilter("xamoom-push-notification-received"));
  }


  @Override
  public void onTerminate() {
    super.onTerminate();
  }

  private final BroadcastReceiver mNotificationReceived = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      if (intent.getExtras() != null) {
        Bundle extras = intent.getExtras();
        String contentId = extras.getString("contentId");
        String title = extras.getString("title");
        String body = extras.getString("body");

        if (!sharedPreferences.getBoolean(Globals.PREF_NOTIFICATIONS_ON, true)) {
          return;
        }

        if (contentId != null) {
          NotificationUtil.sendNotification(getApplicationContext(),
                  0,
                  title,
                  body,
                  R.drawable.ic_ble,
                  sharedPreferences.getBoolean(Globals.PREF_NOTIFICATION_SOUND_ON, true),
                  sharedPreferences.getBoolean(Globals.PREF_NOTIFICATIONS_VIBRATION_ON, true),
                  contentId);
        }
      }
    }
  };

  private final BroadcastReceiver mBeaconEnter = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      AnalyticsUtil.Companion.reportCustomEvent("Entered Beacon Region", "Entered Beacon Region",
              null, null);
    }
  };

  private final BroadcastReceiver mBeaconReady = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.v(TAG, "BeaconService Ready");
    }
  };

  private final BroadcastReceiver mFoundBeaconsReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, Intent intent) {
      if (intent.getExtras() != null) {
        ArrayList<Beacon> beacons = intent.getExtras().getParcelableArrayList(XamoomBeaconService.BEACONS);
        ArrayList<Content> contents = intent.getExtras().getParcelableArrayList(XamoomBeaconService.CONTENTS);

        if (beacons.size() > 0) {
          Beacon beacon = beacons.get(0);

          if (!sharedPreferences.getBoolean(Globals.PREF_NOTIFICATIONS_ON, true)) {
            return;
          }

          if (BeaconUtil.shouldBeaconNotify(contents.get(0).getId())) {
            Content beaconContent = contents.get(0);

            String title = beaconContent.getTitle();
            String text = beaconContent.getDescription();
            String id = beaconContent.getId();
            String contentId = beaconContent.getId();

            AnalyticsUtil.Companion.reportCustomEvent("Beacon Notification",
                    "Show beacon notification", "For content: " + id, null);

            NotificationUtil.sendNotification(getApplicationContext(),
                    NotificationUtil.BEACON_NOTIFICATION_ID,
                    title,
                    text,
                    R.drawable.ic_ble,
                    sharedPreferences.getBoolean(Globals.PREF_NOTIFICATION_SOUND_ON, true),
                    sharedPreferences.getBoolean(Globals.PREF_NOTIFICATIONS_VIBRATION_ON, true),
                    contentId);
          } else {
            Log.i(TAG, "Beacon " + beacon.getId3().toString() + " is on cooldown");
            return;
          }
        } else {
          BeaconUtil.isNotificationShown = new HashMap<>();
        }
      }
    }
  };

  public static MainApp app() {
    return app;
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }
}
