package com.android.xamoom.tourismtemplate.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.models.GuideItem;
import com.android.xamoom.tourismtemplate.modules.AppModule;
import com.android.xamoom.tourismtemplate.modules.DaggerHomeFragmentComponent;
import com.android.xamoom.tourismtemplate.modules.HomeFragmentModul;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.AppgenHelper;
import com.android.xamoom.tourismtemplate.utils.BeaconManager;
import com.android.xamoom.tourismtemplate.utils.BestLocationListener;
import com.android.xamoom.tourismtemplate.utils.BestLocationProvider;
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter;
import com.android.xamoom.tourismtemplate.view.presenter.HomeFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.HomeFragmentPresenter;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.xamoom.android.xamoomsdk.Helpers.GeofenceBroadcastReceiver;
import com.xamoom.android.xamoomsdk.Helpers.XamoomBeaconService;
import com.xamoom.android.xamoomsdk.PushDevice.PushDeviceUtil;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Spot;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements HomeFragmentContract.View,
        HomeRecyclerAdapter.HomeListeners, HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener,
        HomeRecyclerAdapter.HomeListeners.SliderViewHolderListener {
  private final static String TAG = HomeFragment.class.getSimpleName();

  @BindView(R.id.home_recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.home_progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.nothing_found_view)
  View nothingFoundView;

  @Inject
  HomeFragmentPresenter presenter;
  @Inject BestLocationProvider locationProvider;



  private HomeFragmentListener listener;
  private HomeRecyclerAdapter adapter;
  private Timer geofenceRequestTimer;
  private boolean isSendGeofenceRequest = true;
  private ArrayList<Spot> savedSpots = new ArrayList<>();
  private Location currentLocation;
  private boolean isQuizEnabled = false;
  private long timerDelay = 30000;
  private boolean isTimerEnabled = true;
  public static boolean isScreenRefreshed = false;

  private ArrayList<GuideItem> guideItems = new ArrayList<>();

  public HomeFragment() {
    // Required empty public constructor
    geofenceRequestTimer = new Timer();
    geofenceRequestTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (getContext() != null && isTimerEnabled) {
          presenter.downloadGeofenceRegions(true, savedSpots);
          isSendGeofenceRequest = false;
        }
      }
    }, timerDelay, 30000);

  }

  public static HomeFragment newInstance() {
    HomeFragment fragment = new HomeFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AnalyticsUtil.Companion.reportContentView("Home", Globals.ANALYTICS_CONTENT_TYPE_SCREEN, "",
            null);

    isQuizEnabled = getResources().getString(R.string.enable_quiz_feature).equals("true");
    if (isQuizEnabled && guideItems.isEmpty()) {
      if(getContext().getString(R.string.is_background_image).equals("true")) {
        guideItems.add(new GuideItem(R.drawable.background_score, this.getString(R.string.guide_score_screen)));
        guideItems.add(new GuideItem(R.drawable.background_overview, this.getString(R.string.guide_quizzes_screen)));
      } else {
        guideItems.add(new GuideItem(null, this.getString(R.string.guide_score_screen)));
        guideItems.add(new GuideItem(null, this.getString(R.string.guide_quizzes_screen)));
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    ButterKnife.bind(this, view);

    DaggerHomeFragmentComponent.builder()
            .homeFragmentModul(new HomeFragmentModul(this, getActivity()))
            .appModule(new AppModule(getContext()))
            .build()
            .inject(this);

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    isTimerEnabled = true;
  }

  @Override
  public void onResume() {
    super.onResume();

    isScreenRefreshed = true;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    prefs.edit().putBoolean("isHomeScreenNeedToRefresh", isScreenRefreshed).apply();

    setupRecyclerView();
    progressBar.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
    nothingFoundView.setVisibility(View.GONE);
    presenter.downloadFeaturedContent(null);
    presenter.downloadConfig();
    presenter.foundBeacons(BeaconManager.getInstance().getLastSeenBeacons(), BeaconManager.getInstance().getLastSeenContents());

    LocalBroadcastManager.getInstance(getContext()).registerReceiver(mFoundBeaconsReceiver,
            new IntentFilter(XamoomBeaconService.FOUND_BEACON_BROADCAST));

    if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationProvider.startLocationUpdatesWithListener(locationListener);
    }

    presenter.downloadGeofenceRegions(isSendGeofenceRequest, savedSpots);
    isSendGeofenceRequest = false;
  }

  @Override
  public void onStop() {
    super.onStop();
    isTimerEnabled = false;
    LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mFoundBeaconsReceiver);
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private void setupRecyclerView() {
    recyclerView.setAdapter(getAdapter());
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  private final BroadcastReceiver mFoundBeaconsReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getExtras() != null) {
        ArrayList<Content> contents = intent.getExtras().getParcelableArrayList(XamoomBeaconService.CONTENTS);
        ArrayList<Beacon> beacons = intent.getExtras().getParcelableArrayList(XamoomBeaconService.BEACONS);

        SharedPreferences prefs = context.getSharedPreferences("last-nearby", Context.MODE_PRIVATE);
        Date now = new Date();
        long lastFetch = prefs.getLong("nearby", 0);

        if (!adapter.isNearbyIsShown() || lastFetch == 0) {
          if (beacons.size() > 0) {
            presenter.foundBeacons(beacons, contents);
          }
          long fetch = new Date().getTime();
          prefs.edit().putLong("nearby", fetch).apply();
          return;
        }

        if (lastFetch + 1000 <= now.getTime()) {
          presenter.foundBeacons(beacons, contents);
          long fetch = new Date().getTime();
          prefs.edit().putLong("nearby", fetch).apply();
        }
      }
    }
  };
  private final BroadcastReceiver mExitRegionReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      presenter.exitBeaconRegion();
    }
  };


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof HomeFragmentListener) {
      listener = (HomeFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement MapFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();

    listener = null;
  }

  private HomeRecyclerAdapter getAdapter() {
    if (adapter == null) {
      adapter = new HomeRecyclerAdapter(this, AppgenHelper.getInstance(getContext()).getNearbyRowIndex());
    }
    return adapter;
  }

  @Override
  public void downloadedFeaturedContent(ArrayList<Content> contents) {
    adapter.setFeaturedContents(contents);
  }

  @Override
  public void downloadedConfigs(LinkedHashMap<String, HashMap<String, String>> config) {
    adapter.setConfig(config);
  }

  @Override
  public void downloadedContentLists(LinkedHashMap<String, ArrayList<Content>> contentLists) {
    if (isQuizEnabled && contentLists.size() > 0 && !adapter.isGuideShown()) {
      adapter.showGuide(guideItems);
    }

    for (LinkedHashMap.Entry<String, ArrayList<Content>> entry : contentLists.entrySet()) {
      String key = entry.getKey();
      ArrayList<Content> value = entry.getValue();

      if (value.isEmpty()) {
        adapter.removeConfigWithKey(key);
      }
    }

    adapter.setContentLists(contentLists);
    adapter.notifyDataSetChanged();
    recyclerView.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void  showBeacons(final LinkedHashMap<String, ArrayList<Content>> contentLists) {
    adapter.setContentLists(contentLists);
    if (isQuizEnabled) { adapter.showGuide(guideItems); }

    recyclerView.post(new Runnable() {
      @Override
      public void run() {
        adapter.showNearby();
      }
    });
  }

  @Override
  public void hideBeacons(LinkedHashMap<String, ArrayList<Content>> contentLists) {
    adapter.setContentLists(contentLists);
    adapter.hideNearby();
  }

  @Override
  public void openContent(Content content, boolean isBeacon) {
    listener.didClickContent(content, isBeacon);
  }

  @Override
  public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override
  public void showNothingFound() {
    nothingFoundView.setVisibility(View.VISIBLE);
  }

  @Override
  public void didClickSliderItem(int position) {
    presenter.didClickImageSliderItem(position);
  }

  @Override
  public void didClickContent(Content content, boolean isBeacon) {
    presenter.didClickContentItem(content, isBeacon);
  }

  @Override
  public void loadMore(String tag) {
    presenter.downloadContent(tag);
  }

  @Override
  public void didClickGuide(Integer position) {
    listener.didClickGuide(position);
  }

  public interface HomeFragmentListener {
    void didClickContent(Content content, boolean isBeacon);
    void didClickGuide(Integer position);
  }

  public RecyclerView getRecyclerView() {
    return recyclerView;
  }

  private BestLocationListener locationListener = new BestLocationListener() {
    @Override
    public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
      currentLocation = location;
      presenter.detectUserActiveGeofences(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
  };

  public void setSavedSpots(ArrayList<Spot> savedActiveSpots) {
    savedSpots = savedActiveSpots;
  }

  public Location getCurrentLocation() {
    if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Location loc = new Location("dummyprovider");
      loc.setLatitude(20.3);
      loc.setLongitude(52.6);
      return loc;
    }
    return currentLocation;
  }
}
