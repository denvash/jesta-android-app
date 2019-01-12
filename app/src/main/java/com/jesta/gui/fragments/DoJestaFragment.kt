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

        // set recycle view layout
        val column = if (resources.configuration.orientation == 2) 3 else 2
        view.do_jesta_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)

        // prevent the loss of items
        view.do_jesta_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

        sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS).addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                // todo error here! e.g. start ErrorActivity here
                return@addOnCompleteListener
            }

            // Task completed successfully
            val missionList = (task.result as List<*>).filterIsInstance<Mission>().reversed()
                .filter { mission -> mission.isAvailable }

//            view.do_jesta_empty_page.visibility = if (missionList.isEmpty()) View.VISIBLE else View.INVISIBLE


            // initial adapter with mission posts entries
            view?.do_jesta_recycle_view?.adapter = CardAdapter(missionList)

            view.do_jesta_progress_bar.hide()
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

//                view.do_jesta_empty_page.visibility = if (missionListOnRefresh.isEmpty()) View.VISIBLE else View.INVISIBLE

                view.do_jesta_recycle_view.adapter = CardAdapter(missionListOnRefresh)
                view.do_jesta_swipe_refresh.isRefreshing = false
                view.do_jesta_progress_bar.hide()
            }
        }

        return view
    }
}