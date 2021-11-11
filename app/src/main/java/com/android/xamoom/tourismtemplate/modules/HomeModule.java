package com.android.xamoom.tourismtemplate.modules;

import android.content.Context;

import com.android.xamoom.tourismtemplate.fragments.ScannerFragment;
import com.android.xamoom.tourismtemplate.utils.BestLocationProvider;
import com.android.xamoom.tourismtemplate.view.presenter.HomeScreenContract;
import com.android.xamoom.tourismtemplate.view.presenter.HomeScreenPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

  private HomeScreenContract.View view;
  private ScannerFragment.QrScannerListener listener;

  public HomeModule(HomeScreenContract.View view, ScannerFragment.QrScannerListener listener) {
    this.view = view;
    this.listener = listener;
  }

  @Provides
  public HomeScreenContract.View provideView() {
    return view;
  }

  @Singleton
  @Provides
  public HomeScreenPresenter providePresenter() {
    return new HomeScreenPresenter(view, listener);
  }
}
