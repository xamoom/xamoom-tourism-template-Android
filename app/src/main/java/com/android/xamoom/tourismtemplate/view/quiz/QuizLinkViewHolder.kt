package com.android.xamoom.tourismtemplate.view.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.R

class QuizLinkViewHolder(private val listener: QuizScoreLinkListener, itemView: View) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quiz_link_layout)
    lateinit var linkLayout: LinearLayout
    private val context: Context = itemView.context

    init {
        ButterKnife.bind(this, itemView)
    }


    fun updateQuizLink() {
        if (context.getString(R.string.is_background_image) == "true") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                linkLayout.background = context.getDrawable(R.drawable.background_image)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                linkLayout.setBackgroundColor(context.getColor(R.color.color_primary))
            }
        }
        linkLayout.setOnClickListener { listener.onQuizScoreLinkClick() }
    }

    interface QuizScoreLinkListener {
        fun onQuizScoreLinkClick();
    }


}