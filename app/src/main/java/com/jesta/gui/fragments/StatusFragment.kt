package com.jesta.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.jesta.R
import com.jesta.data.Status
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.adapters.StatusAdapter
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_status.view.*

class StatusFragment : Fragment() {
    private val sysManager = SysManager(MainActivity.instance)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_status, container, false)


        if (sysManager.currentUserFromDB == null) return view

        sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS).addOnCompleteListener { reloadJestasTask ->
            sysManager.statusList.addOnCompleteListener { userRelationsTask ->
                if (!userRelationsTask.isSuccessful || !reloadJestasTask.isSuccessful) {
                    @Suppress("LABEL_NAME_CLASH")
                    return@addOnCompleteListener
                }

                setRecycleView(view!!, userRelationsTask)

                view.jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
                view.jesta_status_swipe_refresh.setOnRefreshListener {

                    sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
                        .addOnCompleteListener {

                            sysManager.statusList
                                .addOnCompleteListener { refreshRelationsTask ->

                                    @Suppress("LABEL_NAME_CLASH")
                                    if (!refreshRelationsTask.isSuccessful) return@addOnCompleteListener

                                    setRecycleView(view, refreshRelationsTask)
                                    view.jesta_status_swipe_refresh.isRefreshing = false
                                }
                        }
                }
            }

        }
        return view
    }

    private fun setRecycleView(view: View, statusTask: Task<Any>) {

        val statusList = (statusTask.result as List<*>).filterIsInstance<Status>()

        view.jesta_status_empty_image.visibility = if (statusList.isEmpty())  View.VISIBLE else View.GONE
        val adapter = StatusAdapter()
        adapter.setItems(statusList)
        view.jesta_status_recycle_view.layoutManager = LinearLayoutManager(MainActivity.instance)
        view.jesta_status_recycle_view.adapter = adapter
    }
}
