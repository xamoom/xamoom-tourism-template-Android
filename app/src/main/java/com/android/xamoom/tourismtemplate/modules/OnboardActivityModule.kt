package com.android.xamoom.tourismtemplate.modules

import android.app.Activity

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import com.android.xamoom.tourismtemplate.view.presenter.OnboardScreenContract
import com.android.xamoom.tourismtemplate.view.presenter.OnboardScreenPresenter

@Module
class OnboardActivityModule(private val view: OnboardScreenContract.View, private val activity: Activity) {

    @Provides
    @Singleton
    fun providePresenter(): OnboardScreenPresenter {
        return OnboardScreenPresenter(view, activity)
    }
}