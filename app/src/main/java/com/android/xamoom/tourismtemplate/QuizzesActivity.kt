package com.android.xamoom.tourismtemplate

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.fragments.quiz.QuizzesFragment
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender
import com.android.xamoom.tourismtemplate.utils.AppgenHelper
import com.android.xamoom.tourismtemplate.utils.updateLocalizations
import com.android.xamoom.tourismtemplate.view.adapter.quiz.ViewPagerAdapter
import com.google.android.gms.analytics.Tracker
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject

class QuizzesActivity : AppCompatActivity() {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_page_toolbar)
    lateinit var toolbar: Toolbar

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_page_viewpager)
    lateinit var viewPager: ViewPager

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_page_tabs)
    lateinit var tabLayout: TabLayout
    private lateinit var firstQuizzesFragment: QuizzesFragment
    private lateinit var secondQuizzesFragment: QuizzesFragment

    @Inject
    lateinit var mTracker: Tracker

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.app().appComponent.inject(this)
        GoogleAnalyticsSender(mTracker).reportContentView("Android Quizzes page screen", "", "", null)
        setContentView(R.layout.activity_quizzes)
        ButterKnife.bind(this)
        firstQuizzesFragment = QuizzesFragment(false);
        secondQuizzesFragment = QuizzesFragment(true);
        setupToolbar()
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = this.applicationContext.getString(R.string.guide_quizzes_screen)
        toolbar.setTitleTextColor(AppgenHelper.getInstance(applicationContext).accentColor)
        val tabBarTextColor = getColor(R.color.color_accent)
        tabLayout.setTabTextColors(tabBarTextColor, tabBarTextColor)
        if (this.getString(R.string.is_background_image) == "true") {
            toolbar.setBackgroundResource(R.drawable.background_image)
            tabLayout.setBackgroundResource(R.drawable.background_image)
        } else {
            toolbar.setBackgroundColor(getColor(R.color.color_primary))
            tabLayout.setBackgroundColor(getColor(R.color.color_primary))
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        updateLocalizations(this, PreferenceManager.getDefaultSharedPreferences(this).getString("current_language_code", null))
        adapter.addFragment(firstQuizzesFragment, getString(R.string.quizzes_page_open_quizzes_title))
        adapter.addFragment(secondQuizzesFragment, getString(R.string.quizzes_page_solved_quizzes_title))
        viewPager.adapter = adapter
    }

}