package com.jesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.jesta.login.SysManager

class PathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path)

//        // TODO: implement getCurrentUser
//        val sysManager = SysManager();
//        val user = sysManager.getCurrentUser()
//        if (user == null) {
//            val i = Intent(this, MainActivity::class.java)// todo go to OTPActivity
//            //            finish();
//            startActivity(i)
//            return
//        }

        val ask_jesta: Button = findViewById(R.id.ask_jesta_button)
        val do_jesta: Button = findViewById(R.id.do_jesta_button)

        ask_jesta.setOnClickListener {
            val intent = Intent(this, AskJestaActivity::class.java)
            startActivity(intent)
        }
            // go to do jesta activity
//        do_jesta.setOnClickListener {
//            val intent = Intent(this, PostGridFragment::class.java)
//        }
    }
}
