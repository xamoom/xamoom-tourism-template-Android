package com.android.xamoom.tourismtemplate.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.Globals
import com.android.xamoom.tourismtemplate.HomeActivity
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.modules.AppModule
import com.android.xamoom.tourismtemplate.modules.DaggerOnboardActivityComponent
import com.android.xamoom.tourismtemplate.modules.OnboardActivityModule
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender
import com.android.xamoom.tourismtemplate.utils.intoSimpleTargetScaled
import com.android.xamoom.tourismtemplate.view.presenter.OnboardScreenContract
import com.android.xamoom.tourismtemplate.view.presenter.OnboardScreenPresenter
import com.bumptech.glide.Glide
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.xamoom.android.xamoomsdk.Helpers.GeofenceBroadcastReceiver
import com.xamoom.android.xamoomsdk.PushDevice.PushDeviceUtil
import com.xamoom.android.xamoomsdk.Resource.Content
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class OnboardFragment : Fragment(), OnboardScreenContract.View {

    @BindView(R.id.coordinater)
    lateinit var coordinatorLayout: CoordinatorLayout
    @BindView(R.id.onboard_content_layout)
    lateinit var onboardContentLayout: ConstraintLayout
    @BindView(R.id.nothing_found_view)
    lateinit var nothingFoundView: View
    @BindView(R.id.content_progress_bar)
    lateinit var progressBar: ProgressBar
    @BindView(R.id.onboard_action_button)
    lateinit var actionButton: LinearLayout
    @BindView(R.id.skip_button)
    lateinit var skipButton: LinearLayout
    @BindView(R.id.action_button_text)
    lateinit var actionButtonText: TextView
    @BindView(R.id.skip_button_text)
    lateinit var skipButtonText: TextView
    @BindView(R.id.dots_panel)
    lateinit var dotsPanel: LinearLayout
    @BindView(R.id.onboard_title)
    lateinit var onboardScreenTitle: TextView
    @BindView(R.id.onboard_description)
    lateinit var onboardScreenDescription: TextView
    @BindView(R.id.onboard_image)
    lateinit var onboardScreenImage: ImageView
    @BindView(R.id.skip_image)
    lateinit var skipImageView: ImageView

    @Inject
    lateinit var presenter: OnboardScreenPresenter
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var mTracker: Tracker

    private val contentList = ArrayList<Content>()
    private var currentScreenIndex = 0
    private var listener: OnboardFragmentListener? = null
    private var dots: Array<ImageView>? = null
    private var systemLanguage: String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AnalyticsUtil.reportContentView("Content", Globals.ANALYTICS_CONTENT_TYPE_SCREEN, Globals.ONBOARDING_TAG, null)

        DaggerOnboardActivityComponent
                .builder()
                .onboardActivityModule(OnboardActivityModule(this, this.activity as Activity))
                .appModule(AppModule(context))
                .build()
                .inject(this)
        GoogleAnalyticsSender(mTracker).reportContentView("Android Onboarding screen", "", "", null)

        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboard, container, false)

        ButterKnife.bind(this, view)
        initLocalization()
        if(this.context?.getString(R.string.is_background_image) == "true") {
            coordinatorLayout.background = this.resources.getDrawable(R.drawable.background_image)
            onboardContentLayout.background = this.resources.getDrawable(R.drawable.background_image)
        } else {
            coordinatorLayout.setBackgroundColor(this.resources.getColor(R.color.color_primary))
            onboardContentLayout.setBackgroundColor(this.resources.getColor(R.color.color_primary))
        }


        if(this.context?.getColor(R.color.color_text).toString() == Color.BLACK.toString())
            skipImageView.setImageDrawable(this.context?.getDrawable(R.drawable.ic_arrow_black_right_24))
        else skipImageView.setImageDrawable(this.context?.getDrawable(R.drawable.ic_play_arrow_white_24dp))

        skipButton.setOnClickListener { skipOnboarding() }
        actionButton.setOnClickListener { actionButtonClicked() }

        presenter.downloadContentListByTag(Globals.ONBOARDING_TAG)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            listener!!.finishActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnboardFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ContentFragmentListener")
        }
    }

    override fun onStop() {
        super.onStop()

        presenter!!.onStop()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun didLoadContent(contents: List<Content>) {
        contentList.addAll(contents.filter {c -> c.customMeta[ANDROID_IGNORE_META] === null && c.customMeta[PERMISSION_NOTIFICATION_META] === null})
        if (contentList.isEmpty()) finishOnboarding()
        hideLoading()
        initDots(contentList.size)
        showScreenAtPosition(0)
    }

    override fun showPlaceholderImage() {
        //headerImageView!!.setImageDrawable(resources.getDrawable(R.drawable.placeholder))
    }

    override fun showNothingFound() {
        nothingFoundView!!.visibility = View.VISIBLE
    }

    override fun showLoading() {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar!!.visibility = View.GONE
        actionButton.visibility = View.VISIBLE
    }

    private fun initLocalization() {
        systemLanguage = Locale.getDefault().language
    }

    private fun initDots(dotsCount: Int) {
        dots = Array<ImageView>(dotsCount) { i -> ImageView(context) }
        for( i in 0 until dotsCount) {
            dots?.get(i)?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.non_active_dot))
            dots?.get(i)?.setColorFilter(ContextCompat.getColor(context!!, R.color.color_slider_pageindicator_color_selected))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(10, 0, 10, 0)
            dotsPanel.addView(dots?.get(i), params)
        }
    }

    private fun showScreenAtPosition(position: Int) {

        if (position > contentList.size - 1) return
        val content = contentList[position]

        if (position == contentList.size - 1) {
            skipButton.visibility = View.GONE
        }

        if (position == 0) {
            skipButtonText.text = content.customMeta["skip-label-$systemLanguage"] ?: context?.getString(R.string.onboard_skip_label)
        }

        for( i in 0 until contentList.size) {
            dots?.get(i)?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.non_active_dot))
        }
        dots?.get(position)?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.active_dot))

        onboardScreenTitle.text = content.title
        onboardScreenDescription.text = if (content.description.length < 260) content.description else content.description.substring(0, 260) + "..."
        actionButtonText.text = getCurrentActionButtonText()

        val imageUrl = content.publicImageUrl
        if(imageUrl != null && imageUrl.endsWith(".gif")) {
            Glide.with(context)
                    .load(content.publicImageUrl)
                    .asGif()
                    .dontTransform() 
                    .placeholder(R.drawable.placeholder)
                    .dontAnimate()
                    .into(onboardScreenImage)
        } else {
            Glide.with(context)
                    .load(content.publicImageUrl)
                    .asBitmap()
                    .placeholder(R.drawable.placeholder)
                    .dontAnimate()
                    .dontTransform()
                    .intoSimpleTargetScaled(onboardScreenImage)
        }

    }

    private fun getCurrentActionButtonText(): String {

        if (contentList[currentScreenIndex].customMeta["permission-location"] != null) {
            return contentList[currentScreenIndex].customMeta["allow-label-$systemLanguage"] ?: context?.getString(R.string.onboard_action_location_permission) ?: ""
        }
        if (currentScreenIndex == contentList.size - 1) {
            return contentList[currentScreenIndex].customMeta["end-label-$systemLanguage"] ?: context?.getString(R.string.onboard_action_finish) ?: ""
        }
        return contentList[currentScreenIndex].customMeta["more-label-$systemLanguage"] ?: context?.getString(R.string.onboard_action_start) ?: ""
    }

    fun actionButtonClicked() {

        if (currentScreenIndex == contentList.size - 1) {
            finishOnboarding()
            return
        }

        if (contentList[currentScreenIndex].customMeta["permission-location"] != null) {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission()
                return
            }
        }

        currentScreenIndex++
        showScreenAtPosition(currentScreenIndex);
    }

    private fun skipOnboarding() {
        currentScreenIndex = contentList.size - 1;
        showScreenAtPosition(currentScreenIndex)
    }

    private fun finishOnboarding() {
        val sharedPref = context?.getSharedPreferences(PushDeviceUtil.PREFES_NAME, Context.MODE_PRIVATE)
        with(sharedPref!!.edit()) {
            putBoolean(Globals.ONBOARDING_TAG, true)
            apply()
        } // check sharedPref!!.edit() line
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                currentScreenIndex++;
                showScreenAtPosition(currentScreenIndex);
                locationLogic()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            locationLogic()
            return
        }

        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun locationLogic() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = LOCATION_INTERVAL
        mLocationRequest.fastestInterval = LOCATION_INTERVAL
        mLocationRequest.smallestDisplacement = 100F
        mLocationRequest.maxWaitTime = WAIT_INTERVAL

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = context?.packageName
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        context?.let { LocationServices.getFusedLocationProviderClient(it).requestLocationUpdates(mLocationRequest, pendingIntent) }
    }

    interface OnboardFragmentListener {
        fun finishActivity()
    }

    companion object {
        val PERMISSION_NOTIFICATION_META = "permission-notification"
        val ANDROID_IGNORE_META = "android-ignore"
        val REQUEST_LOCATION = 0
        private val LOCATION_INTERVAL: Long = 1000 * 60 * 15
        private val WAIT_INTERVAL: Long = 1000 * 60 * 30

        fun newInstance(): OnboardFragment {
            val fragment = OnboardFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor