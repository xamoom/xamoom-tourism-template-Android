package com.android.xamoom.tourismtemplate.view.presenter;


import androidx.fragment.app.Fragment;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.fragments.ContentFragment;
import com.android.xamoom.tourismtemplate.fragments.HomeFragment;
import com.android.xamoom.tourismtemplate.fragments.MapFragment;
import com.android.xamoom.tourismtemplate.fragments.ScannerFragment;
import com.android.xamoom.tourismtemplate.fragments.SettingsFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HomeScreenPresenter implements HomeScreenContract.Presenter {

  private WeakReference<HomeScreenContract.View> view;
  private HomeFragment homeFragment;
  private MapFragment mapFragment;
  private ScannerFragment scannerFragment;
  private ContentFragment contentFragment;
  private SettingsFragment settingsFragment;
  private ScannerFragment.QrScannerListener listener;

  public HomeScreenPresenter(HomeScreenContract.View view, ScannerFragment.QrScannerListener listener) {
    this.view = new WeakReference<>(view);
    this.listener = listener;
  }

  @Override
  public void didSelectTab(int res) {
    Fragment fragment = null;

    switch (res) {
      case R.id.tab_home:
        fragment = getHomeFragment();
        break;
      case R.id.tab_map:
        fragment = getMapFragment();
        break;
      case R.id.tab_qr:
        fragment = getScanFragment();
        break;
      case R.id.tab_info:
        fragment = getInfoFragment();
        break;
      case R.id.tab_setting:
        fragment = getSettingsFragment();
        break;
    }

    view.get().changeTab(fragment);
  }

  @Override
  public void didUpdateSelectedTags(ArrayList<String> selectedTags) {
    mapFragment.setSelectedTags(selectedTags);
  }

  private Fragment getHomeFragment() {
    if (homeFragment == null) {
      homeFragment = HomeFragment.newInstance();
    }
    return homeFragment;
  }

  private Fragment getMapFragment() {
    if (mapFragment == null) {
      mapFragment = MapFragment.newInstance();
    }
    return mapFragment;
  }

  private Fragment getScanFragment() {
      if (scannerFragment == null) {
          scannerFragment = ScannerFragment.Companion.newInstance(listener);
      }
      return scannerFragment;
  }

  private Fragment getInfoFragment() {
    if (contentFragment == null) {
      contentFragment = new ContentFragment();
      contentFragment.setTag(Globals.INFO_TAG);
      contentFragment.setShowBackButton(false);
    }

    return contentFragment;
  }

  private Fragment getSettingsFragment() {
    if (settingsFragment == null) {
      settingsFragment = SettingsFragment.newInstance();
    }

    return settingsFragment;
  }
}
