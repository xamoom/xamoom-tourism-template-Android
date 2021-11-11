package com.android.xamoom.tourismtemplate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.xamoom.tourismtemplate.fragments.ContentFragment;
import com.android.xamoom.tourismtemplate.fragments.HomeFragment;
import com.android.xamoom.tourismtemplate.fragments.MapFragment;
import com.android.xamoom.tourismtemplate.fragments.ScannerFragment;
import com.android.xamoom.tourismtemplate.modules.AppModule;
import com.android.xamoom.tourismtemplate.modules.DaggerHomeComponent;
import com.android.xamoom.tourismtemplate.modules.HomeModule;
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender;
import com.android.xamoom.tourismtemplate.utils.ApiCallback;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.utils.AppgenHelper;
import com.android.xamoom.tourismtemplate.utils.InAppNotificationUtil;
import com.android.xamoom.tourismtemplate.view.presenter.HomeScreenContract;
import com.android.xamoom.tourismtemplate.view.presenter.HomeScreenPresenter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.splitcompat.SplitCompat;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.iid;.FirebaseInstanceId
import com.google.firebase.installations.remote.TokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mapbox.mapboxsdk.Mapbox;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;
import com.xamoom.android.xamoomsdk.Helpers.ColorHelper;
import com.xamoom.android.xamoomsdk.Helpers.GeofenceBroadcastReceiver;
import com.xamoom.android.xamoomsdk.PushDevice.PushDeviceUtil;
import com.xamoom.android.xamoomsdk.Resource.Content;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import at.rags.morpheus.Error;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeScreenContract.View,
        HomeFragment.HomeFragmentListener, MapFragment.MapFragmentListener,
        XamoomContentFragment.OnXamoomContentFragmentInteractionListener,
        ScannerFragment.QrScannerListener, ContentFragment.ContentFragmentListener {

  private static final int REQUEST_LOCATION = 0;
  private static final int REQUEST_CODE_FILTER = 1;
  private static final int REQUEST_CAMERA = 2;

  private static final Long UPDATE_INTERVAL = 15 * 60 * 1000L;
  private static final Long WAIT_INTERVAL = 30 * 60 * 1000L;
  private static final String OPEN_CONTENT_INDICATOR = "content/";

  public static final String EXTRA_CONTENT_NOTIFICATION_ID = "contentid";

  private ScannerFragment scannerFragment;
  @Inject
  Tracker mTracker;

  private boolean hasCameraPermission = false;

  @BindView(R.id.bottom_bar)
  BottomNavigationView bottomBar;
  @BindView(R.id.home_root_layout)
  View rootLayout;

  @Inject
  HomeScreenPresenter presenter;

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    changeLocalization(sharedPreferences.getString("current_language_code", null));

    setContentView(R.layout.activity_home);
    Mapbox.getInstance(getApplicationContext(), getResources().getString(R.string.mapbox_access_token));
    sharedPreferences.edit().putString("is_background_image", this.getResources().getString(R.string.is_background_image)).apply();

    FirebaseApp.initializeApp(this);
    ButterKnife.bind(this);

    ColorHelper.getInstance(this, R.color.color_primary, R.color.color_primary_dark, R.color.color_accent);

    DaggerHomeComponent.builder()
            .appModule(new AppModule(this))
            .homeModule(new HomeModule(this, this)).build().inject(this);

    if(mTracker != null) {
      new GoogleAnalyticsSender(mTracker).reportContentView("Android Home screen", "", "", null);
    }
    removeShiftMode(bottomBar);
    bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        presenter.didSelectTab(item.getItemId());
        return true;
      }
    });
    if(this.getString(R.string.is_background_image).equals("true")) {
      bottomBar.setBackground(this.getDrawable(R.drawable.background_image));
    } else {
      bottomBar.setBackgroundColor(this.getColor(R.color.color_primary));
    }
    if(sharedPreferences.getBoolean("recreateFromSettings", false)) {
      bottomBar.setSelectedItemId(R.id.tab_setting);
        sharedPreferences.edit().putBoolean("recreateFromSettings", false).apply();
    } else {
      bottomBar.setSelectedItemId(R.id.tab_home);
    }
    int[][] states = new int[][]{
            new int[]{android.R.attr.state_checked}, // checked
            new int[]{-android.R.attr.state_checked}, // unchecked
    };
    int[] colors = new int[]{
            AppgenHelper.getInstance(getApplicationContext()).getTabBarSelectedColor(),
            AppgenHelper.getInstance(getApplicationContext()).getTabbarUnselectedColor()
    };
    ColorStateList bottomBarColorStates = new ColorStateList(states, colors);
    bottomBar.setItemIconTintList(bottomBarColorStates);
    bottomBar.setItemTextColor(bottomBarColorStates);

    ApiUtil.getInstance().setInAppNotificationUtil(
            new InAppNotificationUtil(getApplicationContext(), rootLayout));

    FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
              @Override
              public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                  Log.w("FirebaseMessaging", "Fetching FCM registration token failed", task.getException());
                  return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                // Log and toast
                if (token != null) {
                  SharedPreferences sharedPref = getSharedPreferences(PushDeviceUtil.PREFES_NAME,
                          Context.MODE_PRIVATE);
                  PushDeviceUtil util = new PushDeviceUtil(sharedPref);
                  util.storeToken(token);
                  ApiUtil.getInstance().getEnduserApi().pushDevice(util, true);
                }
              }
            });

    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PushDeviceUtil.PREFES_NAME, Context.MODE_PRIVATE);
    Boolean onboardingPassed = sharedPref.getBoolean(Globals.ONBOARDING_TAG, false);
    if (!onboardingPassed) {
      Intent intent = new Intent(getApplicationContext(), OnboardActivity.class);
      startActivity(intent);
    }

    if (getIntent() != null) {
      Bundle extras = getIntent().getExtras();

      Uri uri = getIntent().getData();

      String path = "";
      if (uri != null && uri.getPath() != null) {
        path = uri.getPath();
      }

      if (path != null && path.contains(OPEN_CONTENT_INDICATOR)) {
        String[] paths = path.split(OPEN_CONTENT_INDICATOR);
        String contentId = paths[1];
        ApiUtil.getInstance().loadContent(contentId, this, new ApiCallback.ObjectCallback<Content, Error>() {
          @Override
          public void finish(Content content, Error error) {
            if (content != null) {
              Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
              intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
              intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
              startActivity(intent);
            }
          }
        });
      } else if (path != null && !path.equals("/") && path.contains("/")) {
        String[] paths = path.split("/");
        String contentId = paths[1];
        ApiUtil.getInstance().loadContentByLocationIdentifier(contentId, this, new ApiCallback.ObjectCallback<Content, Error>() {
          @Override
          public void finish(Content content, Error error) {
            if (content != null) {
              Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
              intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
              intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
              startActivity(intent);
            }
          }
        });
      } else if (extras != null && (extras.getString(EXTRA_CONTENT_NOTIFICATION_ID) != null || extras.getString("content-id") != null)) {
        String contentId = extras.getString(EXTRA_CONTENT_NOTIFICATION_ID);
        if (contentId == null) {
          contentId = extras.getString("content-id");
        }
        ApiUtil.getInstance().loadContent(contentId, this, new ApiCallback.ObjectCallback<Content, Error>() {
          @Override
          public void finish(Content content, Error error) {
            if (content != null) {
              Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
              intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
              intent.putExtra(ContentActivity.EXTRA_IS_BEACON, true);
              startActivity(intent);
            }
          }
        });
      } else if (getIntent().getDataString() != null) {
        String dataString = getIntent().getDataString();
        if (dataString.contains(getString(R.string.deep_link)) ||
                dataString.contains("xm.gl")) {
          openNFCIntent(dataString);
        }
      }
    }
    updateLocationIfNeeded();
  }

  private void updateLocationIfNeeded() {
    SharedPreferences sharedPref = this.getSharedPreferences(PushDeviceUtil.PREFES_NAME,
            Context.MODE_PRIVATE);
    PushDeviceUtil util = new PushDeviceUtil(sharedPref);
    Map<String, Float> location = util.getSavedLocation();
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      if (location == null) {
        updateLocation();
      } else {
        Float lat = location.get("lat");
        Float lon = location.get("lon");
        if (lat == 0.0F || lon == 0.0F) {
          updateLocation();
        }
      }
    }
  }

  private void changeLocalization(String language){
      Resources res = getResources();
      DisplayMetrics dm = res.getDisplayMetrics();
      Configuration conf = res.getConfiguration();
      if(language != null && new ArrayList<String>(Arrays.asList("de", "fr", "it", "nl", "sk", "sl", "tr", "en")).contains(language))
        conf.setLocale(new Locale(language));
      else conf.setLocale(new Locale(Locale.getDefault().getLanguage()));
      res.updateConfiguration(conf, dm);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent != null) {
      Bundle extras = intent.getExtras();
      Uri uri = getIntent().getData();

      String path = "";
      if (uri != null && uri.getPath() != null) {
        path = uri.getPath();
      }

      if (path != null && path.contains(OPEN_CONTENT_INDICATOR)) {
        String[] paths = path.split(OPEN_CONTENT_INDICATOR);
        String contentId = paths[1];
        ApiUtil.getInstance().loadContent(contentId, this, new ApiCallback.ObjectCallback<Content, Error>() {
          @Override
          public void finish(Content content, Error error) {
            if (content != null) {
              Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
              intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
              intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
              startActivity(intent);
            }
          }
        });
      } else if (path != null && !path.equals("/") && path.contains("/")) {
        String[] paths = path.split("/");
        String contentId = paths[1];
        ApiUtil.getInstance().loadContentByLocationIdentifier(contentId, this, new ApiCallback.ObjectCallback<Content, Error>() {
          @Override
          public void finish(Content content, Error error) {
            if (content != null) {
              Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
              intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
              intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
              startActivity(intent);
            }
          }
        });
      } else if (extras != null && (extras.getString(EXTRA_CONTENT_NOTIFICATION_ID) != null || extras.getString("content-id") != null)) {
        String contentId = extras.getString(EXTRA_CONTENT_NOTIFICATION_ID);
        if (contentId == null) {
          contentId = extras.getString("content-id");
        }
        ApiUtil.getInstance().loadContent(contentId, this, new ApiCallback.ObjectCallback<Content, Error>() {
          @Override
          public void finish(Content content, Error error) {
            if (content != null) {
              Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
              intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
              intent.putExtra(ContentActivity.EXTRA_IS_BEACON, true);
              startActivity(intent);
            }
          }
        });
      } else if (getIntent().getDataString() != null) {
        String dataString = getIntent().getDataString();
        if (dataString.contains(getString(R.string.custom_webclient)) ||
                dataString.contains("xm.gl") ||
                dataString.contains("r.xm.gl")) {
          openNFCIntent(dataString);
        }
      }
    }
  }

  @SuppressLint("RestrictedApi")
  public void removeShiftMode(BottomNavigationView view) {
    BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
    try {
      Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
      shiftingMode.setAccessible(true);
      shiftingMode.setBoolean(menuView, false);
      shiftingMode.setAccessible(false);
      for (int i = 0; i < menuView.getChildCount(); i++) {
        BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
        // set once again checked value, so view will be updated
        item.setChecked(item.getItemData().isChecked());
      }

    } catch (NoSuchFieldException e) {
      Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
    } catch (IllegalAccessException e) {
      Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_FILTER) {
      ArrayList<String> selectedTags = data.getStringArrayListExtra(FilterActivity.SELECTED_TAGS);
      presenter.didUpdateSelectedTags(selectedTags);
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  void requestCameraPermission() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, scannerFragment).commit();
        return;
      }

      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
          hasCameraPermission = true;
          getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, scannerFragment).commit();
          return;
      }

      this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
  }

  private void openNFCIntent(String dataString) {

    Uri uri = Uri.parse(dataString);
    String locationId = uri.getLastPathSegment();

    ApiUtil.getInstance().loadContentByLocationIdentifier(locationId, this, new ApiCallback.ObjectCallback<Content, Error>() {
      @Override
      public void finish(Content result, Error error) {
        if (result != null) {
          Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
          intent.putExtra(ContentActivity.EXTRA_CONTENT, result);
          intent.putExtra(ContentActivity.EXTRA_IS_BEACON, true);
          startActivity(intent);
        }
      }
    });
  }

  public void openHome() {
    bottomBar.setSelectedItemId(R.id.tab_home);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    updateLocation();
    if (requestCode == REQUEST_CAMERA) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED && isCameraPermissionGranted()) {
        hasCameraPermission = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, scannerFragment).commitAllowingStateLoss();
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

//  private void requestLocationPermission() {
//    String[] permissionsArray = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//      updateLocation();
//      return;
//    }
//
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//      updateLocation();
//      return;
//    }
//
//    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//      //will displayed if the user denied the permission first time.
//
//      AlertDialog.Builder builder = new AlertDialog.Builder(this);
//      builder.setTitle(getString(R.string.alert_permission_location_title));
//      builder.setMessage(getString(R.string.alert_permission_location_message));
//      // Add the buttons
//      builder.setPositiveButton(getString(R.string.alert_permission_location_ok_button), new DialogInterface.OnClickListener() {
//        @RequiresApi(api = Build.VERSION_CODES.M)
//        @Override
//        public void onClick(DialogInterface dialogInterface, int i) {
//          requestPermissions(permissionsArray, REQUEST_LOCATION);
//        }
//      });
//      builder.setNegativeButton(getString(R.string.alert_permission_location_cancel_button), new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int i) {
//          dialog.dismiss();
//        }
//      });
//
//      builder.create().show();
//    } else {
//      // Location permission has not been granted yet. Request it directly.
//      this.requestPermissions(permissionsArray,
//              REQUEST_LOCATION);
//    }
//  }

  private boolean isCameraPermissionGranted() {
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void changeTab(Fragment fragment) {
      if (fragment.getClass() == ScannerFragment.class) {
        if (hasCameraPermission) {
          getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).commit();
        } else {
          scannerFragment = (ScannerFragment) fragment;
          requestCameraPermission();
        }
      } else {
        getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).commit();
      }
  }

  @Override
  public void didClickContent(Content content, boolean isBeacon) {
    Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
    intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
    intent.putExtra(ContentActivity.EXTRA_IS_BEACON, true);
    startActivity(intent);
  }

  @Override
  public void didClickGuide(Integer position) {
    switch (position) {
      case 0:
        startActivity(new Intent(getApplicationContext(), QuizScoreActivity.class));
        break;
      case 1:
        startActivity(new Intent(getApplicationContext(), QuizzesActivity.class));
        break;
    }
  }


  @Override
  public void didClickContent(Content content) {
    didClickContent(content, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void attachBaseContext(Context newBase) {
    Configuration configuration = new Configuration();
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase);
    configuration.setLocale(Locale.forLanguageTag(sharedPreferences.getString("current_language_code", "en")));
    Context context = newBase.createConfigurationContext(configuration);
    super.attachBaseContext(context);
    SplitCompat.install(this);
  }

  @Override
  public void openFilterActivity(HashMap<String, HashMap<String, String>> mapFilter, ArrayList<String> selectedTags) {
    Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
    intent.putExtra(FilterActivity.MAP_FILTER, mapFilter);
    intent.putStringArrayListExtra(FilterActivity.SELECTED_TAGS, selectedTags);
    startActivityForResult(intent, REQUEST_CODE_FILTER);
  }


  @Override
  public void clickedContentBlock(Content content) {
    didClickContent(content, false);
  }

  @Override
  public void clickedSpotMapContentLink(String contentId) {
    Content content = new Content();
    content.setId(contentId);
    didClickContent(content, false);
  }

  //Implemented in Quiz apps only
  @Override
  public void onQuizHtmlResponse(String html) {
  }

  private void updateLocation() {
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setFastestInterval(UPDATE_INTERVAL);
    mLocationRequest.setSmallestDisplacement(600F);
    mLocationRequest.setMaxWaitTime(WAIT_INTERVAL);

    Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
    intent.setAction(this.getPackageName());
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, pendingIntent);
  }

  @Override
  public void openContent(@NotNull Content content) {
    Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
    intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
    intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
    startActivity(intent);
  }

  public void openContentId(@NotNull String contentId) {
    Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
    intent.putExtra(ContentActivity.EXTRA_CONTENTID, contentId);
    intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
    startActivity(intent);
  }

  public void openLocId(@NotNull String locId) {
    Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
    intent.putExtra(ContentActivity.EXTRA_LOCID, locId);
    intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false);
    startActivity(intent);
  }

  public void restartFromSettings() {
    recreate();
  }

  @Override
  public void handleOtherScanResult(@NotNull String resultText) {
    //not a valid url scan result
  }

  @Override
  public void finishActivity() {

  }

  @Override
  public void clickedScanVoucher() {

  }
}
