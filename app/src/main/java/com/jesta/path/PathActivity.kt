package com.jesta.path

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jesta.askJesta.AskJestaActivity
import com.jesta.R
import com.jesta.doJesta.DoJestaActivity
import kotlinx.android.synthetic.main.activity_path.*

class PathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path)

        ask_jesta_button.setOnClickListener {
            val intent = Intent(this, AskJestaActivity::class.java)
            startActivity(intent)
        }

        do_jesta_button.setOnClickListener {
            val intent = Intent(this, DoJestaActivity::class.java)
            startActivity(intent)
        }
    }
}
