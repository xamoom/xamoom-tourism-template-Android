package com.android.xamoom.tourismtemplate.modules;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizScoreFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizScoreFragmentPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class QuizScoreFragmentModule {

    private QuizScoreFragmentContract.View view;
    private Activity activity;


    public QuizScoreFragmentModule(QuizScoreFragmentContract.View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    @Provides
    @Singleton
    public QuizScoreFragmentPresenter providePresenter() {
        return new QuizScoreFragmentPresenter(view, activity);
    }

    @Provides
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
    }
}
