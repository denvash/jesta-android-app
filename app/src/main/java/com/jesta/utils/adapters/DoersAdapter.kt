package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jesta.R
import com.jesta.data.*
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.ChatFragment
import com.jesta.data.chat.ChatRoom
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.jesta_doers.view.*
import kotlin.random.Random

class DoersAdapter internal constructor(
    private val doerList: List<Relation>,
    private val status: Status,
    private val mission: Mission
) : RecyclerView.Adapter<DoersAdapter.JestaCardViewHolder>() {

    private val sysManager = SysManager(MainActivity.instance)

    inner class JestaCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var doerBar: LinearLayout = itemView.findViewById(R.id.jesta_doers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JestaCardViewHolder {
        val cardLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.jesta_doers, parent, false)
        return JestaCardViewHolder(cardLayoutView)
    }

    override fun onBindViewHolder(holder: JestaCardViewHolder, position: Int) {
        if (position < doerList.size) {
            val doerRelation = doerList[position]
            val doer = sysManager.getUserByID(doerList[position].doerID)
            val bar = holder.doerBar
            bar.jesta_doers_name.text = doer.displayName
            bar.jesta_doers_diamond_amount.text = Random.nextInt(1, 40000).toString()

            Picasso.get().load(doer.photoUrl).noFade().into(holder.doerBar.jesta_doers_avatar)

            if (doerRelation.status != RELATION_STATUS_INIT) {
                holder.doerBar.jesta_doers_accept.isEnabled = false
                holder.doerBar.jesta_doers_decline.isEnabled = false
            }

            holder.doerBar.jesta_doers_accept.setOnClickListener {
                Toast.makeText(it.context,"Accept Clicked",Toast.LENGTH_LONG).show()
                sysManager.onAcceptDoer(doerList[position],mission)
                holder.doerBar.jesta_doers_accept.isEnabled = false
            }

            holder.doerBar.jesta_doers_decline.setOnClickListener {
                Toast.makeText(it.context,"Declined Clicked",Toast.LENGTH_LONG).show()
                sysManager.onDeclineUser(doerList[position])
                holder.doerBar.jesta_doers_accept.isEnabled = false
            }

            holder.doerBar.jesta_doers_chat.setOnClickListener {
                val roomDoer = sysManager.getUserByID(doerList[position].doerID)
                val poster = sysManager.getUserByID(mission.posterID)
                val chatRoom = ChatRoom(roomDoer, poster, mission)

                MainActivity.instance.fragNavController.pushFragment(
                    ChatFragment.newInstance(
                        chatRoom.id!!,
                        doerList[position].id,
                        mission.id
                    )
                )
            }
        }


    }

    override fun getItemCount(): Int {
        return doerList.size
    }
}