package com.jesta.doJesta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.R
import com.jesta.askJesta.AskJestaActivity
import com.jesta.settings.SettingsActivity
import com.jesta.status.StatusActivity
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_do_jesta.*
import kotlinx.android.synthetic.main.frame_bottom_navigation_view.*


class DoJestaActivity : AppCompatActivity() {

    // an instance for referencing the context.
    companion object {
        lateinit var instance: DoJestaActivity
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // late init
        instance = this@DoJestaActivity

        val sysManager = SysManager(this@DoJestaActivity)
        val getAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)

        // note: this change the layout of the activity,
        // therefore you can use setContentView only after calling to sysManager.stopLoadingAnim()
        sysManager.startLoadingAnim()
        getAllJestas.addOnCompleteListener { task ->
            sysManager.stopLoadingAnim()

            if (!task.isSuccessful) {
                // todo error here! e.g. start ErrorActivity here
                return@addOnCompleteListener
            }

            setContentView(R.layout.activity_do_jesta)


            // set recycle view layout
            val column = if (resources.configuration.orientation == 2) 3 else 2
            do_jesta_recycle_view.layoutManager =
                    StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)

            // prevent the loss of items
            do_jesta_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

            // Task completed successfully
            val result: List<*> = task.result as List<*>

            // initial adapter with mission posts entries
            val adapter = JestaCardRecyclerViewAdapter(result.filterIsInstance<Mission>())

            do_jesta_recycle_view.adapter = adapter

            // Note: The layout has internal spacing;
            // mission cards padding example.
            // val scale = resources.displayMetrics.density
            // val spacing = (1 * scale + 2.0f).toInt()
            //do_jesta_recycle_view.addItemDecoration(JestaCardGridItemDecoration(0))

            jesta_bottom_navigation.selectedItemId = R.id.nav_do_jesta
            jesta_bottom_navigation.setOnNavigationItemSelectedListener {

                if (it.itemId == jesta_bottom_navigation.selectedItemId) return@setOnNavigationItemSelectedListener true

                val intent = when (it.itemId) {
                    R.id.nav_ask_jesta -> {
                        Intent(this@DoJestaActivity, AskJestaActivity::class.java)
                    }
                    R.id.nav_status -> {
                        Intent(this@DoJestaActivity, StatusActivity::class.java)
                    }
                    // Settings Activity
                    else -> {
                        Intent(this@DoJestaActivity, SettingsActivity::class.java)
                    }
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                startActivity(intent)
                true
            }

            do_jesta_swipe_refresh.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary)
            do_jesta_swipe_refresh.setOnRefreshListener {

                val onRefreshGetAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
                onRefreshGetAllJestas.addOnCompleteListener { task ->

                    val refreshResult: List<*> = task.result as List<*>
                    val refreshAdapter = JestaCardRecyclerViewAdapter(refreshResult.filterIsInstance<Mission>())
                    do_jesta_recycle_view.adapter = refreshAdapter
                    do_jesta_swipe_refresh.isRefreshing = false
                }
            }
        }
    }
}