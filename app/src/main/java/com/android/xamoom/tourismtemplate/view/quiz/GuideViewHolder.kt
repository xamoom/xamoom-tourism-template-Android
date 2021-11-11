package com.android.xamoom.tourismtemplate.view.quiz

import android.annotation.SuppressLint
import android.graphics.Color
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.models.GuideItem
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter

class GuideViewHolder(itemView: View, listener: HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.guide_title)
    lateinit var title: TextView

    @BindView(R.id.guide_image_view)
    lateinit var iconView: ImageView


    private var pos: Int = 0

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
            listener.didClickGuide(pos)
        }
    }

    @SuppressLint("ResourceAsColor")
    fun updateViewHolder(position: Int, guide: GuideItem) {
        this.pos = position
        title.text = guide.title
        if (guide.imageResource != null) {
            iconView.setImageResource(guide.imageResource)
        }
    }
}