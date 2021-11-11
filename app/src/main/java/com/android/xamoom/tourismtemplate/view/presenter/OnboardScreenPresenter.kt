package com.android.xamoom.tourismtemplate.view.presenter

import android.app.Activity
import android.text.TextUtils

import com.xamoom.android.xamoomsdk.Resource.Content
import com.xamoom.android.xamoomsdk.Resource.ContentBlock

import java.lang.ref.WeakReference
import java.util.ArrayList

import retrofit2.Call

import com.android.xamoom.tourismtemplate.utils.ApiUtil

class OnboardScreenPresenter(view:OnboardScreenContract.View, private val activity:Activity):OnboardScreenContract.Presenter {

    private val view:WeakReference<OnboardScreenContract.View> = WeakReference(view)
    private var currentCall:Call<*>? = null

    override fun onStop() {
        if (currentCall != null)
        {
            ApiUtil.getInstance().cancelCall(currentCall)
            currentCall = null
        }
    }

    override fun downloadContentListByTag(tag: String) {
        val tags = ArrayList<String>(1)
        tags.add(tag)
        ApiUtil.getInstance().loadContents(tags, null, true) { result, cursor, hasMore ->
            if (result.size <= 0) {
                if(view.get() != null) {
                    view.get()!!.showNothingFound()
                }
            }
            if(view.get() != null)
                view.get()!!.didLoadContent(result)
        }
    }


    private fun showContentImage(content:Content) {
        if (content.publicImageUrl != null)
        {
            //view.get()!!.gotContentImage(content.publicImageUrl)
        }
        else
        {
            view.get()!!.showPlaceholderImage()
        }
    }

    private fun showContent(content:Content?) {
        var content = content
        showContentImage(content!!)
        if (content!!.contentBlocks.size == 0 || content!!.contentBlocks[0].blockType != -1)
        {
            content = addContentTitle(content!!)
        }
        //view.get()!!.didLoadContent(content)
    }

    private fun addContentTitle(content:Content):Content {
        val contentBlock = ContentBlock()
        contentBlock.blockType = -1
        contentBlock.title = content.title
        if (!TextUtils.isEmpty(content.description))
        {
            contentBlock.text = content.description
        }

        content.contentBlocks.add(0, contentBlock)
        return content
    }
}