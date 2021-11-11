package com.android.xamoom.tourismtemplate.view.adapter.quiz

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.models.QuizScoreModel
import com.android.xamoom.tourismtemplate.view.quiz.QuizLinkViewHolder
import com.android.xamoom.tourismtemplate.view.quiz.QuizTextViewHolder
import java.util.ArrayList

class QuizScoreAdapter(private val listener: QuizLinkViewHolder.QuizScoreLinkListener, private var viewTypes: ArrayList<Int>, private var quizScoreContent: ArrayList<QuizScoreModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val VIEW_TYPE_QUIZ_TEXT: Int = 0
        val VIEW_TYPE_QUIZ_LINK: Int = 1
    }


    override fun getItemViewType(position: Int): Int {
        return viewTypes[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_QUIZ_TEXT -> {
                return QuizTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_quiz_score_text, parent, false))
            }
            VIEW_TYPE_QUIZ_LINK -> {
                return QuizLinkViewHolder(listener,
                    LayoutInflater.from(parent.context).inflate(R.layout.viewholder_quiz_score_link, parent, false))
            }
        }
        return QuizTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_quiz_score_text, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuizTextViewHolder) {
            holder.setupTextValue(quizScoreContent[position])
        }
        if (holder is QuizLinkViewHolder) {
            holder.updateQuizLink()
        }
    }

    override fun getItemCount(): Int {
        return viewTypes.size
    }



}