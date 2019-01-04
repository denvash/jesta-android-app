package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.jesta.R
import com.jesta.data.*
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.jesta_card_preview.view.*
import kotlinx.android.synthetic.main.jesta_status.view.*

import java.util.ArrayList

class StatusAdapter(
) : RecyclerView.Adapter<StatusAdapter.RecyclerHolder>() {

    private val list = ArrayList<Pair<Relation,Mission>>()
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

        holder.bind(list[position])
        expansionsCollection.add(holder.expansionLayout)

        holder.view.jesta_status_title.text = mission.title
        holder.view.jesta_status_job.text = if (currUser.id==mission.posterID) STATUS_POSTER else STATUS_DOER

        holder.view.jesta_status_phase.text = when (relation.status) {
            RELATION_STATUS_INIT -> STATUS_PENDING
            RELATION_STATUS_IN_PROGRESS -> STATUS_IN_PROGRESS
            RELATION_STATUS_DONE -> STATUS_DONE
            else -> STATUS_DECLINED
        }

        holder.view.jesta_status_phase.setTextColor(
            when (relation.status) {
                RELATION_STATUS_INIT -> R.color.status_pending
                RELATION_STATUS_IN_PROGRESS -> R.color.status_in_progress
                RELATION_STATUS_DONE -> R.color.status_done
                else -> R.color.status_declined
            }
        )

        val posterAvatar = sysManager.getUserByID(mission.posterID).photoUrl
        if (posterAvatar != MISSION_EMPTY_AUTHOR_IMAGE) {
            Picasso.get().load(posterAvatar).noFade().into(holder.view.jesta_status_avatar)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItems(items: List<Pair<Relation,Mission>>) {
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