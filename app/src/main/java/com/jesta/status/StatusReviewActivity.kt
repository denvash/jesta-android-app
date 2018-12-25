package com.jesta.status

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.util.ImageReq
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_status_preview.*
import kotlinx.android.synthetic.main.jesta_status_bar_preview.*

class StatusReviewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_preview)

        SysManager(this@StatusReviewActivity)
        val mission = intent.getParcelableExtra(extra) as Mission

        jesta_status_bar_preview_title.text = mission.title

        jesta_status_preview_back_button.setOnClickListener {
            finish()
        }
    }
}