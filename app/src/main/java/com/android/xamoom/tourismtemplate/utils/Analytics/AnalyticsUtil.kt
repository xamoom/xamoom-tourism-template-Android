package com.android.xamoom.tourismtemplate.utils.Analytics

interface AnalyticsSender {
  fun reportError(name: String, domain: String, description: String, code: Int)
  fun reportContentView(name: String, contentType: String, id: String, customAttributes: HashMap<String, String>?)
  fun reportCustomEvent(name: String, action: String?, description: String?, code: Int?)
}

class AnalyticsUtil {

  companion object {
    @JvmStatic val errorName = "Error"

    @JvmStatic var analyticsSender: ArrayList<AnalyticsSender> = ArrayList()

    fun reportError(type: String, error: Error) {
      for (sender in analyticsSender) {
        sender.reportError(errorName, type, error.localizedMessage, -1)
      }
    }

    fun reportContentView(name: String, type: String, id: String,
                          customAttributs: HashMap<String, String>?) {
      for (sender in analyticsSender) {
        sender.reportContentView(name, type, id, customAttributs)
      }
    }

    fun reportCustomEvent(name: String, action: String?, description: String?, code: Int?) {
      for (sender in analyticsSender) {
        sender.reportCustomEvent(name, action, description, code)
      }
    }

    fun registerSender(sender: AnalyticsSender) {
      analyticsSender.add(sender)
    }
  }
}
