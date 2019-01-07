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
import com.jesta.data.chat.ChatRoom
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.ChatFragment
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.jesta_status.view.*
import kotlinx.android.synthetic.main.jesta_status_doer.view.*
import kotlinx.android.synthetic.main.jesta_status_done.view.*
import kotlinx.android.synthetic.main.jesta_status_poster.view.*
import kotlinx.android.synthetic.main.jesta_status_poster_complete.view.*
import kotlinx.android.synthetic.main.jesta_status_poster_no_doers.view.*
import java.util.*

class StatusAdapter : RecyclerView.Adapter<StatusAdapter.RecyclerHolder>() {

    private val list = ArrayList<Status>()
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
        val mission = sysManager.getMissionByID(status.missionID)


        holder.bind(list[position])
        expansionsCollection.add(holder.expansionLayout)

        holder.view.jesta_status_title.text = mission.title
        holder.view.jesta_status_total_doers.text = mission.numOfPeople.toString()

        if (status.status == RELATION_STATUS_DONE || !status.isPoster) {
            holder.view.jesta_status_doer_layout.visibility = View.VISIBLE

            holder.view.jesta_status_total_doers.visibility = View.INVISIBLE
            holder.view.jesta_status_divider.visibility = View.INVISIBLE

            holder.view.jesta_status_doer_chat.setOnClickListener {
                val roomDoer = sysManager.currentUserFromDB
                val poster = sysManager.getUserByID(mission.posterID)
                val chatRoom = ChatRoom(roomDoer, poster, mission)

                MainActivity.instance.fragNavController.pushFragment(
                    ChatFragment.newInstance(
                        chatRoom.id!!,
                        roomDoer.id,
                        mission.id
                    )
                )
            }
        } else if (status.isPoster && status.doerIDList.last().doerID == RELATION_EMPTY_DOER_ID) {
            holder.view.jesta_status_poster_no_doers_layout.visibility = View.VISIBLE
        } else if (status.isPoster) {
            when {
                mission.numOfPeople == 0 -> {
                    holder.view.jesta_status_poster_complete_layout.visibility = View.VISIBLE
                    holder.view.jesta_status_complete_doers_recycle_view.layoutManager =
                            LinearLayoutManager(MainActivity.instance)
                    holder.view.jesta_status_complete_doers_recycle_view.adapter = DoersAdapter(
                        status.doerIDList.filter { it.status == RELATION_STATUS_IN_PROGRESS },
                        status,
                        mission
                    )
                }
                status.status == RELATION_STATUS_DONE -> {
                    holder.view.jesta_status_done_complete_layout.visibility = View.VISIBLE
                    holder.view.jesta_status_done_recycle_view.layoutManager =
                            LinearLayoutManager(MainActivity.instance)
                    holder.view.jesta_status_done_recycle_view.adapter = DoersAdapter(
                        status.doerIDList.filter { it.status == RELATION_STATUS_DONE },
                        status,
                        mission
                    )
                }
                else -> holder.view.jesta_status_poster_layout.visibility = View.VISIBLE
            }
        }


        holder.view.jesta_status_button_complete.setOnClickListener {
            
             sysManager.updateRelationsDone(status)
        }

        holder.view.jesta_status_done_button_remove.setOnClickListener {
            Alerter.create(MainActivity.instance)
                .setTitle("Nice you complete a Jesta! \uD83D\uDCA0")
                .setText("Now claim your rewards!")
                .setBackgroundColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_jesta_diamond_normal)
                .show()
            sysManager.moveToGraveDB(status)
        }


        if (mission.numOfPeople == 0) {
            holder.view.jesta_status_total_doers.visibility = View.INVISIBLE
            holder.view.jesta_status_divider.visibility = View.INVISIBLE
        }

        val prefix = "As a "
        holder.view.jesta_status_job.text = if (status.isPoster) prefix + STATUS_POSTER else prefix + STATUS_DOER

        holder.view.jesta_status_phase.text = when (status.status) {
            RELATION_STATUS_INIT -> STATUS_PENDING
            RELATION_STATUS_IN_PROGRESS -> STATUS_IN_PROGRESS
            RELATION_STATUS_DONE -> STATUS_DONE
            else -> STATUS_DECLINED
        }

        holder.view.jesta_status_phase.setTextColor(
            ContextCompat.getColor(
                MainActivity.instance,
                when (status.status) {
                    RELATION_STATUS_INIT -> R.color.status_pending
                    RELATION_STATUS_IN_PROGRESS -> R.color.status_in_progress
                    RELATION_STATUS_DONE -> R.color.status_done
                    else -> R.color.status_declined
                }
            )
        )

        holder.view.jesta_status_doers_recycle_view.layoutManager = LinearLayoutManager(MainActivity.instance)
        holder.view.jesta_status_doers_recycle_view.adapter =
                if (status.doerIDList.size == 1) DoersAdapter(status.doerIDList, status, mission)
                else DoersAdapter(status.doerIDList.takeLast(status.doerIDList.size - 1), status, mission)

        val posterAvatar = sysManager.getUserByID(mission.posterID).photoUrl
        Picasso.get().load(posterAvatar).noFade().into(holder.view.jesta_status_avatar)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItems(items: List<Status>) {
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
            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.jesta_status, viewGroup, false))
            }
        }
    }
}