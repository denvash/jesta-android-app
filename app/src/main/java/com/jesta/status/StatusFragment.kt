package com.jesta.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.MainActivity
import com.jesta.R
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.fragment_status.*

class StatusFragment : Fragment() {

    companion object {
        lateinit var instance: StatusFragment
            private set
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_status, container, false)
//
//        // late init
//        instance = this@StatusFragment
//
//        val sysManager = SysManager(activity as MainActivity)
//        val getAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
//
//        // note: this change the layout of the activity,
//        // therefore you can use setContentView only after calling to sysManager.stopLoadingAnim()
//        sysManager.startLoadingAnim()
//        getAllJestas.addOnCompleteListener { task ->
//            sysManager.stopLoadingAnim()
//
//            if (!task.isSuccessful) {
//                // todo error here! e.g. start ErrorActivity here
//                return@addOnCompleteListener
//            }
//
//
//            // set recycle view layout
//            val column = 1
//            jesta_status_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)
//
//            // prevent the loss of items
//            jesta_status_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)
//
//            // Task completed successfully
//            val result: List<*> = task.result as List<*>
//
//            // initial adapter with mission posts entries
//            val adapter = StatusRecyclerViewAdapter(result.filterIsInstance<Mission>())
//
//            jesta_status_recycle_view.adapter = adapter
//
//            jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary)
//            jesta_status_swipe_refresh.setOnRefreshListener {
//
//                val onRefreshGetAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
//                onRefreshGetAllJestas.addOnCompleteListener { task ->
//
//                    val refreshResult: List<*> = task.result as List<*>
//                    val refreshAdapter = StatusRecyclerViewAdapter(refreshResult.filterIsInstance<Mission>())
//                    jesta_status_recycle_view.adapter = refreshAdapter
//                    jesta_status_swipe_refresh.isRefreshing = false
//                }
//            }
//        }

        return view
    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // late init
//        instance = this@StatusFragment
//
//        val sysManager = SysManager(this@StatusFragment)
//        val getAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
//
//        // note: this change the layout of the activity,
//        // therefore you can use setContentView only after calling to sysManager.stopLoadingAnim()
//        sysManager.startLoadingAnim()
//        getAllJestas.addOnCompleteListener { task ->
//            sysManager.stopLoadingAnim()
//
//            if (!task.isSuccessful) {
//                // todo error here! e.g. start ErrorActivity here
//                return@addOnCompleteListener
//            }
//
//            setContentView(R.layout.activity_status)
//
//
//            // set recycle view layout
//            val column = 1
//            jesta_status_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)
//
//            // prevent the loss of items
//            jesta_status_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)
//
//            // Task completed successfully
//            val result: List<*> = task.result as List<*>
//
//            // initial adapter with mission posts entries
//            val adapter = StatusRecyclerViewAdapter(result.filterIsInstance<Mission>())
//
//            jesta_status_recycle_view.adapter = adapter
//
//            jesta_bottom_navigation.selectedItemId = R.id.nav_status
//            jesta_bottom_navigation.setOnNavigationItemSelectedListener {
//
//                if (it.itemId == jesta_bottom_navigation.selectedItemId) return@setOnNavigationItemSelectedListener true
//
//                val intent = when (it.itemId) {
//                    R.id.nav_do_jesta -> {
//                        Intent(this@StatusFragment, DoJestaFragment::class.java)
//                    }
//                    R.id.nav_ask_jesta -> {
//                        Intent(this@StatusFragment, AskJestaFragment::class.java)
//                    }
//                    // Settings Activity
//                    else -> {
//                        Intent(this@StatusFragment, SettingsFragment::class.java)
//                    }
//                }
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                finish()
//                startActivity(intent)
//                true
//            }
//
//            jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary)
//            jesta_status_swipe_refresh.setOnRefreshListener {
//
//                val onRefreshGetAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
//                onRefreshGetAllJestas.addOnCompleteListener { task ->
//
//                    val refreshResult: List<*> = task.result as List<*>
//                    val refreshAdapter = StatusRecyclerViewAdapter(refreshResult.filterIsInstance<Mission>())
//                    jesta_status_recycle_view.adapter = refreshAdapter
//                    jesta_status_swipe_refresh.isRefreshing = false
//                }
//            }
//        }
//
//
//    }
}
