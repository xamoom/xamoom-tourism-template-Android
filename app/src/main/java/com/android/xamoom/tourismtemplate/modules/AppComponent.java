package com.android.xamoom.tourismtemplate.modules;

import com.android.xamoom.tourismtemplate.ContentActivity;
import com.android.xamoom.tourismtemplate.MainApp;
import com.android.xamoom.tourismtemplate.QuizScoreActivity;
import com.android.xamoom.tourismtemplate.QuizzesActivity;
import com.android.xamoom.tourismtemplate.fragments.ScannerFragment;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.view.HorizontalContentSliderViewHolder;
import com.android.xamoom.tourismtemplate.view.quiz.GuideSliderViewHolder;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
  void inject(MainApp mainApp);
  void inject(ApiUtil apiUtil);
  void inject(ScannerFragment scannerFragment);
  void inject(HorizontalContentSliderViewHolder horizontalContentSliderViewHolder);
  void inject(GuideSliderViewHolder guideSliderViewHolder);
  void inject(QuizScoreActivity quizScoreActivity);
  void inject(QuizzesActivity quizzesActivity);
  void inject(ContentActivity contentActivity);
}
