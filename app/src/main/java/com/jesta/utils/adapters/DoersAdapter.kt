package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.jesta.R
import com.jesta.data.MISSION_EMPTY_AUTHOR_IMAGE
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.JestaCardReviewFragment
import com.jesta.utils.db.SysManager
import com.jesta.utils.services.ImageReqService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.jesta_card.view.*
import kotlinx.android.synthetic.main.jesta_doers.view.*

class DoersAdapter internal constructor(
    private val doerList: List<String>
) : RecyclerView.Adapter<DoersAdapter.JestaCardViewHolder>() {

    private val mainInstance = MainActivity.instance
    private val sysManager = SysManager(mainInstance)

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
        }

        holder.doerBar.jesta_doers_accept.setOnClickListener {
            Toast.makeText(it.context,"Accept Clicked",Toast.LENGTH_LONG).show()
        }

        holder.doerBar.jesta_doers_decline.setOnClickListener {
            Toast.makeText(it.context,"Declined Clicked",Toast.LENGTH_LONG).show()
        }

        holder.doerBar.jesta_doers_chat.setOnClickListener {
            Toast.makeText(it.context,"Chat Clicked",Toast.LENGTH_LONG).show()
        }



    }

    override fun getItemCount(): Int {
        return doerList.size
    }


}


