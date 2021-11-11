package com.android.xamoom.tourismtemplate.utils.Analytics

import android.util.Log

class DebugAnalyticsSender: AnalyticsSender {
  val TAG = "DebugAnalyticsSender"

  override fun reportError(name: String, domain: String, description: String, code: Int) {
    Log.v(TAG, "[Error]" +
            "\n name: " + name +
            "\n domain: " + domain +
            "\n description: " + description +
            "\n code: " + code)
  }

  override fun reportContentView(name: String, contentType: String, id: String, customAttributes: HashMap<String, String>?) {
    Log.v(TAG, "[Content View]" +
            "\n name: " + name +
            "\n contentType: " + contentType +
            "\n id: " + id +
            "\n customAttributes: " + customAttributes)
  }

  override fun reportCustomEvent(name: String, action: String?, description: String?, code: Int?) {
    Log.v(TAG, "[Custom Event]" +
            "\n name: " + name +
            "\n action: " + action +
            "\n description: " + description +
            "\n code: " + code)
  }

}