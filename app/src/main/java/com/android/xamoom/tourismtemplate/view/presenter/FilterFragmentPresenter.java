package com.android.xamoom.tourismtemplate.view.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterFragmentPresenter implements FilterFragmentContract.Presenter {
  private WeakReference<FilterFragmentContract.View> view;
  private HashMap<String, HashMap<String, String>> mapFilter;
  private ArrayList<String> selectedTags;
  private ArrayList<String> allTags = new ArrayList<>();
  private boolean skipSelectAll = false;

  public FilterFragmentPresenter(FilterFragmentContract.View view, ArrayList<String> selectedTags,
                                 HashMap<String, HashMap<String, String>> mapFilter) {
    this.view = new WeakReference<FilterFragmentContract.View>(view);
    this.selectedTags = selectedTags;
    this.mapFilter = mapFilter;

    createAllTags();
  }

  private void createAllTags() {
    for (Map.Entry<String, HashMap<String, String>> entry : mapFilter.entrySet()) {
      for (Map.Entry<String, String> tagEntry : entry.getValue().entrySet()) {
        allTags.add(tagEntry.getKey());
      }
    }
  }

  @Override
  public void didUpdateSelectedTag(String tag, boolean isSelected) {
    if (selectedTags.contains(tag)) {
      if (!isSelected) {
        selectedTags.remove(tag);
      }
    } else {
      if (isSelected) {
        selectedTags.add(tag);
      }
    }

    skipSelectAll = true;
    view.get().didUpdateSelectedTags(selectedTags);
    view.get().updateSelectAllSwitch(selectedTags.size() == allTags.size());
    skipSelectAll = false;
  }

  @Override
  public void didChangeSelectAll(boolean isSelected) {
    if (skipSelectAll) {
      skipSelectAll = false;
      return;
    }
    if (isSelected) {
      selectedTags.clear();
      selectedTags.addAll(allTags);
    } else {
      selectedTags.clear();
    }

    view.get().didUpdateSelectedTags(selectedTags);
    view.get().shouldUpdateRecylcerView();
  }

  @Override
  public void didIntializeSelectAllSwitch() {
    view.get().updateSelectAllSwitch(selectedTags.size() == allTags.size());
  }
}
