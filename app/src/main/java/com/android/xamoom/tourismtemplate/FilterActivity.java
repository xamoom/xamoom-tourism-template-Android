package com.android.xamoom.tourismtemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.xamoom.tourismtemplate.fragments.FilterFragment;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity implements FilterFragment.FilterFragmentListener {
  public final static String SELECTED_TAGS = "selectedTags";
  public final static String MAP_FILTER = "tags";

  @BindView(R.id.toolbar) Toolbar toolbar;

  private HashMap<String, HashMap<String, String>> mapFilter;
  private ArrayList<String> selectedTags;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);

    ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(R.string.filter_activity_title);
    }

    Intent intent = getIntent();
    if (intent != null) {
      mapFilter = (HashMap<String, HashMap<String, String>>) intent.getSerializableExtra(MAP_FILTER);
      selectedTags = intent.getStringArrayListExtra(SELECTED_TAGS);

      FilterFragment filterFragment = FilterFragment.newInstance(this, mapFilter, selectedTags);
      getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.main_frame_layout, filterFragment)
              .commit();
    }
  }

  @Override
  public void finish() {
    Intent intent = new Intent();
    intent.putStringArrayListExtra(SELECTED_TAGS, selectedTags);
    setResult(RESULT_OK, intent);

    super.finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void updateSelectedTags(ArrayList<String> selectedTags) {
    this.selectedTags = selectedTags;
  }
}
