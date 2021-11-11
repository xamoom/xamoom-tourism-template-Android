package com.android.xamoom.tourismtemplate.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.xamoom.tourismtemplate.view.presenter.SettingsFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.SettingsFragmentPresenter;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes= {AppModule.class})
public class SettingsFragmentModul {
  private SettingsFragmentContract.View view;
  private Context context;
  public SettingsFragmentModul(SettingsFragmentContract.View view, Context context) {
    this.view = view;
    this.context = context;
  }

  @Provides
  public SettingsFragmentContract.View provideView() {
    return view;
  };

  @Provides
  @Singleton
  public SettingsFragmentPresenter providePresenter(SharedPreferences sharedPreferences) {
    return new SettingsFragmentPresenter(view, sharedPreferences, context);
  }

  @Provides
  @Singleton
  public SplitInstallManager provideSplitInstallManager() {
    return SplitInstallManagerFactory.create(context);
  }
}
