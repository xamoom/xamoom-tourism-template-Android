package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.FilterFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, FilterFragmentModul.class})
public interface FilterFragmentComponent {
  void inject(FilterFragment filterFragment);
}
