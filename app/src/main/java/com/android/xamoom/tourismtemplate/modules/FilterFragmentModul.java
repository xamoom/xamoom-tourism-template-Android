package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.view.adapter.FilterAdapter;
import com.android.xamoom.tourismtemplate.view.presenter.FilterFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.FilterFragmentPresenter;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FilterFragmentModul {

  private FilterFragmentContract.View view;
  private ArrayList<String> selectedTags;
  private HashMap<String, HashMap<String, String>> mapFilter;
  private FilterAdapter.FilterAdapterListener filterAdapterListener;

  public FilterFragmentModul(FilterFragmentContract.View view, ArrayList<String> selectedTags,
                             HashMap<String, HashMap<String, String>> mapFilter,
                             FilterAdapter.FilterAdapterListener filterAdapterListener) {
    this.view = view;
    this.selectedTags = selectedTags;
    this.mapFilter = mapFilter;
    this.filterAdapterListener = filterAdapterListener;
  }

  @Provides
  public FilterFragmentContract.View provideView() {
    return view;
  };

  @Provides
  @Singleton
  public FilterAdapter provideAdapter() {
    return new FilterAdapter(mapFilter, selectedTags, filterAdapterListener);
  }

  @Provides
  @Singleton
  public FilterFragmentPresenter providePresenter() {
    return new FilterFragmentPresenter(view, selectedTags, mapFilter);
  }
}
