package com.android.xamoom.tourismtemplate.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.android.xamoom.tourismtemplate.HomeActivity
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.modules.AppModule
import com.android.xamoom.tourismtemplate.modules.DaggerAppComponent
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender
import com.android.xamoom.tourismtemplate.utils.ApiUtil
import com.google.android.gms.analytics.Tracker
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.xamoom.android.xamoomsdk.Helpers.ColorHelper
import com.xamoom.android.xamoomsdk.Resource.Content
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.net.URL
import javax.inject.Inject

class ScannerFragment: androidx.fragment.app.Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var qrView: ZXingScannerView
    private lateinit var listener: QrScannerListener
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var mTracker: Tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent.builder()
                .appModule(AppModule(context))
                .build()
                .inject(this)
        GoogleAnalyticsSender(mTracker).reportContentView("Android Onboarding screen", "", "", null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_scan, container, false)
        qrView = view.findViewById(R.id.qrReaderView)
        progressBar = view.findViewById(R.id.progress_bar)
        restartCamera()
        return view
    }

    override fun onResume() {
        super.onResume()
        qrView.stopCamera()
        qrView.setResultHandler(this)
        qrView.startCamera()
    }

    private fun restartCamera() {
        hideLoading()
        qrView.stopCamera()
        qrView.setResultHandler(this)
        qrView.startCamera()
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun handleResult(p0: Result?) {
        val qrResult = p0 ?: return
        var resultText = qrResult.text

        var validUrl = false
        try {
            val url = URL(resultText)
            url.toURI()
            validUrl  = true
        } catch (exc: Throwable) {

        }

        showLoading()

        if (!validUrl) {
            listener.handleOtherScanResult(resultText)
            // listener.openLocId(resultText)
//            ApiUtil.getInstance().loadContentCodeByLocationIdentifier(resultText, activity) { result, error ->
//                if (result != null) {
//                    hideLoading()
//                    listener.openContent(result)
//                } else {
//                    restartCamera()
//                    if (error != null && error.code != "92") {
//                        Toast.makeText(activity!!.applicationContext, getString(R.string.scan_fragment_qr), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
        } else {
            if (resultText.equals("https://${getString(R.string.deep_link)}") || resultText.equals("http://${getString(R.string.deep_link)}") ||
                resultText.equals("https://${getString(R.string.deep_link)}/") || resultText.equals("http://${getString(R.string.deep_link)}/")) {

                val mainActivity = activity as HomeActivity
                mainActivity.openHome()
            } else if (resultText.contains("${getString(R.string.deep_link)}/content/")) {
                val uri = Uri.parse(resultText)
                val contentId = uri.lastPathSegment
                if(contentId != null) {
                    listener.openContentId(contentId)
                }

//                ApiUtil.getInstance().loadContentCode(contentId, activity) { result, error ->
//                    if (result != null) {
//                        hideLoading()
//                        listener.openContent(result)
//                    } else {
//                        restartCamera()
//                        if (error != null && error.code != "92") {
//                            Toast.makeText(activity!!.applicationContext, getString(R.string.scan_fragment_qr), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
            } else if (resultText.contains(getString(R.string.deep_link))) {
                val uri = Uri.parse(resultText)
                val locationIdentifier = uri.lastPathSegment
                if(locationIdentifier != null) {
                    listener.openLocId(locationIdentifier)
                }

//                ApiUtil.getInstance().loadContentCodeByLocationIdentifier(locationIdentifier, activity) { result, error ->
//                    if (result != null) {
//                        hideLoading()
//                        listener.openContent(result)
//                    } else {
//                        restartCamera()
//                        if (error != null && error.code != "92") {
//                            Toast.makeText(activity!!.applicationContext, getString(R.string.scan_fragment_qr), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
            } else if (resultText.contains("xm.gl") || resultText.contains("r.xm.gl")) {
                if (resultText.contains("content/")) {
                    val uri = Uri.parse(resultText)
                    val contentId = uri.lastPathSegment
                    if(contentId != null) {
                        listener.openContentId(contentId)
                    }
//                    ApiUtil.getInstance().loadContentCode(contentId, activity) { result, error ->
//                        if (result != null) {
//                            hideLoading()
//                            listener.openContent(result)
//                        } else {
//                            restartCamera()
//                            if (error != null && error.code != "92") {
//                                Toast.makeText(activity!!.applicationContext, getString(R.string.scan_fragment_qr), Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
                } else {
                    val uri = Uri.parse(resultText)
                    val locationIdentifier = uri.lastPathSegment
                    if(locationIdentifier != null) {
                        listener.openLocId(locationIdentifier)
                    }
//                    ApiUtil.getInstance().loadContentCodeByLocationIdentifier(locationIdentifier, activity) { result, error ->
//                        if (result != null) {
//                            hideLoading()
//                            listener.openContent(result)
//                        } else {
//                            restartCamera()
//                            if (error != null && error.code != "92") {
//                                Toast.makeText(activity!!.applicationContext, getString(R.string.scan_fragment_qr), Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
                    return
                }
            } else {
                Toast.makeText(activity!!.applicationContext, getString(R.string.scan_fragment_qr), Toast.LENGTH_SHORT).show()
                restartCamera()
            }
        }

        hideLoading()
    }

    companion object {
        fun newInstance(listener: QrScannerListener): ScannerFragment {
            val fragment = ScannerFragment()
            fragment.listener = listener
            return fragment
        }
    }

    interface QrScannerListener {
        fun openContent(content: Content)
        fun openContentId(contentId: String)
        fun openLocId(locId: String)
        fun handleOtherScanResult(resultText: String)
    }
}