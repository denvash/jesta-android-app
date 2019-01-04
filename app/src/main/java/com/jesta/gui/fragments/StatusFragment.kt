@file:Suppress("LABEL_NAME_CLASH")

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
import com.jesta.data.Relation
import com.jesta.utils.adapters.StatusRecyclerViewAdapter
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

                val allMissions = (reloadJestasTask.result as List<*>).filterIsInstance<Mission>()
                val allRelations: List<Relation> = (userRelationsTask.result as List<*>).filterIsInstance<Relation>()

                val column = 1
                view.jesta_status_recycle_view.layoutManager = StaggeredGridLayoutManager(column, RecyclerView.VERTICAL)
                view.jesta_status_recycle_view.recycledViewPool.setMaxRecycledViews(0, 0)

                view.jesta_status_recycle_view.adapter = getRelatedAdapter(allRelations, allMissions)

                view.jesta_status_swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
                view.jesta_status_swipe_refresh.setOnRefreshListener {

                    sysManager.createDBTask(SysManager.DBTask.RELOAD_JESTAS)
                        .addOnCompleteListener { refreshReloadTask ->

                            sysManager.getUserRelations(sysManager.currentUserFromDB.id)
                                .addOnCompleteListener { refreshRelationsTask ->

                                    if (!refreshRelationsTask.isSuccessful) {
                                        return@addOnCompleteListener
                                    }

                                    val refreshAllRelations: List<Relation> =
                                        (refreshRelationsTask.result as List<*>).filterIsInstance<Relation>()
                                    val refreshAllMissions =
                                        (refreshReloadTask.result as List<*>).filterIsInstance<Mission>()

                                    view.jesta_status_recycle_view.adapter =
                                            getRelatedAdapter(refreshAllRelations, refreshAllMissions)
                                    view.jesta_status_swipe_refresh.isRefreshing = false
                                }
                        }
                }
            }

        }
        return view
    }

    private fun getRelatedAdapter(allRelations: List<Relation>, allMissions: List<Mission>): RecyclerView.Adapter<*>? {
        val userID = sysManager.currentUserFromDB.id

        // get All related relations
        val asAPoster = allRelations.filter { it.posterID == userID }
        val asADoer = allRelations.filter { it.doerList.contains(userID) }
        val relatedRelations = asAPoster + asADoer

        // Get all related Missions
        val relatedMissionsIDs = relatedRelations.map { it.missionID }
        val relatedMissions = allMissions.filter { relatedMissionsIDs.contains(it.id) }

        // initial adapter with mission posts entries
        return StatusRecyclerViewAdapter(relatedRelations, relatedMissions)
    }
}
