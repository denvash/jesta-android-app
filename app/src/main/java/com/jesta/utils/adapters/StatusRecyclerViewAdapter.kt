package com.jesta.utils.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.data.Relation
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.activities.StatusReviewActivity
import kotlinx.android.synthetic.main.jesta_status_bar.view.*

class StatusRecyclerViewAdapter internal constructor(
    private val relatedMissions: List<Relation>,
    private val filteredMissions: List<Mission>
) : RecyclerView.Adapter<StatusRecyclerViewAdapter.StatusReviewViewHolder>() {

    inner class StatusReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var jestaStatus: MaterialCardView = itemView.findViewById(R.id.jesta_status_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusReviewViewHolder {
        val cardLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.jesta_status_bar, parent, false)
        return StatusReviewViewHolder(cardLayoutView)
    }

    override fun onBindViewHolder(holder: StatusReviewViewHolder, position: Int) {
        if (position < relatedMissions.size) {
            val jestaStatus = relatedMissions[position]
            holder.jestaStatus.jesta_status_bar_title.text = filteredMissions.find { it.id == jestaStatus.missionID }!!.title

            Log.i("filtered", filteredMissions.toString())
            // TODO: add counter to Jesta Status
//            holder.jestaStatus.jesta_status_count.text = jestaStatus.count.toString()
//            holder.jestaStatus.jesta_status_bar_count.text = jestaStatus.size.toString()
        }

        holder.jestaStatus.setOnClickListener {
            Toast.makeText(
                it.context,
                "Clicked status=$position",
                Toast.LENGTH_LONG
            )
                .show()
//            MainActivity.instance.fragNavController.clearStack()
//            val intent = Intent(it.context, StatusReviewActivity::class.java)
            // if poster 'postList[position]' containing list of doers
            // if doer 'postList[position]' containing list of 1 node
//            intent.putParcelableArrayListExtra(StatusReviewActivity.extra, postList[position])
//            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return relatedMissions.size
    }
}


