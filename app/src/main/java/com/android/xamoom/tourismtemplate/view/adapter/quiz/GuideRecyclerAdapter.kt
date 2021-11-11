package com.android.xamoom.tourismtemplate.view.adapter.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.models.GuideItem
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter
import com.android.xamoom.tourismtemplate.view.quiz.GuideViewHolder

class GuideRecyclerAdapter(val listener: HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener, val guides: ArrayList<GuideItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent?.context
        val view = LayoutInflater.from(context!!)
            .inflate(R.layout.viewholder_guide, parent, false)
        return GuideViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return guides.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val guideHolder = holder as GuideViewHolder
        guideHolder.updateViewHolder(position, guides[position])
    }
}