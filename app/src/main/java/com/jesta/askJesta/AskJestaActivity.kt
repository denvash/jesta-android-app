package com.jesta.askJesta

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.R.id.container
import com.jesta.R
import kotlinx.android.synthetic.main.activity_ask_jesta.*

class AskJestaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_jesta)

//        this.setSupportActionBar(jesta_top_bar)
//        jesta_top_bar.setNavigationOnClickListener(NavigationIconClickListener(
//            this@AskJestaActivity,
//            ask_jesta_grid,
//            AccelerateDecelerateInterpolator(),
//            ContextCompat.getDrawable(applicationContext, R.drawable.jesta_branded_menu),
//            ContextCompat.getDrawable(applicationContext, R.drawable.jesta_close_menu)))
//
//        // Set cut corner background for API 23+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ask_jesta_grid.background = applicationContext?.getDrawable(R.drawable.jesta_product_grid_background_shape)
//        }
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.jesta_toolbar_menu, menu)
//        return true
//    }
}
