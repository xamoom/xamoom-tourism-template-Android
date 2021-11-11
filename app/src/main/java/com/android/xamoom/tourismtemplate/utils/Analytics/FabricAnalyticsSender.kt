package com.android.xamoom.tourismtemplate.utils.Analytics

import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle

class FabricAnalyticsSender (firebaseAnalytics: FirebaseAnalytics) : AnalyticsSender  {
  private val mFirebaseAnalytics: FirebaseAnalytics = firebaseAnalytics

  override fun reportError(name: String, domain: String, description: String, code: Int) {
    val event = Bundle()
    event.putString("Error Domain", domain)
    event.putString("Error Description", description)
    event.putInt("Error Code", code)
    mFirebaseAnalytics.logEvent(name, event)
  }

  override fun reportContentView(name: String, contentType: String, id: String,
                                 customAttributes: HashMap<String, String>?) {
    val event = Bundle()
    event.putString("item_name", name)
    event.putString("item_id", id)
    event.putString("item_category", contentType)

    if (customAttributes != null) {
      for ((k, v) in customAttributes) {
        event.putString(k, v)
      }
    }
    mFirebaseAnalytics.logEvent("view_item", event)
  }

  override fun reportCustomEvent(name: String, action: String?, description: String?, code: Int?) {
    val event = Bundle()
    event.putString("Action", action)
    event.putString("Description", description)
    code?.let {event.putInt("Code", it)}
    mFirebaseAnalytics.logEvent(name, event)
  }
}