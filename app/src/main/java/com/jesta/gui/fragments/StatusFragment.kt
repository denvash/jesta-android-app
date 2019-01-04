@file:Suppress("LABEL_NAME_CLASH")

package com.jesta.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.data.Relation
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.adapters.StatusAdapter
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_status.view.*

class StatusFragment : Fragment() {
    private val sysManager = SysManager(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_status, container, false)

        sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS).addOnCompleteListener { reloadJestasTask ->
            sysManager.getUserRelations(sysManager.currentUserFromDB.id).addOnCompleteListener { userRelationsTask ->
                if (!userRelationsTask.isSuccessful || !reloadJestasTask.isSuccessful) {
                    return@addOnCompleteListener
                }

                setRecycleView(view!!,reloadJestasTask,userRelationsTask)

                view.jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
                view.jesta_status_swipe_refresh.setOnRefreshListener {

                    sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
                        .addOnCompleteListener { refreshReloadTask ->

                            sysManager.getUserRelations(sysManager.currentUserFromDB.id)
                                .addOnCompleteListener { refreshRelationsTask ->

                                    if (!refreshRelationsTask.isSuccessful) {
                                        return@addOnCompleteListener
                                    }

                                    setRecycleView(view,refreshReloadTask,refreshRelationsTask)
                                    view.jesta_status_swipe_refresh.isRefreshing = false
                                }
                        }
                }
            }

        }
        return view
    }

    private fun setRecycleView(view: View, reloadJestasTask: Task<Any>, userRelationsTask: Task<Any>) {
        val statusList: List<Pair<Relation, Mission>> = toStatusList(userRelationsTask, reloadJestasTask)

        val allRelations = statusList.map { it.first }

        val missionToDoers = statusList.map {
            it.second.id to allRelations.filter { relation ->
                relation.missionID == it.second.id
            }.map { relation -> relation.doerID }
        }.map { it.first to it.second }.toMap()

        val adapter = StatusAdapter(missionToDoers)
        adapter.setItems(statusList)
        view.jesta_status_recycle_view.layoutManager = LinearLayoutManager(MainActivity.instance)
        view.jesta_status_recycle_view.adapter = adapter
    }

    private fun toStatusList(
        userRelationsTask: Task<Any>,
        reloadJestasTask: Task<Any>
    ): List<Pair<Relation, Mission>> {
        val userRelations: List<Relation> = (userRelationsTask.result as List<*>).filterIsInstance<Relation>()
        val allMissions: List<Mission> = (reloadJestasTask.result as List<*>).filterIsInstance<Mission>()
        val relatedMissionsIDs = userRelations.map { it.missionID }
        val relatedMissions = allMissions.filter { relatedMissionsIDs.contains(it.id) }
        return userRelations.map { relation -> relation to relatedMissions.find { it.id == relation.missionID }!! }
    }
}
