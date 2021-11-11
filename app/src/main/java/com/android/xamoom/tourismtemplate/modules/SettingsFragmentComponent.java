package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, SettingsFragmentModul.class})
public interface SettingsFragmentComponent {
  void inject(SettingsFragment settingsFragment);
}
