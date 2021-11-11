package com.android.xamoom.tourismtemplate.view.presenter

import com.xamoom.android.xamoomsdk.Resource.Content

interface OnboardScreenContract {

    interface View {
        fun didLoadContent(contents: List<Content>)
        //    fun gotContentImage(imageUrl: String)
        fun showPlaceholderImage()
        fun showNothingFound()
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun onStop()
        fun downloadContentListByTag(tag: String)
    }
}