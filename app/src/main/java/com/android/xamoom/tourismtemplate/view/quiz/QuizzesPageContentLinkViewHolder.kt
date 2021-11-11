package com.android.xamoom.tourismtemplate.view.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.utils.getResizedBitmap
import com.android.xamoom.tourismtemplate.utils.sizeOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.xamoom.android.xamoomsdk.Resource.Content

class QuizzesPageContentLinkViewHolder(private val listener: QuizPageContentLinkListener, itemView: View) : RecyclerView.ViewHolder(itemView) {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_viewholder_root_layout)
    lateinit var rootLayout: RelativeLayout


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_page_image)
    lateinit var quizzesPageImage: ImageView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_page_title)
    lateinit var quizzesPageTitle: TextView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_page_description)
    lateinit var quizzesPageDescription: TextView

    private var context: Context = itemView.context


    init {
        ButterKnife.bind(this, itemView)
    }


    fun updateQuizzesContentLink(content: Content) {
        quizzesPageTitle.text = content.title
        quizzesPageDescription.text = content.description
        setupQuizzesImage(content.publicImageUrl)
        rootLayout.setOnClickListener { listener.onContentClick(content) }
    }

    private fun setupQuizzesImage(imageUrl: String?) {
        if(imageUrl != null) {
            if (imageUrl.endsWith(".gif")) {
                Glide.with(context)
                    .load(imageUrl)
                    .asGif()
                    .dontTransform()
                    .placeholder(R.drawable.placeholder)
                    .dontAnimate()
                    .into(quizzesPageImage)
            } else {
                Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .dontTransform()
                    .placeholder(R.drawable.placeholder)
                    .dontAnimate()
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap?>?) {
                            if (resource != null) {
                                val size = sizeOf(resource)
                                if (size > 2000000) {
                                    quizzesPageImage.setImageBitmap(getResizedBitmap(resource, 1000))
                                } else {
                                    quizzesPageImage.setImageBitmap(resource)
                                }
                            }

                        }
                    })
            }
        } else {
            Glide.with(context)
                .load(R.mipmap.ic_launcher)
                .dontTransform()
                .placeholder(R.drawable.placeholder)
                .dontAnimate()
                .into(quizzesPageImage)
        }

    }


    interface QuizPageContentLinkListener {
        fun onContentClick(content: Content)
    }

}