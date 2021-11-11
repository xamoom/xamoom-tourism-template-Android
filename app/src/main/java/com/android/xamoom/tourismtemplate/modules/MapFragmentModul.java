package com.android.xamoom.tourismtemplate.modules;

import android.content.Context;

import com.android.xamoom.tourismtemplate.utils.BestLocationProvider;
import com.android.xamoom.tourismtemplate.view.presenter.MapFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.MapFragmentPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MapFragmentModul {

  private MapFragmentContract.View view;
  private Context context;

  public MapFragmentModul(MapFragmentContract.View view, Context context) {
    this.view = view;
    this.context = context;
  }

  @Provides
  public MapFragmentContract.View provideView() {
    return view;
  }

  @Singleton
  @Provides
  public MapFragmentPresenter provideMapFragmentPresenter() {
    return new MapFragmentPresenter(view, context);
  }

  @Singleton
  @Provides
  public BestLocationProvider provideBestLocationProvider(Context context) {
    return new BestLocationProvider(context, true, true, 1000, 1000, (long)60*60, 1000);
  }
}
