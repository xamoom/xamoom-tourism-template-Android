package com.android.xamoom.tourismtemplate.view.presenter.quiz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.models.QuizScoreModel;
import com.android.xamoom.tourismtemplate.models.QuizScoreType;
import com.android.xamoom.tourismtemplate.utils.ExtensionsKt;
import com.android.xamoom.tourismtemplate.utils.QuizUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class QuizScoreFragmentPresenter implements QuizScoreFragmentContract.Presenter {

    private static final Integer VIEW_TYPE_QUIZ_TEXT = 0;
    private static final Integer VIEW_TYPE_QUIZ_LINK = 1;
    private WeakReference<QuizScoreFragmentContract.View> view;
    private Activity activity;
    private QuizUtil quizUtil;

    public QuizScoreFragmentPresenter(QuizScoreFragmentContract.View view, Activity activity) {
        this.view = new WeakReference<QuizScoreFragmentContract.View>(view);
        this.activity = activity;
        this.quizUtil = new QuizUtil(activity);
    }


    @Override
    public void getQuizScoreData(Context context) {
        view.get().onGetQuizScoreData(getQuizScoreModels(context), getQuizViewTypes());
    }


    @SuppressLint("StringFormatMatches")
    private ArrayList<QuizScoreModel> getQuizScoreModels(Context context) {
        ExtensionsKt.updateLocalizations(context, PreferenceManager.getDefaultSharedPreferences(context).getString("current_language_code", null));
        return new ArrayList<>(Arrays.asList(
                new QuizScoreModel(context.getString(R.string.quiz_score_screen_title), QuizScoreType.TITLE),
                new QuizScoreModel(context.getString(R.string.quiz_score_screen_subtitle), QuizScoreType.TEXT),
                new QuizScoreModel(context.getString(R.string.quiz_score_screen_points, getCurrentPointsAmount()), QuizScoreType.TITLE_BACKGROUND),
                new QuizScoreModel(context.getString(R.string.quiz_score_screen_level_description), QuizScoreType.TEXT),
                new QuizScoreModel(context.getString(R.string.quiz_score_screen_levels, getCurrentLevel()), QuizScoreType.TITLE_BACKGROUND),
                new QuizScoreModel(null, QuizScoreType.QUIZ_LINK)
        ));
    }

    private ArrayList<Integer> getQuizViewTypes() {
        return new ArrayList<>(Arrays.asList(
                VIEW_TYPE_QUIZ_TEXT,
                VIEW_TYPE_QUIZ_TEXT,
                VIEW_TYPE_QUIZ_TEXT,
                VIEW_TYPE_QUIZ_TEXT,
                VIEW_TYPE_QUIZ_TEXT,
                VIEW_TYPE_QUIZ_TEXT,
                VIEW_TYPE_QUIZ_LINK
        ));
    }


    private Integer getCurrentPointsAmount() {
        return quizUtil.getQuizPoints();
    }


    private Integer getCurrentLevel() {
        return quizUtil.getQuizLevel();
    }


    private Integer getCurrentVouchersAmount() {
        return quizUtil.getQuizVouchers();
    }


}

