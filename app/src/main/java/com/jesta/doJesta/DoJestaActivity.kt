package com.jesta.doJesta

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jesta.R
import kotlinx.android.synthetic.main.activity_do_jesta.*


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

        // activity with bottom action bar
        setSupportActionBar(bottom_app_bar_do_jesta)

        // set recycle view layout
        do_jesta_recycle_view.layoutManager =
                GridLayoutManager(this@DoJestaActivity, 2, RecyclerView.VERTICAL, false)

        // initial adapter with jesta posts entries
        val adapter = JestaCardRecyclerViewAdapter(JestaCard.initJestaCardList(resources))

        do_jesta_recycle_view.adapter = adapter

        // jesta cards padding
        val largePadding = resources.getDimensionPixelSize(R.dimen._16sdp)
        val smallPadding = resources.getDimensionPixelSize(R.dimen._4sdp)

        do_jesta_recycle_view.addItemDecoration(JestaCardGridItemDecoration(largePadding, smallPadding))

        do_jesta_fab_button.setOnClickListener {
            Toast.makeText(this@DoJestaActivity,"fab clicked",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                val bottomNavDrawerFragment = BottomNavigationFragment()
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}