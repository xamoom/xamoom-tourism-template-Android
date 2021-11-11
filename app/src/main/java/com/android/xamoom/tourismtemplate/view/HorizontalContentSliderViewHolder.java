package com.android.xamoom.tourismtemplate.view;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.MainApp;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.view.adapter.ContentAdapter;
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;



public class HorizontalContentSliderViewHolder extends RecyclerView.ViewHolder
    implements ContentAdapter.ContentAdapterListener {

  @BindView(R.id.horizontal_content_recyclerview) RecyclerView recyclerView;
  @BindView(R.id.title_label) TextView titleTextView;

  @Inject
  SharedPreferences sharedPreferences;
  @Inject EnduserApi enduserApi;

  private Context context;
  private HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener listener;
  private ContentAdapter contentAdapter;
  private ArrayList<Content> contents = new ArrayList<>();
  private String tag;
  private HashMap<String, String> config;

  public HorizontalContentSliderViewHolder(View itemView,
                                           HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener listener) {
    super(itemView);

    context = itemView.getContext();
    this.listener = listener;

    ButterKnife.bind(this, itemView);

    MainApp.app().getAppComponent().inject(this);

    setupRecyclerView();
  }

  private void setupRecyclerView() {
    contentAdapter = new ContentAdapter(contents, this, listener);
    recyclerView.setAdapter(contentAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

    HorizontalContentItemDecoration decoration = new HorizontalContentItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.default_margin),
            context.getResources().getDimensionPixelSize(R.dimen.default_margin));
    recyclerView.addItemDecoration(decoration);
  }

  private void updateViewHolder() {
    titleTextView.setText(getLocalizedName());

    //
    if (tag.equalsIgnoreCase(Globals.NEARBY_TAG)) {
      contentAdapter.setNearby(true);
    }
    contentAdapter.notifyDataSetChanged();
  }

  private String getLocalizedName() {
    if (config == null) {
      return tag;
    }

    if (tag.equalsIgnoreCase(Globals.NEARBY_TAG)) {
      return context.getString(R.string.nearby);
    }

    String languageIdentifier = sharedPreferences.getString("current_language_code", null) != null
            ? sharedPreferences.getString("current_language_code", null) : enduserApi.getSystemLanguage();
    languageIdentifier = String.format("%s-%s", tag, languageIdentifier);

    String localizedName = config.get(languageIdentifier);
    if (localizedName == null) {

      // if the language is not found, it will try to use en
      languageIdentifier = "en";
      languageIdentifier = String.format("%s-%s", tag, languageIdentifier);
      localizedName = config.get(languageIdentifier);

      // if en is also not found, it will return the tag name
      if (localizedName == null) {
        return "";
      }
    }

    return localizedName;
  }

  public void setContents(ArrayList<Content> contents) {
    if (contents == null) {
      return;
    }

    this.contents = contents;
    contentAdapter.setContents(contents);
    updateViewHolder();
  }

  public void updateTitle(String name) {
    this.tag = name;
    updateViewHolder();
  }

  public void updateTitle(String tag, HashMap<String, String> config) {
    this.tag = tag;
    this.config = config;
    updateViewHolder();
  }

  @Override
  public void loadMore() {
    listener.loadMore(tag);
  }
}
