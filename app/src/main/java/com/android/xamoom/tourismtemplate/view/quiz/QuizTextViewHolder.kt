package com.android.xamoom.tourismtemplate.view.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.models.QuizScoreModel
import com.android.xamoom.tourismtemplate.models.QuizScoreType

class QuizTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.score_text)
    lateinit var quizText: TextView
    @BindView(R.id.score_text_layout)
    lateinit var quizTextLayout: LinearLayout
    private val context: Context = itemView.context

    init {
        ButterKnife.bind(this, itemView)
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setupTextValue(quizScore: QuizScoreModel) {
        when (quizScore.textType) {
            QuizScoreType.TEXT -> {
                quizText.textSize = 18.0f
                quizText.setTextColor(Color.BLACK)
                quizText.text = quizScore.textValue
            }
            QuizScoreType.TITLE -> {
                quizText.textSize = 24.0f
                quizText.setTextColor(Color.BLACK)
                quizText.text = quizScore.textValue
            }
            QuizScoreType.TITLE_BACKGROUND -> {
                quizText.textSize = 24.0f
                quizText.text = quizScore.textValue
                quizText.gravity = Gravity.CENTER
                quizText.setTextColor(Color.parseColor(context.getString(R.color.color_accent)))
                changeLayoutHeight(250, quizTextLayout)
                if (context.getString(R.string.is_background_image) == "true") {
                    quizTextLayout.background = context.getDrawable(R.drawable.background_image)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        quizTextLayout.setBackgroundColor(context.getColor(R.color.color_primary))
                    }
                }
            }

            else -> {
            }
        }
    }


    private fun changeLayoutHeight(height: Int, layout: LinearLayout) {
        val params = layout.layoutParams
        params.height = height
        layout.layoutParams = params
    }


}