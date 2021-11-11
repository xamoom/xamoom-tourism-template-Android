package com.android.xamoom.tourismtemplate.view.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.models.GuideItem;
import com.android.xamoom.tourismtemplate.utils.AppgenHelper;
import com.android.xamoom.tourismtemplate.view.HorizontalContentSliderViewHolder;
import com.android.xamoom.tourismtemplate.view.SliderViewHolder;
import com.android.xamoom.tourismtemplate.view.quiz.GuideSliderViewHolder;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final static int VIEW_TYPE_IMAGE_SLIDER = 0;
  private final static int VIEW_TYPE_HORIZONTAL_CONTENT_SLIDER = 1;
  private final static int VIEW_TYPE_GUIDE = 2;
  private final static int VIEW_TYPE_NEARBY = 3;

  private ArrayList<Integer> viewTypes = new ArrayList<>();
  private ArrayList<Content> featuredContents = new ArrayList<>();
  private LinkedHashMap<String, HashMap<String, String>> config = new LinkedHashMap<>(0);
  private LinkedHashMap<String, ArrayList<Content>> contentLists = new LinkedHashMap<>(0);
  private HomeListeners listener;
  private ArrayList<GuideItem> guides = new ArrayList<>();
  private boolean nearbyIsShown = false;
  private int nearbyRowIndex = 1;

  public HomeRecyclerAdapter(HomeListeners listener, int nearbyRowIndex) {
    this.listener = listener;

    viewTypes.add(VIEW_TYPE_IMAGE_SLIDER);
    this.nearbyRowIndex = nearbyRowIndex;
  }

  @Override
  public int getItemViewType(int position) {
    return viewTypes.get(position);
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_TYPE_IMAGE_SLIDER:
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_slider, parent, false);
        return new SliderViewHolder(view, (HomeListeners.SliderViewHolderListener) listener);
      case VIEW_TYPE_GUIDE:
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_horizontal_content_slider, parent, false);
        return new GuideSliderViewHolder(view, (HomeListeners.OnHorizontalContentListener) listener, guides);
      case VIEW_TYPE_NEARBY: // falltrough
      case VIEW_TYPE_HORIZONTAL_CONTENT_SLIDER:
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_horizontal_content_slider, parent, false);
        return new HorizontalContentSliderViewHolder(view, (HomeListeners.OnHorizontalContentListener) listener);
    }

    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof SliderViewHolder) {
      SliderViewHolder sliderViewHolder = (SliderViewHolder) holder;
      sliderViewHolder.updateContents(featuredContents);
    }

    if (holder instanceof HorizontalContentSliderViewHolder) {
      HorizontalContentSliderViewHolder horizontalSlider = (HorizontalContentSliderViewHolder) holder;
      String configKey = getConfigKey(position - 1);
      if (configKey != null) {
        horizontalSlider.updateTitle(configKey, getConfig(position - 1));
      }

      ArrayList<Content> contents = contentLists.get(getConfigKey(position - 1));
      horizontalSlider.setContents(contents);
    }

    if (holder instanceof GuideSliderViewHolder) {
      GuideSliderViewHolder guideSliderViewHolder = (GuideSliderViewHolder) holder;
      String configKey = getConfigKey(position - 1);

      if (configKey != null) {
        guideSliderViewHolder.updateTitle(configKey, getConfig(position - 1));
      }
    }
  }

  @Override
  public int getItemCount() {
    return viewTypes.size();
  }

  private String getConfigKey(int position) {
    if (position == -1 || position >= config.keySet().toArray().length) {
      return null;
    }
    return (String) config.keySet().toArray()[position];
  }

  private HashMap<String, String> getConfig(int position) {
    return config.get(getConfigKey(position));
  }

  private void updateViewTypes() {
    viewTypes.clear();
    viewTypes.add(VIEW_TYPE_IMAGE_SLIDER);

    nearbyIsShown = false;

    for (Object tag : this.config.keySet().toArray()) {
      viewTypes.add(VIEW_TYPE_HORIZONTAL_CONTENT_SLIDER);
    }
  }

  public void showNearby() {
    if (viewTypes.size() < nearbyRowIndex || nearbyIsShown) {
      notifyItemChanged(nearbyRowIndex);
      return;
    }

    nearbyIsShown = true;

    addConfigAndType(VIEW_TYPE_NEARBY, Globals.NEARBY_TAG);

    notifyItemInserted(nearbyRowIndex);
  }

  public void addConfigAndType(int type, String tag) {
    viewTypes.add(nearbyRowIndex, type);

    LinkedHashMap<String, HashMap<String, String>> backup =
            (LinkedHashMap<String, HashMap<String, String>>) config.clone();
    config.clear();
    if (isGuideShown()) { config.put(Globals.GUIDE_TAG, new HashMap<String, String>()); }
    config.put(tag, new HashMap<String, String>(0));
    config.putAll(backup);
  }

  public void hideNearby() {
    nearbyIsShown = false;

    if (viewTypes.size() < nearbyRowIndex + 1) {
      return;
    }

    if (viewTypes.size() > nearbyRowIndex && viewTypes.get(nearbyRowIndex) != VIEW_TYPE_NEARBY) {
      return;
    }

    viewTypes.remove(nearbyRowIndex);
    config.remove(Globals.NEARBY_TAG);
    notifyItemRemoved(nearbyRowIndex);
  }

  public void setFeaturedContents(ArrayList<Content> featuredContents) {
    this.featuredContents = new ArrayList<>(featuredContents);
  }

  public void setConfig(LinkedHashMap<String, HashMap<String, String>> config) {
    this.config = new LinkedHashMap<>(config);
    updateViewTypes();
  }

  public void removeConfigWithKey(String key) {
    List<String> indexes = new ArrayList<>(this.config.keySet());
    int index = indexes.indexOf(key) + 1;

    this.config.remove(key);
    viewTypes.remove(index);
    notifyItemRemoved(index);
  }

  public boolean isGuideShown() {
    return viewTypes.contains(VIEW_TYPE_GUIDE);
  }

  public void showGuide(ArrayList<GuideItem> guides) {
    this.guides = guides;
    if (!isGuideShown()) {
      viewTypes.add(1, VIEW_TYPE_GUIDE);
      LinkedHashMap<String, HashMap<String, String>> backup =
              (LinkedHashMap<String, HashMap<String, String>>) config.clone();
      config.clear();
      config.put(Globals.GUIDE_TAG, new HashMap<String, String>());
      config.putAll(backup);
      notifyItemInserted(1);
      return;
    }
    notifyItemChanged(1);
  }

  public boolean isNearbyIsShown() {
    return nearbyIsShown;
  }

  public void setContentLists(LinkedHashMap<String, ArrayList<Content>> contentLists) {
    this.contentLists = new LinkedHashMap<>(contentLists);
  }

  public interface HomeListeners {
    interface OnHorizontalContentListener {
      void didClickContent(Content content, boolean isBeacon);
      void loadMore(String tag);
      void didClickGuide(Integer position);
    }

    interface SliderViewHolderListener {
      void didClickSliderItem(int position);
    }
  }
}
