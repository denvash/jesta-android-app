package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.jesta.R
import com.jesta.data.*
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.jesta_status.view.*
import java.util.*

class StatusAdapter(private val missionToDoers: Map<String, List<String>>) :
    RecyclerView.Adapter<StatusAdapter.RecyclerHolder>() {

    private val list = ArrayList<Pair<Relation, Mission>>()
    private val expansionsCollection = ExpansionLayoutCollection()
    private val sysManager = SysManager(MainActivity.instance)

    init {
        expansionsCollection.openOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        val status = list[position]
        val currUser = sysManager.currentUserFromDB

        val relation = status.first
        val mission = status.second

        val isPosterMission = currUser.id == mission.posterID

        holder.bind(list[position])
        expansionsCollection.add(holder.expansionLayout)

        holder.view.jesta_status_title.text = mission.title
        holder.view.jesta_status_total_doers.text = mission.numOfPeople.toString()
        val prefix = "As a "
        holder.view.jesta_status_job.text = if (isPosterMission) prefix + STATUS_POSTER else prefix + STATUS_DOER
        holder.view.jesta_status_total_doers.visibility = if (isPosterMission) View.VISIBLE else View.INVISIBLE
        holder.view.jesta_status_divider.visibility = if (isPosterMission) View.VISIBLE else View.INVISIBLE


        holder.view.jesta_status_phase.text = when (relation.status) {
            RELATION_STATUS_INIT -> STATUS_PENDING
            RELATION_STATUS_IN_PROGRESS -> STATUS_IN_PROGRESS
            RELATION_STATUS_DONE -> STATUS_DONE
            else -> STATUS_DECLINED
        }

        holder.view.jesta_status_phase.setTextColor(
            ContextCompat.getColor(
                MainActivity.instance,
                when (relation.status) {
                    RELATION_STATUS_INIT -> R.color.status_pending
                    RELATION_STATUS_IN_PROGRESS -> R.color.status_in_progress
                    RELATION_STATUS_DONE -> R.color.status_done
                    else -> R.color.status_declined
                }
            )
        )

        holder.view.jesta_status_doers_recycle_view.layoutManager = LinearLayoutManager(MainActivity.instance)
        holder.view.jesta_status_doers_recycle_view.adapter =
                DoersAdapter(missionToDoers[mission.id]!!, relation, mission)


        val posterAvatar = sysManager.getUserByID(mission.posterID).photoUrl
        if (posterAvatar != MISSION_EMPTY_AUTHOR_IMAGE) {
            Picasso.get().load(posterAvatar).noFade().into(holder.view.jesta_status_avatar)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItems(items: List<Pair<Relation, Mission>>) {
        this.list.addAll(items)
        notifyDataSetChanged()
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val view = itemView.findViewById<LinearLayout>(R.id.jesta_status)!!
        val expansionLayout: ExpansionLayout = itemView.statusExpansionLayout

        fun bind(`object`: Any) {
            expansionLayout.collapse(false)
        }

        companion object {

            private val LAYOUT = R.layout.jesta_status

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(LAYOUT, viewGroup, false))
            }
        }
    }
}