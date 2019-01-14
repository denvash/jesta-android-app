package com.jesta.gui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.adapters.CardAdapter
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_do_jesta.view.*


class DoJestaFragment : Fragment() {

    companion object {
        private val TAG = DoJestaFragment::class.java.simpleName
    }

    private val sysManager = SysManager(MainActivity.instance)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_do_jesta, container, false)

        sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS).addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                MainActivity.instance.alertError(task.exception!!.message)
                return@addOnCompleteListener
            }

            // Task completed successfully
            val missionList = (task.result as List<*>).filterIsInstance<Mission>().reversed()
                .filter { mission -> mission.isAvailable }

            setRecycleView(view,missionList)
        }

        // set refresh on swiping top
        view.do_jesta_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        view.do_jesta_swipe_refresh.setOnRefreshListener {

            view.do_jesta_progress_bar.show()
            sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS).addOnCompleteListener {

                if (!it.isSuccessful) {
                    MainActivity.instance.alertError(it.exception!!.message)
                    return@addOnCompleteListener
                }

                val missionListOnRefresh = (it.result as List<*>).filterIsInstance<Mission>().reversed()
                    .filter { mission -> mission.isAvailable }

                setRecycleView(view,missionListOnRefresh)
            }
        }

        return view
    }

    private fun setRecycleView(view: View, missionList: List<Mission>) {

        // set recycle view layout
        val column = if (resources.configuration.orientation == 2) 3 else 2
        view.do_jesta_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)

        // prevent the loss of items
        view.do_jesta_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

        view.do_jesta_progress_bar.hide()
        if (missionList.isEmpty()) view.do_jesta_empty.visibility = View.VISIBLE
        view.do_jesta_recycle_view.adapter = CardAdapter(missionList)
        view.do_jesta_swipe_refresh.isRefreshing = false
    }
}