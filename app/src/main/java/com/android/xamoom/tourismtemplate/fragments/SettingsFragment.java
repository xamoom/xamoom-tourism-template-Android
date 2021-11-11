package com.android.xamoom.tourismtemplate.fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.HomeActivity;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.modules.AppModule;
import com.android.xamoom.tourismtemplate.modules.DaggerSettingsFragmentComponent;
import com.android.xamoom.tourismtemplate.modules.SettingsFragmentModul;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender;
import com.android.xamoom.tourismtemplate.utils.AppgenHelper;
import com.android.xamoom.tourismtemplate.utils.LanguageUtil;
import com.android.xamoom.tourismtemplate.view.presenter.SettingsFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.SettingsFragmentPresenter;
import com.google.android.gms.analytics.Tracker;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.platforminfo.GlobalLibraryVersionRegistrar;
import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Resource.System;
import com.xamoom.android.xamoomsdk.Resource.SystemSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.inject.Inject;

import at.rags.morpheus.Error;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements SettingsFragmentContract.View,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  @BindView(R.id.toolbar) Toolbar toolbar;

  @BindView(R.id.notification_on_switch) SwitchCompat notificationSwitch;
  @BindView(R.id.notification_sound_on_switch) SwitchCompat notificationSoundSwitch;
  @BindView(R.id.notification_vibration_on_switch) SwitchCompat notificationVibrationSwitch;

  @BindView(R.id.navigation_walking_radio_button) RadioButton navigationWalkingRadioButton;
  @BindView(R.id.navigation_bike_radio_button) RadioButton navigationBikeRadioButton;
  @BindView(R.id.navigation_car_radio_button) RadioButton navigationCarRadioButton;

  @BindView(R.id.soundLayout) RelativeLayout soundLayout;
  @BindView(R.id.vibrationLayout) RelativeLayout vibrationLayout;
  @BindView(R.id.ephemeral_id_textView)
  TextView ephemeralIdTextView;
  @BindView(R.id.languages_radio_group)
  RadioGroup languagesRadioGroup;
  @BindView(R.id.language_title_textView)
  TextView languagesTextView;

  @Inject SettingsFragmentPresenter presenter;
  @Inject
  EnduserApi enduserApi;
  @Inject
  Tracker mTracker;
  @Inject
  SplitInstallManager splitInstallManager;
  private int numberOfTouchOnEphemeralId = 0;
  private int mySessionId = -1;
  private boolean recreateActivity = true;
  private SharedPreferences sharedPreferences;

  public SettingsFragment() {
    // Required empty public constructor
  }

  public static SettingsFragment newInstance() {
    SettingsFragment fragment = new SettingsFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AnalyticsUtil.Companion.reportContentView("Settings", Globals.ANALYTICS_CONTENT_TYPE_SCREEN, "",
            null);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    ButterKnife.bind(this, view);

    DaggerSettingsFragmentComponent.builder()
            .settingsFragmentModul(new SettingsFragmentModul(this, getContext()))
            .appModule(new AppModule(getContext()))
            .build()
            .inject(this);
    new GoogleAnalyticsSender(mTracker).reportContentView("Android Settings screen", "", "", null);

    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    setupActionbar();
    setupUi();
    initEphemeralId();

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void changeLocalization(String language){

    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();

// Creates a listener for request status updates.
    SplitInstallStateUpdatedListener listener = state -> {
      if (state.sessionId() == mySessionId) {
        if (state.status() == SplitInstallSessionStatus.INSTALLED) {
          recreateActivity = true;
//          Resources res = getResources();
//          DisplayMetrics dm = res.getDisplayMetrics();
//          Configuration conf = res.getConfiguration();
          if(new ArrayList<String>(Arrays.asList("de", "fr", "it", "nl", "sk", "sl", "tr", "en")).contains(language)) {
            conf.setLocale(new Locale(language));
          } else {
            conf.setLocale(new Locale(Locale.ENGLISH.getLanguage()));
          }
          res.updateConfiguration(conf, dm);
          if ((HomeActivity) getActivity() != null) {
            ((HomeActivity) getActivity()).restartFromSettings();
          }
          sharedPreferences.edit().putBoolean("recreateFromSettings", true).apply();
        }
      }
    };

// Registers the listener.
    splitInstallManager.registerListener(listener);

    if (splitInstallManager.getInstalledLanguages().contains(language)) {
      if(language != null && new ArrayList<String>(Arrays.asList("de", "fr", "it", "nl", "sk", "sl", "tr", "en")).contains(language)) {
        conf.setLocale(new Locale(language));
      } else {
        conf.setLocale(new Locale(Locale.ENGLISH.getLanguage()));
      }
      res.updateConfiguration(conf, dm);
    } else {
      recreateActivity = false;
      SplitInstallRequest request =
              SplitInstallRequest.newBuilder()
                      .addLanguage(Locale.forLanguageTag(language))
                      .build();
      splitInstallManager.startInstall(request).addOnSuccessListener(new OnSuccessListener<Integer>() {
        @Override
        public void onSuccess(Integer sessionId) {
          mySessionId = sessionId;
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(Exception e) {Resources res = getResources();
//          DisplayMetrics dm = res.getDisplayMetrics();
//          Configuration conf = res.getConfiguration();
//          conf.setLocale(new Locale(Locale.ENGLISH.getLanguage()));
//          res.updateConfiguration(conf, dm);
        }
      });
    }

  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void showPreferredLanguages(ArrayList<LanguageUtil.Language> languages) {
    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setMargins(0, 20, 0, 20);
    boolean isAnyRadioChecked = false;

    languagesRadioGroup.removeAllViews();
    for (int i = 0; i < languages.size(); i++) {
      RadioButton radioButton = new RadioButton(getContext());
      radioButton.setLayoutParams(params);
      radioButton.setText(languages.get(i).getOriginName() + "\n" + languages.get(i).getEnglishName());
      radioButton.setId(i + 1);
      languagesRadioGroup.addView(radioButton);
      if(sharedPreferences.getString("current_language_code", null) != null){
        isAnyRadioChecked = true;
        if(languages.get(i).getCode().toLowerCase(Locale.ENGLISH).equals(sharedPreferences.getString("current_language_code", null))){
          radioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.color_primary)));
          radioButton.setChecked(true);
          changeLocalization(sharedPreferences.getString("current_language_code", null));
        }
      }
      if (!isAnyRadioChecked && languages.get(i).getCode().toLowerCase(Locale.ENGLISH).equals(getEndUserApi().getSystemLanguage().toLowerCase(Locale.ENGLISH))) {
        radioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.color_primary)));
        radioButton.setChecked(true);
        sharedPreferences.edit().putString("current_language_code", languages.get(i).getCode().toLowerCase(Locale.ENGLISH)).apply();
        changeLocalization(sharedPreferences.getString("current_language_code", null));
        isAnyRadioChecked = true;
      }

    }

    if(!isAnyRadioChecked){
      for(int i = 0; i < languagesRadioGroup.getChildCount(); i++){
        if(languagesRadioGroup.getChildAt(i) instanceof RadioButton){
            if(((RadioButton) languagesRadioGroup.getChildAt(i)).getText().toString().split("\n")[0].equals("English")){
              ((RadioButton) languagesRadioGroup.getChildAt(i)).setChecked(true);
              sharedPreferences.edit().putString("current_language_code", "en").apply();
              changeLocalization("en");
              break;
            }
        }
      }
    }

    languagesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        sharedPreferences.edit().putString("current_language_code", languages.get(checkedId - 1).getCode().toLowerCase(Locale.ENGLISH)).apply();
        changeLocalization(languages.get(checkedId - 1).getCode().toLowerCase(Locale.ENGLISH));
        if (recreateActivity) {
          ((HomeActivity) getActivity()).restartFromSettings();
          sharedPreferences.edit().putBoolean("recreateFromSettings", true).apply();
        }
      }
    });
  }

  @Override
  public void hideLanguagesTitle() {
    languagesTextView.setVisibility(View.GONE);
  }


  private void initEphemeralId(){
    ephemeralIdTextView.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    numberOfTouchOnEphemeralId+=1;
                    if(numberOfTouchOnEphemeralId == 7){
                        ephemeralIdTextView.setText(getEndUserApi().getEphemeralId());
                    }
                    if(numberOfTouchOnEphemeralId == 8){
                      copyToClipboard(ephemeralIdTextView.getText().toString());
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                  break;
            }
            return true;
        }
    });
  }

  private void copyToClipboard(String id){
    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText(id, id);
    clipboard.setPrimaryClip(clip);
    Toast.makeText(getContext(), getContext().getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onResume() {
    super.onResume();
//    if(!sharedPreferences.getBoolean("isSettingsRestarted", false)) {
      presenter.getLanguages();
//    }
//     else sharedPreferences.edit().putBoolean("isSettingsRestarted", false).apply();

    numberOfTouchOnEphemeralId = 0;
  }

  private EnduserApi getEndUserApi() {
    if (enduserApi == null) {
      throw new NullPointerException("Set EnduserApi with " +
              "ApiUtil.getInstance().setEnduserApi(api)");
    }
    return enduserApi;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void setupActionbar() {
    if(this.getString(R.string.is_background_image).equals("true")) {
      if(this.getActivity() != null)
        toolbar.setBackground(this.getActivity().getDrawable(R.drawable.background_image));
    } else {
      if(this.getActivity() != null)
        toolbar.setBackground(this.getActivity().getDrawable(R.color.color_primary));
    }
    toolbar.setTitleTextColor(AppgenHelper.getInstance(getContext()).getBarFontColor());
    AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(R.string.settings_action_bar_title);
    }
  }

  private void setupUi() {
    navigationWalkingRadioButton.setOnClickListener(this);
    navigationBikeRadioButton.setOnClickListener(this);
    navigationCarRadioButton.setOnClickListener(this);

    notificationSwitch.setOnCheckedChangeListener(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      soundLayout.setVisibility(View.GONE);
      vibrationLayout.setVisibility(View.GONE);
    } else {
      notificationSoundSwitch.setOnCheckedChangeListener(this);
      notificationVibrationSwitch.setOnCheckedChangeListener(this);
    }
  }

  @Override
  public void onClick(View view) {
    presenter.didClickRadioButton(view);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    presenter.didChangeSwitch(buttonView, isChecked);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void didUpdateSettings(Boolean notificationOn, boolean notificationSoundOn,
                                boolean notificationVibrationOn, int navigationType) {
    notificationSwitch.setChecked(notificationOn);
    notificationSoundSwitch.setChecked(notificationSoundOn);
    notificationVibrationSwitch.setChecked(notificationVibrationOn);

    switch (navigationType) {
      case 0:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          navigationWalkingRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.color_primary)));
          navigationBikeRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.no_favs_text_color)));
          navigationCarRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.no_favs_text_color)));
        }
        navigationWalkingRadioButton.setChecked(true);
        break;
      case 1:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          navigationBikeRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.color_primary)));
          navigationWalkingRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.no_favs_text_color)));
          navigationCarRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.no_favs_text_color)));
        }
        navigationBikeRadioButton.setChecked(true);
        break;
      case 2:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          navigationCarRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.color_primary)));
          navigationWalkingRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.no_favs_text_color)));
          navigationBikeRadioButton.setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.no_favs_text_color)));
        }
        navigationCarRadioButton.setChecked(true);
        break;
    }
  }
}
