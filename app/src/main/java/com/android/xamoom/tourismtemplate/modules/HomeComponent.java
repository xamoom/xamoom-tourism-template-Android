package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.HomeActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, HomeModule.class})
public interface HomeComponent {
  void inject(HomeActivity activity);
}
