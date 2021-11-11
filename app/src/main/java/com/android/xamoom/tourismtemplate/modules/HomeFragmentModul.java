package com.android.xamoom.tourismtemplate.modules;

import android.app.Activity;
import android.content.Context;

import com.android.xamoom.tourismtemplate.utils.BestLocationProvider;
import com.android.xamoom.tourismtemplate.view.presenter.HomeFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.HomeFragmentPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by raphaelseher on 15/05/2017.
 */

@Module
public class HomeFragmentModul {
  private HomeFragmentContract.View view;
  private Activity activity;

  public HomeFragmentModul(HomeFragmentContract.View view, Activity activity) {
    this.view = view;
    this.activity = activity;
  }

  @Provides
  public HomeFragmentContract.View provideView() {
    return view;
  }

  @Singleton
  @Provides
  public HomeFragmentPresenter providePresenter() {
    return new HomeFragmentPresenter(view, activity);
  }

  @Singleton
  @Provides
  public BestLocationProvider provideBestLocationProvider(Context context) {
    return new BestLocationProvider(context, true, true, 1000, 1000, 2, 5);
  }
}
