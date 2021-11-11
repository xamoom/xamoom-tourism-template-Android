package com.android.xamoom.tourismtemplate

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.android.xamoom.tourismtemplate.fragments.OnboardFragment
import com.android.xamoom.tourismtemplate.utils.ApiUtil
import com.android.xamoom.tourismtemplate.utils.InAppNotificationUtil

class OnboardActivity : AppCompatActivity(), OnboardFragment.OnboardFragmentListener {

    @BindView(R.id.onboard_root_layout)
    lateinit var rootLayout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSLUCENT)
        setContentView(R.layout.activity_onboard)

        ButterKnife.bind(this)
        ApiUtil.getInstance().setInAppNotificationUtil(
                InAppNotificationUtil(this, rootLayout))

        val fragment = OnboardFragment.newInstance()
        var id: String? = null

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.onboard_main_frame_layout, fragment)
                .commit()
    }

    override fun finishActivity() {
        finish()
    }

}