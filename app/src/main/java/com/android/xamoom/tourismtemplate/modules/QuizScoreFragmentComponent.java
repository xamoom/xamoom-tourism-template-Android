package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.fragments.quiz.QuizScoreFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {QuizScoreFragmentModule.class})
public interface QuizScoreFragmentComponent {
    void inject(QuizScoreFragment scoreFragment);
}
