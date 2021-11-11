package com.android.xamoom.tourismtemplate.utils.Analytics

fun AnalyticsUtil.Companion.reportError(type: String, error: at.rags.morpheus.Error) {
  for (sender in analyticsSender) {
    sender.reportError(errorName, type, error.detail, error.code.toInt())
  }
}