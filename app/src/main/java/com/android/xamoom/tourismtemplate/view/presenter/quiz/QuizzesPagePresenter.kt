package com.android.xamoom.tourismtemplate.view.presenter.quiz

import android.app.Activity
import com.android.xamoom.tourismtemplate.Globals
import com.android.xamoom.tourismtemplate.utils.ApiCallback.ListCallback
import com.android.xamoom.tourismtemplate.utils.ApiUtil
import com.android.xamoom.tourismtemplate.utils.QuizUtil
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizzesPageContract
import com.xamoom.android.xamoomsdk.Resource.Content
import java.util.*
import kotlin.collections.ArrayList

class QuizzesPagePresenter(val view: QuizzesPageContract.View, val activity: Activity) : QuizzesPageContract.Presenter {

    private var quizUtil: QuizUtil = QuizUtil(activity.applicationContext)
    private var downloadedQuizzes: ArrayList<Content> = ArrayList()


    override fun loadQuizzesContent(isQuizzesSolved: Boolean, cursor: String?) {
        view.showProgressBar()
        val tags = arrayListOf(Globals.QUIZ_TAG, Globals.QUIZ_TAG.toUpperCase(Locale.ROOT))
        ApiUtil.getInstance()
            .loadContents(tags, cursor, true
            ) { result, currentCursor, hasMore ->
                if (result != null) {
                    downloadedQuizzes.addAll(result)
                }
                if (hasMore) {
                    loadQuizzesContent(isQuizzesSolved, currentCursor)
                } else {
                    view.hideProgressBar()
                    view.onLoadedQuizzes(filterSolvedQuizzes(downloadedQuizzes, isQuizzesSolved))
                    downloadedQuizzes = ArrayList<Content>()
                }
            }
    }

    override fun filterSolvedQuizzes(contents: ArrayList<Content>, isQuizzesSolved: Boolean): ArrayList<Content> {
        return quizUtil.filterSolvedQuizzes(contents, isQuizzesSolved)
    }

}