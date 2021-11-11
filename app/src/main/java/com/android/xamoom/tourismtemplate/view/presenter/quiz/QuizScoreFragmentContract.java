package com.android.xamoom.tourismtemplate.view.presenter.quiz;

import android.content.Context;

import com.android.xamoom.tourismtemplate.models.QuizScoreModel;

import java.util.ArrayList;

public interface QuizScoreFragmentContract {

    interface View {
        void loadQuizPage();

        void onGetQuizScoreData(ArrayList<QuizScoreModel> quizScoreModels, ArrayList<Integer> quizScoreViewTypes);

        void setupRecyclerView(ArrayList<QuizScoreModel> quizScoreModels, ArrayList<Integer> quizScoreViewTypes);

        void setupToolbar();
    }

    interface Presenter {
        void getQuizScoreData(Context context);
    }
}
