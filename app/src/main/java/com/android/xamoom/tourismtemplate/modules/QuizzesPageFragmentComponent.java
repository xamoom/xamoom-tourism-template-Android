package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.quiz.QuizzesFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {QuizzesPageFragmentModule.class})
public interface QuizzesPageFragmentComponent {
    void inject(QuizzesFragment quizzesFragment);
}
