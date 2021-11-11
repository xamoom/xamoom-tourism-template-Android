package com.android.xamoom.tourismtemplate.fragments.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.QuizzesActivity
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.models.QuizScoreModel
import com.android.xamoom.tourismtemplate.modules.DaggerQuizScoreFragmentComponent
import com.android.xamoom.tourismtemplate.modules.QuizScoreFragmentModule
import com.android.xamoom.tourismtemplate.utils.AppgenHelper
import com.android.xamoom.tourismtemplate.view.adapter.quiz.QuizScoreAdapter
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizScoreFragmentContract
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizScoreFragmentPresenter
import com.android.xamoom.tourismtemplate.view.quiz.QuizLinkViewHolder
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mapbox.mapboxsdk.Mapbox
import java.util.ArrayList
import javax.inject.Inject

class QuizScoreFragment : Fragment(), QuizScoreFragmentContract.View, QuizLinkViewHolder.QuizScoreLinkListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.score_toolbar)
    lateinit var toolbar: Toolbar

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quiz_score_collapsingToolbarLayout)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.score_header_image)
    lateinit var headerImageView: ImageView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.score_recycler_view)
    lateinit var scoreRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: QuizScoreFragmentPresenter

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerQuizScoreFragmentComponent
            .builder()
            .quizScoreFragmentModule(QuizScoreFragmentModule(this, this.activity))
            .build()
            .inject(this)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_score, container, false)
        ButterKnife.bind(this, view)
        setupToolbar()
        loadQuizPage()
        return view
    }


    override fun loadQuizPage() {
        presenter.getQuizScoreData(activity?.applicationContext)
    }

    override fun onGetQuizScoreData(quizScoreModels: ArrayList<QuizScoreModel>?, quizScoreViewTypes: ArrayList<Int>?) {
        setupRecyclerView(quizScoreModels = quizScoreModels, quizScoreViewTypes = quizScoreViewTypes)
    }

    override fun setupRecyclerView(quizScoreModels: ArrayList<QuizScoreModel>?, quizScoreViewTypes: ArrayList<Int>?) {
        if (quizScoreModels != null && quizScoreViewTypes != null) {
            scoreRecyclerView.adapter = QuizScoreAdapter(this, quizScoreViewTypes, quizScoreModels)
            scoreRecyclerView.layoutManager = LinearLayoutManager(context)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun setupToolbar() {
        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setTitle(R.string.guide_score_screen)
        toolbar.setTitleTextColor(AppgenHelper.getInstance(context).accentColor)
        if (this.getString(R.string.is_background_image) == "true") {
            if (this.activity != null) collapsingToolbarLayout.contentScrim = this.activity?.getDrawable(R.drawable.background_image)
        } else {
            if (this.activity != null) collapsingToolbarLayout.contentScrim = this.activity?.getDrawable(R.color.color_primary)
        }
        collapsingToolbarLayout.setCollapsedTitleTextColor(AppgenHelper.getInstance(context).accentColor)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQuizScoreLinkClick() {
        startActivity(Intent(Mapbox.getApplicationContext(), QuizzesActivity::class.java))
    }
}