package com.android.xamoom.tourismtemplate.view.quiz

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.Globals
import com.android.xamoom.tourismtemplate.MainApp
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.models.GuideItem
import com.android.xamoom.tourismtemplate.view.HorizontalContentItemDecoration
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter
import com.android.xamoom.tourismtemplate.view.adapter.quiz.GuideRecyclerAdapter
import java.util.ArrayList
import java.util.HashMap

class GuideSliderViewHolder(itemView: View,
                            val listener: HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener,
                            val guide: ArrayList<GuideItem>
) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.horizontal_content_recyclerview)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.title_label)
    @Nullable
    lateinit var titleTextView: TextView

    private lateinit var adapter: GuideRecyclerAdapter
    private var context: Context = itemView.context

    private var tag: String? = null
    private var config: HashMap<String, String>? = null

    init {
        ButterKnife.bind(this, itemView)
        MainApp.app().appComponent.inject(this)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = GuideRecyclerAdapter(listener, guide)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val decoration = HorizontalContentItemDecoration(context.resources.getDimensionPixelSize(R.dimen.default_margin),
            context.resources.getDimensionPixelSize(R.dimen.default_margin))
        recyclerView.addItemDecoration(decoration)
    }

    private fun updateViewHolder() {
        titleTextView.text = getLocalizedName()
        adapter.notifyDataSetChanged()
    }

    private fun getLocalizedName(): String {
        if (config == null) {
            return tag!!
        }

        if (tag.equals(Globals.GUIDE_TAG, ignoreCase = true)) {
            return context.getString(R.string.guide_list_title)
        }

        return "default title"
    }

    fun updateTitle(tag: String, config: HashMap<String, String>) {
        this.tag = tag
        this.config = config
        updateViewHolder()
    }
}