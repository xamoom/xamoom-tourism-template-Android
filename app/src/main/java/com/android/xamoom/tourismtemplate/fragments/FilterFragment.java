package com.android.xamoom.tourismtemplate.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.modules.DaggerFilterFragmentComponent;
import com.android.xamoom.tourismtemplate.modules.FilterFragmentModul;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.view.adapter.FilterAdapter;
import com.android.xamoom.tourismtemplate.view.presenter.FilterFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.FilterFragmentPresenter;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragmentListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment implements FilterFragmentContract.View,
        FilterAdapter.FilterAdapterListener{
  private static final String ARG_MAP_FILTER = "mapFilter";
  private static final String ARG_SELECTED_TAGS = "selectedTags";

  @BindView(R.id.filter_recycler_view) RecyclerView recyclerView;
  SwitchCompat selectAllSwitch;

  @Inject FilterAdapter adapter;
  @Inject FilterFragmentPresenter presenter;
  private HashMap<String, HashMap<String, String>> mapFilter;
  private ArrayList<String> selectedTags;
  private FilterFragmentListener listener;

  public FilterFragment() {
    // Required empty public constructor
  }

  public static FilterFragment newInstance(FilterFragmentListener listener,
                                           HashMap<String, HashMap<String, String>> mapFilter,
                                           ArrayList<String> selectedTags) {
    FilterFragment fragment = new FilterFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_MAP_FILTER, mapFilter);
    args.putStringArrayList(ARG_SELECTED_TAGS, selectedTags);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AnalyticsUtil.Companion.reportContentView("Filter", Globals.ANALYTICS_CONTENT_TYPE_SCREEN, "",
            null);
    setHasOptionsMenu(true);

    if (getArguments() != null) {
      mapFilter = (HashMap<String, HashMap<String, String>>) getArguments().getSerializable(ARG_MAP_FILTER);
      selectedTags = getArguments().getStringArrayList(ARG_SELECTED_TAGS);
    }

    DaggerFilterFragmentComponent.builder()
            .filterFragmentModul(new FilterFragmentModul(this, selectedTags, mapFilter, this))
            .build()
            .inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_filter, container, false);
    ButterKnife.bind(this, view);

    setupRecyclerView();

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_filter_activity, menu);

    MenuItem switchItem = menu.findItem(R.id.menu_item_switch);
    switchItem.setActionView(R.layout.menu_switch_layout);

    selectAllSwitch = (SwitchCompat) switchItem.getActionView().findViewById(R.id.switch_all);
    selectAllSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        presenter.didChangeSelectAll(isChecked);
      }
    });

    presenter.didIntializeSelectAllSwitch();

    super.onCreateOptionsMenu(menu, inflater);
  }

  private void setupRecyclerView() {
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof FilterFragmentListener) {
      listener = (FilterFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement FilterFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  @Override
  public void didChangeSelectedTag(String tag, boolean isSelected) {
    presenter.didUpdateSelectedTag(tag, isSelected);
  }

  @Override
  public void updateSelectAllSwitch(boolean isSelected) {
    selectAllSwitch.setChecked(isSelected);
  }

  @Override
  public void didUpdateSelectedTags(ArrayList<String> selectedTags) {
    this.selectedTags = selectedTags;
  }

  @Override
  public void shouldUpdateRecylcerView() {
    adapter.updateRecyclerView();
  }

  public interface FilterFragmentListener {
    void updateSelectedTags(ArrayList<String> selectedTags);
  }
}
