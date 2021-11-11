package com.android.xamoom.tourismtemplate.view.presenter;

import android.app.Activity;

import androidx.annotation.RequiresApi;
import androidx.collection.ArrayMap;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.android.xamoom.tourismtemplate.ContentActivity;
import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.ApiCallback;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.utils.BeaconManager;
import com.xamoom.android.xamoomsdk.Enums.ContentReason;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Spot;

import org.altbeacon.beacon.Beacon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import at.rags.morpheus.Error;

public class HomeFragmentPresenter implements HomeFragmentContract.Presenter {
  private static final String TAG = HomeFragmentPresenter.class.getSimpleName();
  private WeakReference<HomeFragmentContract.View> view;
  private ArrayList<Content> featuredContents = new ArrayList<>();
  private LinkedHashMap<String, ArrayList<Content>> tagContents = new LinkedHashMap<>();
  private ArrayList<Beacon> savedBeacons = new ArrayList<>();
  private ArrayList<Content> savedContents = new ArrayList<>();
  private ArrayList<Content> savedNearbyContents = new ArrayList<>();
  private ArrayList<Spot> savedSpots = new ArrayList<>();
  private ArrayList<Content> activeGeofenceSpots = new ArrayList<>();
  private HashMap<Integer, Content> savedBeaconContent = new HashMap<>();
  private ArrayMap<String, PagingInfo> pagingInfos = new ArrayMap<>();
  private ArrayList<String> tagLoading = new ArrayList<>();
  private Activity activity;

  public HomeFragmentPresenter(HomeFragmentContract.View view, Activity activity) {
    this.view = new WeakReference<>(view);
    this.activity = activity;
  }

  @Override
  public void downloadFeaturedContent(String cursor) {
    final ArrayList<String> tags = new ArrayList<>(1);
    tags.add(Globals.TOP_TIP_TAG);

    ApiUtil.getInstance().loadContents(tags, null, true, new ApiCallback.ListCallback<List<Content>>() {
      @Override
      public void finish(List<Content> result, String cursor, Boolean hasMore) {
        featuredContents.clear();
        featuredContents.addAll(result);
        view.get().downloadedFeaturedContent(featuredContents);
      }
    });
  }

  @Override
  public void downloadConfig() {
    final ArrayList<String> tags = new ArrayList<>(1);
    tags.add(Globals.APP_CONFIG_TAG);
    tagContents.clear();

    ApiUtil.getInstance().loadContents(tags, null, true, new ApiCallback.ListCallback<List<Content>>() {
      @Override
      public void finish(List<Content> result, String cursor, Boolean hasMore) {
        if (result.size() >= 1) {
          Content content = result.get(0);
          LinkedHashMap<String, HashMap<String, String>> config = generateConfig(content.getCustomMeta());
          view.get().downloadedConfigs(config);
          if (config != null) downloadContentList(config);
        } else {
          view.get().downloadedConfigs(new LinkedHashMap<String, HashMap<String, String>>());
          view.get().showNothingFound();
        }
        doNearbyUiUpdate();
      }
    });
  }

  @Override
  public void downloadContent(String tag) {
    PagingInfo pagingInfo = pagingInfos.get(tag);
    if (pagingInfo == null || !pagingInfo.hasMore) {
      return;
    }

    downloadContent(tag, pagingInfo.cursor);
  }

  @Override
  public void didClickImageSliderItem(int position) {
    view.get().openContent(featuredContents.get(position), false);
  }

  @Override
  public void didClickContentItem(Content content, boolean isBeacon) {
    view.get().openContent(content, isBeacon);
  }

  @Override
  public void foundBeacons(ArrayList<Beacon> beacons, ArrayList<Content> contents) {
    Map<Integer, Content> newBeaconsContent = new HashMap<>();
    for (int beaconIndex = 0; beaconIndex < beacons.size(); beaconIndex ++) {
      newBeaconsContent.put(beacons.get(beaconIndex).getId3().toInt(), contents.get(beaconIndex));
    }
    BeaconManager.getInstance().foundBeacons(beacons, contents);

    if (beacons.size() == 0 && savedBeacons.size() > 0) {
      savedBeacons.clear();
      savedBeaconContent.clear();
      doNearbyUiUpdate();
      return;
    }

    boolean isTheSame = BeaconManager.getInstance().compareBeacons(savedBeacons);
    if (isTheSame) {
      Log.v(TAG, "are the same");
      return;
    }

    savedBeacons.clear();
    savedBeacons.addAll(BeaconManager.getInstance().getLastSeenBeacons());

    Iterator it = savedBeaconContent.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Integer, Content> item = (Map.Entry<Integer, Content>) it.next();
      int minorKey = item.getKey();
      boolean contains = false;
      for (Beacon b : savedBeacons) {
        int minor = b.getId3().toInt();
        if (minor == minorKey) {
          contains = true;
        }
      }

      if (!contains) {
        it.remove();
      }
    }

    for (Beacon beacon : savedBeacons) {
      int beaconMinor = beacon.getId3().toInt();
      savedBeaconContent.put(beaconMinor, newBeaconsContent.get(beaconMinor));
//      downloadBeaconContent(beacon.getId3().toInt());
    }
    doNearbyUiUpdate();
  }

  @Override
  public void exitBeaconRegion() {
    resetBeacons();
    BeaconManager.getInstance().clearLastseenBeacons();
    doNearbyUiUpdate();
  }

  private void resetBeacons() {
    savedBeacons.clear();
    savedBeaconContent.clear();
    savedContents.clear();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void doNearbyUiUpdate() {
    ArrayList<Content> backupSavedNearbyContents = (ArrayList<Content>) savedNearbyContents.clone();
    LinkedHashMap<String, ArrayList<Content>> backupContents =
            (LinkedHashMap<String, ArrayList<Content>>) tagContents.clone();
    if (backupContents.containsKey(Globals.NEARBY_TAG)) {
      backupContents.remove(Globals.NEARBY_TAG);
    }
    tagContents.clear();
    ArrayList<Content> beaconContents = new ArrayList<>(savedBeaconContent.values());
    tagContents.putAll(backupContents);

    if (beaconContents.size() > 0 || activeGeofenceSpots.size() > 0) {
      savedNearbyContents = beaconContents;
      savedNearbyContents.addAll(activeGeofenceSpots);
      tagContents.put(Globals.NEARBY_TAG, savedNearbyContents);
      savedNearbyContents = (ArrayList<Content>) savedNearbyContents.stream()
              .distinct()
              .collect(Collectors.toList());
      if (!isNearbyTheSame(savedNearbyContents, backupSavedNearbyContents)) {
        view.get().showBeacons(tagContents);
      }
    } else {
      savedNearbyContents = new ArrayList<>();
      view.get().hideBeacons(tagContents);
    }
  }

  private boolean isNearbyTheSame(ArrayList<Content> oldNearby, ArrayList<Content> newNearby) {
    ArrayList<Content> oldNearbyTemp = (ArrayList<Content>) oldNearby.clone();
    ArrayList<Content> newNearbyTemp = (ArrayList<Content>) newNearby.clone();
    oldNearbyTemp.removeAll(newNearby);
    newNearbyTemp.removeAll(oldNearby);
    return oldNearbyTemp.size() == 0 && newNearbyTemp.size() == 0;
  }

  private void downloadBeaconContent(final int minor) {
    if (savedBeaconContent.containsKey(minor)) {
      doNearbyUiUpdate();
      return;
    }

    ApiUtil.getInstance().loadContentByBeacon(Integer.parseInt(activity.getString(R.string.beacon_major)), minor, ContentReason.BEACON, activity, new ApiCallback.ObjectCallback<Content, Error>() {

      @Override
      public void finish(Content result, Error error) {
        if (result == null) {
          Log.e(TAG, "One beacon content is null");
          doNearbyUiUpdate();
          return;
        }

        savedBeaconContent.put(minor, result);

        Log.d(TAG, "savedBeaconContent " + savedBeaconContent.size());
        Log.d(TAG, "savedBeacons " + savedBeacons.size());

        if (savedBeaconContent.size() == savedBeacons.size()) {
          doNearbyUiUpdate();
        }
      }
    });
  }

  private void downloadContentList(HashMap<String, HashMap<String, String>> config) {
    for (final String tag : config.keySet()) {
      downloadContent(tag, null);
    }
  }

  private void downloadContent(final String tag, String cursor) {
    if (isTagLoading(tag)) {
      return;
    }

    tagLoading.add(tag);

    final ArrayList<String> tagList = new ArrayList<>(1);
    tagList.add(tag);

    ApiUtil.getInstance().loadContents(tagList, cursor, true, new ApiCallback.ListCallback<List<Content>>() {
      @Override
      public void finish(List<Content> result, String cursor, Boolean hasMore) {
        tagLoading.remove(tag);

        pagingInfos.put(tag, new PagingInfo(hasMore, cursor));

        ArrayList<Content> contents = tagContents.get(tag);
        if (contents == null) {
          contents = new ArrayList<>();
        }
        contents.addAll(result);
        tagContents.put(tag, contents);
        if (tagLoading.size() == 0) {
          view.get().downloadedContentLists(tagContents);
        }
      }
    });
  }

  public void downloadGeofenceRegions(boolean isDownload, ArrayList<Spot> backupSavedSpots) {
    Location currentLocation = view.get().getCurrentLocation();

    final ArrayList<String> tags = new ArrayList<>(1);
    tags.add(Globals.GEOFENCE_TAG);
    if (isDownload) {
      ApiUtil.getInstance().loadSpots(tags, null, false, new ApiCallback.ListCallback<List<Spot>>() {
        @Override
        public void finish(List<Spot> result, String cursor, Boolean hasMore) {
          savedSpots = (ArrayList<Spot>) result;
          view.get().setSavedSpots(savedSpots);
          if (currentLocation != null) {
            detectUserActiveGeofences(currentLocation.getLatitude(), currentLocation.getLongitude());
          }
        }
      });
    } else {
      savedSpots = backupSavedSpots;
      if (currentLocation != null) {
        detectUserActiveGeofences(currentLocation.getLatitude(), currentLocation.getLongitude());
      }
    }
  }

  public ArrayList<Spot> getSavedSpots() {
    return savedSpots;
  }

  /**
   * Adds null object to all list of contents with the given tags.
   * This will trigger the ContentAdapter to display the LoadingViewHolder.
   *
   * @param tags List of tags to add the null element
   */
  private void showLoading(ArrayList<String> tags) {
    for (String tag : tags) {
      ArrayList<Content> contents = tagContents.get(tag);
      if (contents == null) {
        contents = new ArrayList<>();
      }

      if (contents.size() == 0 || contents.get(contents.size() - 1) != null) {
        contents.add(null);
      }
      tagContents.put(tag, contents);
    }

    view.get().downloadedContentLists(tagContents);
  }

  private void hideLoading(ArrayList<String> tags) {
    for (String tag : tags) {
      ArrayList<Content> contents = tagContents.get(tag);
      if (contents == null) {
        continue;
      }

      if (contents.size() > 0 && contents.get(contents.size() - 1) == null) {
        contents.remove(contents.size() - 1);
      }
      tagContents.put(tag, contents);
    }

    view.get().downloadedContentLists(tagContents);
  }

  private LinkedHashMap<String, HashMap<String, String>> generateConfig(HashMap<String, String> customMeta) {
    if (customMeta == null) {
      return null;
    }

    LinkedHashMap<String, HashMap<String, String>> config = new LinkedHashMap<>();

    ArrayList<Integer> tagsKeys = new ArrayList<>();
    ArrayList<String> localizedTags = new ArrayList<>();

    // filter out numbered keys and localizedTags
    for(Map.Entry<String, String> entry : customMeta.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      try {
        Integer parsedKey = Integer.parseInt(key);
        tagsKeys.add(parsedKey);
      } catch (NumberFormatException ex) {
        localizedTags.add(key);
      }
    }

    // populate tags ordered with the tagsKeys
    ArrayList<String> tags = new ArrayList<>(tagsKeys.size());
    Collections.sort(tagsKeys);
    for (Integer tagKey : tagsKeys) {
      tags.add(customMeta.get(String.valueOf(tagKey)));
    }

    // generate config by finding tags in localizedTag name
    for (String tag : tags) {
      HashMap<String, String> helper = new HashMap<>();

      for (String localizedTag : localizedTags) {
        if (localizedTag.contains(tag)) { // could break if you have tags with same naming components
          helper.put(localizedTag, customMeta.get(localizedTag));
        }
      }

      config.put(tag, helper);
    }

    return config;
  }

  private boolean isTagLoading(String tag) {
    return tagLoading.contains(tag);
  }

  private class PagingInfo {
    boolean hasMore;
    String cursor;

    PagingInfo(boolean hasMore, String cursor) {
      this.hasMore = hasMore;
      this.cursor = cursor;
    }
  }

  public void detectUserActiveGeofences(double currentLatitude, double currentLongitude) {
    activeGeofenceSpots = new ArrayList<Content>();
    boolean hssAtLeastOneActiveRegion = false;
    for (Spot spot : savedSpots) {
      if (spot.getContent() != null) {
        float spotRadius = Float.parseFloat(Objects.requireNonNull(spot.getCustomMeta().get("diameter"))) / 2;

        float[] result = new float[1];
        Location.distanceBetween(currentLatitude,
                currentLongitude, spot.getLocation().getLatitude(),
                spot.getLocation().getLongitude(), result);

        if (result[0] < spotRadius) {
          hssAtLeastOneActiveRegion = true;
          ApiUtil.getInstance().loadContent(spot.getContent().getId(), activity, new ApiCallback.ObjectCallback<Content, Error>() {
            @Override
            public void finish(Content content, Error error) {
              if (content != null) {
                spot.setName(content.getTitle());
                spot.setPublicImageUrl(content.getPublicImageUrl());

                Content regionContent = spot.getContent();
                regionContent.setTitle(spot.getName());
                regionContent.setPublicImageUrl(spot.getPublicImageUrl());

                if (!activeGeofenceSpots.contains(regionContent)) {
                  activeGeofenceSpots.add(regionContent);
                }
              }
              doNearbyUiUpdate();
            }
          });
        }
      }
    }
    if (hssAtLeastOneActiveRegion == false) {
      doNearbyUiUpdate();
    }
  }
}


