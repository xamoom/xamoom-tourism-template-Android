package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by raphaelseher on 15/05/2017.
 */

@Singleton
@Component(modules = {AppModule.class, HomeFragmentModul.class})
public interface HomeFragmentComponent {
  void inject(HomeFragment fragment);
}
