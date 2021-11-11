package com.android.xamoom.tourismtemplate

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.xamoom.tourismtemplate.fragments.quiz.QuizScoreFragment
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender
import com.google.android.gms.analytics.Tracker
import java.util.*
import javax.inject.Inject

class QuizScoreActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var mTracker: Tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.app().appComponent.inject(this)
        GoogleAnalyticsSender(mTracker).reportContentView("Android Quiz Score screen", "", "", null)
        setContentView(R.layout.activity_quiz_score)
        updateLocalization()
        showQuizScoreFragment()
    }

    override fun onResume() {
        super.onResume()
        updateLocalization()
    }

    private fun showQuizScoreFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.score_frame_layout, QuizScoreFragment())
            .commit()
    }

    private fun updateLocalization() {
        val language = sharedPreferences.getString("current_language_code", null)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (language != null && listOf("de", "fr", "it", "nl", "sk", "sl", "tr", "en").contains(language)){
            conf.setLocale(Locale(language))
        }
        else {
            conf.setLocale(Locale(Locale.getDefault().language))
        }
        res.updateConfiguration(conf, dm)
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

}