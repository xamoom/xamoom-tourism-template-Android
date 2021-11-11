package com.android.xamoom.tourismtemplate.view.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.view.CheckBoxViewHolder;
import com.android.xamoom.tourismtemplate.view.HeaderViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CheckBoxViewHolder.CheckBoxViewHolderListener {
  private final static int VIEW_TYPE_HEADER = 0;
  private final static int VIEW_TYPE_SWITCH = 1;

  private HashMap<String, HashMap<String, String>> mapFilter;
  private ArrayList<String> selectedTags;
  private ArrayList<Integer> headerPositions = new ArrayList<>();
  private LinkedHashMap<String, String> flatTags = new LinkedHashMap<>();
  private Boolean updateSwitches = false;
  private FilterAdapterListener listener;

  public FilterAdapter(HashMap<String, HashMap<String, String>> mapFilter,
                       ArrayList<String> selectedTags, FilterAdapterListener listener) {
    this.mapFilter = mapFilter;
    this.selectedTags = selectedTags;
    this.listener = listener;

    flattenTags();
  }

  @Override
  public int getItemViewType(int position) {
    if (headerPositions.contains(position)) {
      return VIEW_TYPE_HEADER;
    } else {
      return VIEW_TYPE_SWITCH;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_SWITCH) {
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.viewholder_check_box, parent, false);
      return new CheckBoxViewHolder(view, this);
    } else if (viewType == VIEW_TYPE_HEADER) {
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.viewholder_header, parent, false);
      return new HeaderViewHolder(view);
    }

    throw new IllegalStateException();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof CheckBoxViewHolder) {
      CheckBoxViewHolder checkBoxViewHolder = (CheckBoxViewHolder) holder;
      checkBoxViewHolder.configure(getName(position), selectedTags.contains(getTag(position)));
    }

    if (holder instanceof HeaderViewHolder) {
      HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
      headerViewHolder.setTitle(getName(position));
    }

    if (position == getItemCount() - 1) {
      updateSwitches = false;
    }
  }

  @Override
  public int getItemCount() {
    return this.flatTags.size();
  }

  public void updateRecyclerView() {
    updateSwitches = true;
    notifyDataSetChanged();
  }

  private void flattenTags() {
    for (Map.Entry<String, HashMap<String, String>> entry : this.mapFilter.entrySet()) {
      flatTags.put(entry.getKey(), entry.getKey());
      headerPositions.add(flatTags.size() - 1);

      for (Map.Entry<String, String> tagEntry : entry.getValue().entrySet()) {
        flatTags.put(tagEntry.getKey(), tagEntry.getValue());
      }
    }

    Map.Entry<String, String> heading = flatTags.entrySet().iterator().next();
    flatTags.remove(flatTags.keySet().iterator().next());

    LinkedHashMap<String, String> flatTagsCopy = (LinkedHashMap<String, String>) flatTags.clone();

    flatTags.clear();
    flatTags.put(heading.getKey(), heading.getValue());
    flatTags.putAll(sortMapByKeys(flatTagsCopy));
  }

  private LinkedHashMap<String, String> sortMapByKeys(LinkedHashMap<String, String> linkedHashMap) {
    LinkedList<String> sortedKey = new LinkedList<String>(Arrays.asList(Globals.FILTER_TAGS_SORTED));

    LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();
    for (String key : sortedKey) {
      sortedMap.put(key, linkedHashMap.get(key));
    }

    return sortedMap;
  }

  private String getName(int position) {
    List<String> l = new ArrayList<String>(flatTags.values());
    return l.get(position);
  }

  private String getTag(int position) {
    List<String> l = new ArrayList<String>(flatTags.keySet());
    return l.get(position);
  }

  @Override
  public void didChangeSelected(int position, boolean isSelected) {
    if (listener != null && !updateSwitches) {
      listener.didChangeSelectedTag(getTag(position), isSelected);
    }
  }

  public interface FilterAdapterListener {
    void didChangeSelectedTag(String tag, boolean isSelected);
  }
}
