package com.android.xamoom.tourismtemplate.modules;

import android.app.Activity;

import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizzesPageContract;
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizzesPagePresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class QuizzesPageFragmentModule {

    private QuizzesPageContract.View view;
    private Activity activity;


    public QuizzesPageFragmentModule(QuizzesPageContract.View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    @Provides
    @Singleton
    public QuizzesPagePresenter providePresenter() {
        return new QuizzesPagePresenter(view, activity);
    }
}
