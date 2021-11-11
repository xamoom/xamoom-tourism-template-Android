package com.android.xamoom.tourismtemplate.modules;

import android.app.Activity;

import com.android.xamoom.tourismtemplate.view.presenter.ContentScreenContract;
import com.android.xamoom.tourismtemplate.view.presenter.ContentScreenPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContentActivityModul {

  private ContentScreenContract.View view;
  private Activity activity;

  public ContentActivityModul(ContentScreenContract.View view, Activity activity) {
    this.view = view;
    this.activity = activity;
  }

  @Provides
  @Singleton
  public ContentScreenPresenter providePresenter() {
    return new ContentScreenPresenter(view, activity);
  }
}
