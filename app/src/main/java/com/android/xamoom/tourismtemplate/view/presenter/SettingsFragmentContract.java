package com.android.xamoom.tourismtemplate.view.presenter;


import com.android.xamoom.tourismtemplate.utils.LanguageUtil;

import java.util.ArrayList;

public interface SettingsFragmentContract {
  interface View {
    void didUpdateSettings(Boolean notificationOn, boolean notificationSoundOn,
                           boolean notificationVibrationOn, int navigationType);
    void showPreferredLanguages(ArrayList<LanguageUtil.Language> languages);
    void hideLanguagesTitle();
  }

  interface Presenter {
    void onStart();
    void didClickRadioButton(android.view.View view);
    void didChangeSwitch(android.view.View view, boolean isChecked);
    void getLanguages();
  }
}
