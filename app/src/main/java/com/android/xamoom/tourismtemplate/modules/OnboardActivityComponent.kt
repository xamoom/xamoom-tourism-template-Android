package com.android.xamoom.tourismtemplate.modules


import javax.inject.Singleton

import dagger.Component
import com.android.xamoom.tourismtemplate.fragments.OnboardFragment

@Singleton
@Component(modules = [AppModule::class, OnboardActivityModule::class])
interface OnboardActivityComponent {
    fun inject(onboardFragment: OnboardFragment)
}