package com.android.xamoom.tourismtemplate.view.presenter;

import android.content.Context;
import android.util.Log;

import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.ApiCallback;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.google.android.gms.common.api.Api;
import com.xamoom.android.xamoomsdk.Resource.Spot;
import com.xamoom.android.xamoomsdk.Resource.Style;
import com.xamoom.android.xamoomsdk.Resource.System;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import at.rags.morpheus.Error;

public class MapFragmentPresenter implements MapFragmentContract.Presenter {

  private Context context;
  private WeakReference<MapFragmentContract.View> view;
  private ArrayList<Spot> savedSpots = new ArrayList<>();
  private ArrayList<String> selectedTags = new ArrayList<>();
  private ArrayList<String> downloadedTags = new ArrayList<>();
  private HashMap<String, HashMap<String, String>> mapFilters = new HashMap<>();
  private ArrayList<String> tags = new ArrayList<>();

  public MapFragmentPresenter(MapFragmentContract.View view, Context context) {
    this.view = new WeakReference<>(view);
    this.context = context;

    //setupMapFilters();
  }

  private void setupMapFilters() {
    LinkedHashMap<String, String> filterTags = new LinkedHashMap<>();
    filterTags.put("ausflugsziel", context.getString(R.string.map_filter_tag_attractions));
    filterTags.put("architektur", context.getString(R.string.map_filter_tag_architecture));
    filterTags.put("denkmal", context.getString(R.string.map_filter_tag_monument));
    filterTags.put("kirche", context.getString(R.string.map_filter_tag_churches));
    filterTags.put("galmus", context.getString(R.string.map_filter_tag_galmus));
    filterTags.put("natur", context.getString(R.string.map_filter_tag_nature));
    filterTags.put("sightseeing", context.getString(R.string.map_filter_tag_sightseeing));
    filterTags.put("shopping", context.getString(R.string.map_filter_tag_shopping));

    mapFilters.put(context.getString(R.string.map_filter_section_see_experience), filterTags);

    for (HashMap<String, String> tagMap : mapFilters.values()) {
      for (String tag : tagMap.keySet()) {
        tags.add(tag);
      }
    }

    selectedTags.addAll(tags);
  }

  @Override
  public void downloadSpots() {
    if (savedSpots.size() > 0) {
      view.get().stopLoading();
      return;
    }

    view.get().startLoading();

    if (ApiUtil.getInstance().getStyle() == null || ApiUtil.getInstance().getStyle().getCustomMarker() == null) {
      downloadStyle();
    }

    /*
    if (selectedTags.containsAll(downloadedTags) && downloadedTags.containsAll(selectedTags)) {
      view.get().stopLoading();
      view.get().didDownloadSpots(savedSpots);
      return;
    }

    savedSpots.clear();

    if (selectedTags.size() == 0) {
      view.get().stopLoading();
      view.get().didDownloadSpots(savedSpots);
      return;
    }


    downloadedTags.clear();
    downloadedTags.addAll(selectedTags);
    */

    final ArrayList<String> tags = new ArrayList<>(1);
    tags.add("MAP");
    tags.add("map");
    ApiUtil.getInstance().loadSpots(tags, null, false, new ApiCallback.ListCallback<List<Spot>>() {
      @Override
      public void finish(List<Spot> result, String cursor, Boolean hasMore) {
        savedSpots.addAll(result);

        if (hasMore) {
          ApiUtil.getInstance().loadSpots(tags, cursor, false, this);
        } else {
          view.get().stopLoading();
          view.get().didDownloadSpots(savedSpots);
        }
      }
    });
  }

  private void downloadStyle() {
    if (ApiUtil.getInstance().getSystem() == null) {
      ApiUtil.getInstance().loadSystem(new ApiCallback.ObjectCallback<System, Error>() {
        @Override
        public void finish(System result, Error error) {
          if (error == null) {
            downloadStyle();
          }
        }
      });
    } else {
      ApiUtil.getInstance().loadStyle(ApiUtil.getInstance().getSystem().getStyle().getId(),
          new ApiCallback.ObjectCallback<Style, Error>() {
            @Override
            public void finish(Style result, Error error) {
              Log.v("Test", "Style: " + result);
            }
          });
    }
  }

  public void setSelectedTags(ArrayList<String> selectedTags) {
    this.selectedTags = selectedTags;
  }

  public HashMap<String, HashMap<String, String>> getMapFilters() {
    return mapFilters;
  }

  public ArrayList<String> getSelectedTags() {
    return selectedTags;
  }
}
