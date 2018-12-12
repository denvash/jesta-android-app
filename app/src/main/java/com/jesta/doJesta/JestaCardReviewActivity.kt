package com.jesta.doJesta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R

class JestaCardReviewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)
    }
}