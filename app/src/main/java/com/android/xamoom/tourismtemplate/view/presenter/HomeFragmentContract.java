package com.android.xamoom.tourismtemplate.view.presenter;

import android.location.Location;

import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Spot;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface HomeFragmentContract {
  interface View {
    void downloadedFeaturedContent(ArrayList<Content> contents);
    void downloadedConfigs(LinkedHashMap<String, HashMap<String, String>> config);
    void downloadedContentLists(LinkedHashMap<String, ArrayList<Content>> contentLists);
    void showBeacons(LinkedHashMap<String, ArrayList<Content>> contentLists);
    void hideBeacons(LinkedHashMap<String, ArrayList<Content>> contentLists);
    void openContent(Content content, boolean isBeacon);
    void hideLoading();
    void showNothingFound();
    Location getCurrentLocation();
    void setSavedSpots(ArrayList<Spot> savedSpots);
  }

  interface Presenter {
    void downloadFeaturedContent(String cursor);
    void downloadConfig();
    void downloadContent(String tag);
    void didClickImageSliderItem(int position);
    void didClickContentItem(Content content, boolean isBeacon);
    void foundBeacons(ArrayList<Beacon> beacons, ArrayList<Content> contents);
    void exitBeaconRegion();
    void downloadGeofenceRegions(boolean isDownload, ArrayList<Spot> backupSavedSpots);
  }
}
