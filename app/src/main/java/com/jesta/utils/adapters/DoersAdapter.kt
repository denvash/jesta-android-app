package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jesta.R
import com.jesta.data.MISSION_EMPTY_AUTHOR_IMAGE
import com.jesta.data.Mission
import com.jesta.data.Relation
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.jesta_doers.view.*
import kotlinx.android.synthetic.main.jesta_status.view.*
import kotlin.random.Random

class DoersAdapter internal constructor(
    private val doerList: List<String>,
    private val relation: Relation,
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
        val doer = sysManager.getUserByID(doerList[position])
        if (position < doerList.size) {
            val bar = holder.doerBar
            bar.jesta_doers_name.text = doer.displayName
            bar.jesta_doers_diamond_amount.text = Random.nextInt(1, 40000).toString()
        }

        if (doer.photoUrl != MISSION_EMPTY_AUTHOR_IMAGE) {
            Picasso.get().load(doer.photoUrl).noFade().into(holder.doerBar.jesta_doers_avatar)
        }

        holder.doerBar.jesta_doers_accept.setOnClickListener {
            Toast.makeText(it.context,"Accept Clicked",Toast.LENGTH_LONG).show()
            sysManager.onAcceptDoer(relation,mission)
        }

        holder.doerBar.jesta_doers_decline.setOnClickListener {
            Toast.makeText(it.context,"Declined Clicked",Toast.LENGTH_LONG).show()
            sysManager.onDeclineUser(relation)
        }

        holder.doerBar.jesta_doers_chat.setOnClickListener {
            Toast.makeText(it.context,"Chat Clicked",Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return doerList.size
    }
}