package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.ContentFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ContentActivityModul.class})
public interface ContentActivityComponent {
  void inject(ContentFragment contentFragment);
}
