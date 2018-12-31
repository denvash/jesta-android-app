package com.jesta.doJesta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.R
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.fragment_do_jesta.*


class DoJestaFragment : Fragment() {

    companion object {
        private val TAG = DoJestaFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_do_jesta, container, false)
//
//        val sysManager = SysManager(this)
//        val getAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
//
////         note: this change the layout of the activity,
////         therefore you can use setContentView only after calling to sysManager.stopLoadingAnim()
//        sysManager.startLoadingAnim()
//        getAllJestas.addOnCompleteListener { task ->
//            sysManager.stopLoadingAnim()
//
//            if (!task.isSuccessful) {
//                // todo error here! e.g. start ErrorActivity here
//                return@addOnCompleteListener
//            }
//
//            // set recycle view layout
//            val column = if (resources.configuration.orientation == 2) 3 else 2
//            do_jesta_recycle_view.layoutManager =
//                    StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)
//
//            // prevent the loss of items
//            do_jesta_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)
//
//            // Task completed successfully
//            val result: List<*> = task.result as List<*>
//
//            val missionList = result.filterIsInstance<Mission>()
//
//            // initial adapter with mission posts entries
//            val adapter = JestaCardRecyclerViewAdapter(missionList)
//
//            Log.i(TAG, missionList.toString())
//
//            do_jesta_recycle_view.adapter = adapter
//
//            // Note: The layout has internal spacing;
//            // mission cards padding example.
//            // val scale = resources.displayMetrics.density
//            // val spacing = (1 * scale + 2.0f).toInt()
//            //do_jesta_recycle_view.addItemDecoration(JestaCardGridItemDecoration(0))
//
//            do_jesta_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
//            do_jesta_swipe_refresh.setOnRefreshListener {
//
//                val onRefreshGetAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
//                onRefreshGetAllJestas.addOnCompleteListener { task ->
//
//                    val refreshResult: List<*> = task.result as List<*>
//                    val refreshAdapter = JestaCardRecyclerViewAdapter(refreshResult.filterIsInstance<Mission>())
//                    do_jesta_recycle_view.adapter = refreshAdapter
//                    do_jesta_swipe_refresh.isRefreshing = false
//                }
//            }
//        }
        return view
    }
}