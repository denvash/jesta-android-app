package com.jesta.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.utils.adapters.StatusRecyclerViewAdapter
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_status.view.*

class StatusFragment : Fragment() {
    private val sysManager = SysManager(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_status, container, false)
        val getAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)

        getAllJestas.addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                // todo error here! e.g. start ErrorActivity here
                return@addOnCompleteListener
            }

            // set recycle view layout
            val column = 1
            view.jesta_status_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)

            // prevent the loss of items
            view.jesta_status_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

            // Task completed successfully
            val missionList = (task.result as List<*>).filterIsInstance<Mission>()

            // initial adapter with mission posts entries
            val adapter = StatusRecyclerViewAdapter(missionList)

            view.jesta_status_recycle_view.adapter = adapter

            view.jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
            view.jesta_status_swipe_refresh.setOnRefreshListener {

                val onRefreshGetAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
                onRefreshGetAllJestas.addOnCompleteListener { task ->

                    val refreshResult: List<*> = task.result as List<*>
                    val refreshAdapter =
                        StatusRecyclerViewAdapter(refreshResult.filterIsInstance<Mission>())
                    view.jesta_status_recycle_view.adapter = refreshAdapter
                    view.jesta_status_swipe_refresh.isRefreshing = false
                }
            }
        }

        return view
    }
}
