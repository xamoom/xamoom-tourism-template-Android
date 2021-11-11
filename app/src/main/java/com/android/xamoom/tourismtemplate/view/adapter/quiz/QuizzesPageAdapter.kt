package com.android.xamoom.tourismtemplate.view.adapter.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.view.quiz.QuizzesPageContentLinkViewHolder
import com.xamoom.android.xamoomsdk.Resource.Content

class QuizzesPageAdapter(private val listener: QuizzesPageContentLinkViewHolder.QuizPageContentLinkListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var quizzesPageContents: ArrayList<Content> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return QuizzesPageContentLinkViewHolder(listener = listener, itemView =
        LayoutInflater.from(parent.context).inflate(R.layout.viewholder_quizzes_page_link, parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentContent = quizzesPageContents[position]
        if(holder is QuizzesPageContentLinkViewHolder) {
            holder.updateQuizzesContentLink(currentContent)
        }
    }

    override fun getItemCount(): Int {
        return quizzesPageContents.size
    }

    fun setQuizzesPageContents(contents: ArrayList<Content>) {
        this.quizzesPageContents.clear()
        this.quizzesPageContents.addAll(contents)
    }


}