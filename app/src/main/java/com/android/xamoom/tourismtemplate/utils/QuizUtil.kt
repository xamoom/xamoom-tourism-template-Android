package com.android.xamoom.tourismtemplate.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xamoom.android.xamoomsdk.Resource.Content
import java.util.*

class QuizUtil(context: Context) {

    private val QUIZZES = "quizzes"
    private val POINTS = "points"
    private val VOUCHERS = "vouchers"
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    private fun getSubmittedQuizzes(): ArrayList<Quiz> {
        val gson = Gson()
        val quizzesJson = prefs.getString(QUIZZES, null)
        val type = object : TypeToken<ArrayList<Quiz>>() {
        }.type


        return gson.fromJson(quizzesJson, type) ?: arrayListOf()
    }

    fun saveSubmittedQuiz(quizData: Quiz) {
        val gson = Gson()
        val quizzes = getSubmittedQuizzes()
        quizzes.add(quizData)
        val quizzesJson = gson.toJson(quizzes)
        prefs.edit().putString(QUIZZES, quizzesJson).apply()
    }


    fun isQuizSubmitted(pageId: String): Boolean {
        for (quiz in getSubmittedQuizzes()) {
            if (quiz.pageId == pageId) return true
        }
        return false
    }

    fun getQuizPoints(): Int {
        return prefs.getInt(POINTS, 0)
    }

    fun getQuizLevel(): Int {
        return (getQuizPoints() / 300) + 1
    }

    fun getQuizVouchers(): Int {
        return prefs.getInt(VOUCHERS, 0)
    }

    fun increaseQuizPoints(scoredPoints: Int) {
        prefs.edit().putInt(POINTS, getQuizPoints() + scoredPoints).apply()
    }

    fun increaseVoucherAmount(scoredPoints: Int) {
        prefs.edit()
            .putInt(VOUCHERS, getQuizVouchers() + getIncreaseVoucherAmount(getQuizPoints(), getQuizPoints() - scoredPoints))
            .apply()

    }

    fun decreaseVoucherAmount(vouchersAmount: Int) {
        val totalVouchers = getQuizVouchers()
        if (totalVouchers > vouchersAmount) {
            prefs.edit()
                .putInt(VOUCHERS, getQuizVouchers() - vouchersAmount)
                .apply()
        }
    }


    private fun getIncreaseVoucherAmount(currentPoints: Int, previousPoints: Int): Int {
        if ((currentPoints / 300) > (previousPoints / 300)) {
            return 1
        }
        return 0

    }

    fun filterSolvedQuizzes(contents: ArrayList<Content>, isSolved: Boolean): ArrayList<Content> {
        var solvedQuizzes = arrayListOf<Content>()
        var openQuizzes = arrayListOf<Content>()
        for (quiz in contents) {
            val quizId = quiz.id
            if (quizId != null) {
                if (isQuizSubmitted(quizId)) {
                    solvedQuizzes.add(quiz)
                } else {
                    openQuizzes.add(quiz)
                }
            }
        }
        return if (isSolved) solvedQuizzes else openQuizzes
    }


    data class Quiz(
        val pageId: String,
        val submittedDate: Date
    )
}
