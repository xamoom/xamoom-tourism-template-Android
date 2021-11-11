package com.android.xamoom.tourismtemplate.fragments.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.ContentActivity
import com.android.xamoom.tourismtemplate.R
import com.android.xamoom.tourismtemplate.modules.DaggerQuizzesPageFragmentComponent
import com.android.xamoom.tourismtemplate.modules.QuizzesPageFragmentModule
import com.android.xamoom.tourismtemplate.view.adapter.quiz.QuizzesPageAdapter
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizzesPageContract
import com.android.xamoom.tourismtemplate.view.presenter.quiz.QuizzesPagePresenter
import com.android.xamoom.tourismtemplate.view.quiz.QuizzesPageContentLinkViewHolder
import com.xamoom.android.xamoomsdk.Resource.Content
import java.util.ArrayList
import javax.inject.Inject

class QuizzesFragment(val isSolvedQuizzes: Boolean) : Fragment(),
    QuizzesPageContract.View, QuizzesPageContentLinkViewHolder.QuizPageContentLinkListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_recycler_view)
    lateinit var quizzesRecyclerView: RecyclerView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_progress_bar)
    lateinit var progressBar: ProgressBar

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_title)
    lateinit var quizzesTitle: TextView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quizzes_description)
    lateinit var quizzesDescription: TextView

    @Inject
    lateinit var quizzesPagePresenter: QuizzesPagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerQuizzesPageFragmentComponent
            .builder()
            .quizzesPageFragmentModule(QuizzesPageFragmentModule(this, this.activity))
            .build()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_quizzes, container, false)
        ButterKnife.bind(this, view)
        setupRecyclerView()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadQuizzes(isSolvedQuizzes)
    }

    override fun loadQuizzes(isQuizzesSolved: Boolean) {
        quizzesPagePresenter.loadQuizzesContent(isQuizzesSolved, null)
    }

    override fun onLoadedQuizzes(quizzes: ArrayList<Content>) {
        val adapter: QuizzesPageAdapter? = quizzesRecyclerView!!.adapter as QuizzesPageAdapter?
        if (adapter != null) {
            adapter.setQuizzesPageContents(quizzes)
            adapter.notifyDataSetChanged()
        }
    }

    override fun setupRecyclerView() {
        if (activity != null) {
            val context = activity!!.applicationContext
            if (context != null) {
                quizzesRecyclerView!!.adapter = QuizzesPageAdapter(this)
                quizzesRecyclerView!!.layoutManager = LinearLayoutManager(context)
                if (isSolvedQuizzes) {
                    quizzesTitle.text = getString(R.string.quizzes_page_title_solved)
                    quizzesDescription.text = getString(R.string.quizzes_page_solved_excerpt)
                } else {
                    quizzesTitle.text = getString(R.string.quizzes_page_title)
                    quizzesDescription.text = getString(R.string.quizzes_page_excerpt)
                }
            }
        }
    }

    override fun showProgressBar() {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar!!.visibility = View.GONE
    }

    override fun onContentClick(content: Content) {
        val intent = Intent(activity!!.applicationContext, ContentActivity::class.java)
        intent.putExtra(ContentActivity.EXTRA_CONTENT, content)
        intent.putExtra(ContentActivity.EXTRA_IS_BEACON, false)
        startActivity(intent)
    }
}
