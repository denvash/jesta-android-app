package com.jesta.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesta.R
import com.jesta.data.Relation
import com.jesta.utils.adapters.StatusRecyclerViewAdapter
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_status.view.*

class StatusFragment : Fragment() {
    private val sysManager = SysManager(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_status, container, false)
        val reloadUsersTasks = sysManager.createDBTask(SysManager.DBTask.RELOAD_USERS)
        reloadUsersTasks.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // todo error

            } else {
                val userRels = sysManager.getUserRels(sysManager.currentUserFromDB.id)

                userRels.addOnCompleteListener { innerTask ->

                    if (!innerTask.isSuccessful) {
                        // todo error here! e.g. start ErrorActivity here
                    }

                    val result: List<Relation> = (innerTask.result as List<*>).filterIsInstance<Relation>()
                    val relMap: HashMap<String, ArrayList<Relation>> = HashMap(11)
                    for(i in result){
                        var lst: ArrayList<Relation>? = relMap[i.jesta_id]
                        if (lst == null) {
                            lst = ArrayList()
                            relMap[i.jesta_id] = lst
                        }
                        lst.add(i)
                    }
                    val relList: MutableList<ArrayList<Relation>> = ArrayList()
                    for(i in relMap)
                        relList.add(i.value)

                    // set recycle view layout
                    val column = 1
                    view.jesta_status_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)

                    // prevent the loss of items
                    view.jesta_status_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

                    // Task completed successfully

                    // initial adapter with mission posts entries
                    val adapter = StatusRecyclerViewAdapter(relList)

                    view.jesta_status_recycle_view.adapter = adapter

                    view.jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
                    view.jesta_status_swipe_refresh.setOnRefreshListener {

                        val onRefreshGetAllJestas = sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
                        onRefreshGetAllJestas.addOnCompleteListener { task ->

                            val refreshResult: List<Relation> = (task.result as List<*>).filterIsInstance<Relation>()
                            val refreshRelMap: HashMap<String, ArrayList<Relation>> = HashMap(11)
                            for(i in refreshResult){
                                var lst1: ArrayList<Relation>? = refreshRelMap[i.jesta_id]
                                if (lst1 == null) {
                                    lst1 = ArrayList()
                                    refreshRelMap[i.jesta_id] = lst1
                                }
                                lst1.add(i)
                            }
                            val refreshRelList: MutableList<ArrayList<Relation>> = ArrayList()
                            for(i in refreshRelMap)
                                refreshRelList.add(i.value)

                            val refreshAdapter = StatusRecyclerViewAdapter(refreshRelList)
                            view.jesta_status_recycle_view.adapter = refreshAdapter
                            view.jesta_status_swipe_refresh.isRefreshing = false
                        }
                    }
                }
            }
        }


        return view
    }
}
