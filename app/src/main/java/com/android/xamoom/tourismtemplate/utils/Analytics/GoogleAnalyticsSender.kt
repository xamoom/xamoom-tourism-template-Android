package com.android.xamoom.tourismtemplate.utils.Analytics

import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

class GoogleAnalyticsSender(var tracker: Tracker) : AnalyticsSender {

    override fun reportError(name: String, domain: String, description: String, code: Int) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory(name)
                .setAction(description)
                .setLabel(description)
                .setValue(code.toLong()).build())
    }

    override fun reportContentView(name: String, contentType: String, id: String,
                                   customAttributes: HashMap<String, String>?) {
        tracker.setScreenName(name)
        tracker.send(HitBuilders.ScreenViewBuilder().build())
    }

    override fun reportCustomEvent(name: String, action: String?, description: String?, code: Int?) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory(name)
                .setAction(description)
                .setLabel(description)
                .setValue(code?.toLong() ?: -1).build())
    }
}