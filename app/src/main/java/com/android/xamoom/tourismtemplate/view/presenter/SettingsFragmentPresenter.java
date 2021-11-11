package com.android.xamoom.tourismtemplate.view.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.ApiCallback;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.utils.LanguageUtil;
import com.google.android.gms.common.api.Api;
import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.PushDevice.PushDeviceUtil;
import com.xamoom.android.xamoomsdk.Resource.SystemSetting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import at.rags.morpheus.Error;

public class SettingsFragmentPresenter implements SettingsFragmentContract.Presenter {
  private WeakReference<SettingsFragmentContract.View> view;
  private SharedPreferences sharedPreferences;
  private Context context;

  public SettingsFragmentPresenter(SettingsFragmentContract.View view,
                                   SharedPreferences sharedPreferences, Context context) {
    this.view = new WeakReference<SettingsFragmentContract.View>(view);
    this.sharedPreferences = sharedPreferences;
    this.context = context;

  }

  private void loadSettings() {
    int navigationType = sharedPreferences
            .getInt(Globals.PREF_NAVIGATION_PREFERRED_TYPE, 0);

    boolean notificationOn = sharedPreferences
            .getBoolean(Globals.PREF_NOTIFICATIONS_ON, true);
    boolean notificationSoundOn = sharedPreferences
            .getBoolean(Globals.PREF_NOTIFICATION_SOUND_ON, true);
    boolean notificationVibrationOn = sharedPreferences
            .getBoolean(Globals.PREF_NOTIFICATIONS_VIBRATION_ON, true);

    view.get().didUpdateSettings(notificationOn, notificationSoundOn,
            notificationVibrationOn, navigationType);
  }

  private void saveNavigationPreference(int value) {
    sharedPreferences
            .edit()
            .putInt(Globals.PREF_NAVIGATION_PREFERRED_TYPE, value)
            .apply();
  }

  private void saveNotificationPreference(String key, boolean value) {
    sharedPreferences
            .edit()
            .putBoolean(key, value)
            .apply();
  }

  @Override
  public void onStart() {
    loadSettings();
  }

  @Override
  public void didClickRadioButton(View view) {
    int value = 0;

    switch (view.getId()) {
      case R.id.navigation_walking_radio_button:
        value = Globals.PREF_NAVIGATION_TYPE_WALKING;
        break;
      case R.id.navigation_bike_radio_button:
        value = Globals.PREF_NAVIGATION_TYPE_BIKE;
        break;
      case R.id.navigation_car_radio_button:
        value = Globals.PREF_NAVIGATION_TYPE_CAR;
        break;
    }

    saveNavigationPreference(value);
    loadSettings();
  }

  @Override
  public void didChangeSwitch(View view, boolean isChecked) {
    String key = null;

    switch (view.getId()) {
      case R.id.notification_on_switch:
        key = Globals.PREF_NOTIFICATIONS_ON;
        if (context != null) {
          SharedPreferences prefs = context.getSharedPreferences(PushDeviceUtil.PREFES_NAME, Context.MODE_PRIVATE);
          PushDeviceUtil util = new PushDeviceUtil(prefs);
          util.setNoNotification(!isChecked);
          ApiUtil.getInstance().getEnduserApi().pushDevice(util, true);
        }
        break;
      case R.id.notification_sound_on_switch:
        key = Globals.PREF_NOTIFICATION_SOUND_ON;
        if (context != null) {
          SharedPreferences prefs = context.getSharedPreferences(PushDeviceUtil.PREFES_NAME, Context.MODE_PRIVATE);
          PushDeviceUtil util = new PushDeviceUtil(prefs);
          util.setSound(isChecked);
          ApiUtil.getInstance().getEnduserApi().pushDevice(util, true);
        }
        break;
      case R.id.notification_vibration_on_switch:
        key = Globals.PREF_NOTIFICATIONS_VIBRATION_ON;
        break;
    }

    saveNotificationPreference(key, isChecked);
    loadSettings();
  }

  @Override
  public void getLanguages() {
    if(ApiUtil.getInstance().isLanguageSwitcherEnabled())
      view.get().showPreferredLanguages(new LanguageUtil(context).getLanguagesByCodes(ApiUtil.getInstance().getAppLanguages()));
    else view.get().hideLanguagesTitle();
  }
}
