package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.MapFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, MapFragmentModul.class})
public interface MapFragmentComponent {
  void inject(MapFragment mapFragment);
}
