package com.android.xamoom.tourismtemplate.view.presenter.quiz

import com.xamoom.android.xamoomsdk.Resource.Content

interface QuizzesPageContract {

    interface View {
        fun loadQuizzes(isQuizzesSolved: Boolean)
        fun onLoadedQuizzes(quizzes: ArrayList<Content>)
        fun setupRecyclerView()
        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun loadQuizzesContent(isQuizzesSolved: Boolean, cursor: String?)
        fun filterSolvedQuizzes(contents: ArrayList<Content>, isQuizzesSolved: Boolean): ArrayList<Content>
    }
}