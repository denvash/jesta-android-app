package com.jesta.gui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.data.Relation
import com.jesta.utils.db.SysManager
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
        val rel = intent.getParcelableArrayListExtra<Relation>(extra) as ArrayList<Relation>
        //TODO: create text views dynamically for all doers , rel is a list of doers
//        jesta_status_bar_preview_title.text = rel[0].doerList

        jesta_status_preview_back_button.setOnClickListener {
            finish()
        }
    }
}