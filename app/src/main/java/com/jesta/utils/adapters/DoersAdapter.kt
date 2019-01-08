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
import com.tapadoo.alerter.Alerter
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

            // All actions related to doer
            if (doer != null && doer.displayName != RELATION_EMPTY_ID) {
                bar.jesta_doers_name.text = doer.displayName
                Picasso.get().load(doer.photoUrl).noFade().into(holder.doerBar.jesta_doers_avatar)
            }

            bar.jesta_doers_diamond_amount.text = Random.nextInt(1, 40000).toString()

            if (doerRelation.status != RELATION_STATUS_INIT) {
                holder.doerBar.jesta_doers_accept.isEnabled = false
                holder.doerBar.jesta_doers_decline.isEnabled = false

                holder.doerBar.jesta_doers_accept.visibility = View.INVISIBLE
                holder.doerBar.jesta_doers_decline.visibility = View.INVISIBLE
            }

            holder.doerBar.jesta_doers_accept.setOnClickListener {
                if (holder.doerBar.jesta_doers_accept.isEnabled) {
                    Alerter.create(MainActivity.instance)
                        .setTitle("You assigned a Doer! Well Done! \uD83E\uDD70")
                        .setText("The Doer will do you a Jesta in no time! \uD83E\uDD1E")
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_jesta_accept)
                        .show()
                    Toast.makeText(it.context,"Accept Clicked",Toast.LENGTH_LONG).show()
                }
                holder.doerBar.jesta_doers_accept.isEnabled = false
                holder.doerBar.jesta_doers_decline.isEnabled = false

                holder.doerBar.jesta_doers_accept.visibility = View.INVISIBLE
                holder.doerBar.jesta_doers_decline.visibility = View.INVISIBLE
                sysManager.onAcceptDoer(doerList[position],mission)
            }

            holder.doerBar.jesta_doers_decline.setOnClickListener {
                if (holder.doerBar.jesta_doers_accept.isEnabled) {
                    Alerter.create(MainActivity.instance)
                        .setTitle("You declined a Doer! \uD83E\uDD7A")
                        .setText("No worries you will get an offer in no time! \uD83E\uDD1E")
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_remove)
                        .show()
                    Toast.makeText(it.context,"Declined Clicked",Toast.LENGTH_LONG).show()
                }
                holder.doerBar.jesta_doers_accept.isEnabled = false
                holder.doerBar.jesta_doers_decline.isEnabled = false

                holder.doerBar.jesta_doers_accept.visibility = View.INVISIBLE
                holder.doerBar.jesta_doers_decline.visibility = View.INVISIBLE
                sysManager.onDeclineUser(doerList[position])
            }

            holder.doerBar.jesta_doers_chat.setOnClickListener {
                val roomDoer = sysManager.getUserByID(doerList[position].doerID)
                val poster = sysManager.getUserByID(mission.posterID)
                val chatRoom = ChatRoom(roomDoer, poster, mission)

                MainActivity.instance.fragNavController.pushFragment(
                    ChatFragment.newInstance(
                        chatRoom.id!!
                    )
                )
            }
        }


    }

    override fun getItemCount(): Int {
        return doerList.size
    }
}