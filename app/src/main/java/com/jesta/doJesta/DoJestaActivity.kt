package com.jesta.doJesta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.R
import com.jesta.askJesta.AskJestaActivity
import com.jesta.map.MapActivity
import com.jesta.status.StatusActivity
import kotlinx.android.synthetic.main.activity_do_jesta.*
import kotlinx.android.synthetic.main.fragment_bottom_navigation_view.*


class DoJestaActivity : AppCompatActivity() {

    // an instance for referencing the context.
    companion object {
        lateinit var instance: DoJestaActivity
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_jesta)

        // late init
        instance = this@DoJestaActivity

        // set recycle view layout
        val column = if (resources.configuration.orientation == 2) 3 else 2
        do_jesta_recycle_view.layoutManager =
                StaggeredGridLayoutManager(column, 1)

        // prevent the loss of items
        do_jesta_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

        // initial adapter with jesta posts entries
        val adapter = JestaCardRecyclerViewAdapter(JestaCard.initJestaCardList(resources))

        do_jesta_recycle_view.adapter = adapter

        // jesta cards padding
        val scale = resources.displayMetrics.density
        val spacing = (1 * scale + 2.5f).toInt()
        do_jesta_recycle_view.addItemDecoration(JestaCardGridItemDecoration(spacing))

        jesta_bottom_navigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.nav_ask_jesta -> {
                    val intent = Intent(this@DoJestaActivity, AskJestaActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_do_jesta -> {
//                    val intent = Intent(this@DoJestaActivity, DoJestaActivity::class.java)
//                    startActivity(intent)
                }
                R.id.nav_map -> {
                    val intent = Intent(this@DoJestaActivity, MapActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_status -> {
                    val intent = Intent(this@DoJestaActivity, StatusActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}