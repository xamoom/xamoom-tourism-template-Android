package com.android.xamoom.tourismtemplate.utils;

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.ImageView
import com.bumptech.glide.BitmapRequestBuilder
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import java.util.*


fun BitmapRequestBuilder<String, Bitmap>.intoSimpleTargetScaled(imageView: ImageView) {
    this.into(object : SimpleTarget<Bitmap>(){
        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
            val size = sizeOf(resource!!)
            if (size > 2000000) {
                imageView.setImageBitmap(getResizedBitmap(resource, 1000))
            } else imageView.setImageBitmap(resource)
        }
    })
}

fun sizeOf(data: Bitmap): Int {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
        data.rowBytes * data.height
    } else {
        data.byteCount
    }
}

fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
    var width = image.width
    var height = image.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(image, width, height, true)
}

fun updateLocalizations(context: Context, language: String?) {
    val res = context.resources
    val dm = res.displayMetrics
    val conf = res.configuration
    if (language != null && ArrayList(listOf("de", "fr", "it", "nl", "sk", "sl", "tr", "en")).contains(language)) conf.setLocale(
        Locale(language)
    ) else conf.setLocale(Locale(Locale.getDefault().language))
    res.updateConfiguration(conf, dm)
}