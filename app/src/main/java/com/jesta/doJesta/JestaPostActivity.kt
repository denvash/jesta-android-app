package com.jesta.doJesta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import kotlinx.android.synthetic.main.jesta_card.*


class JestaPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jesta_post)

        jesta_card_description.text = intent.getStringExtra("description")
    }
}