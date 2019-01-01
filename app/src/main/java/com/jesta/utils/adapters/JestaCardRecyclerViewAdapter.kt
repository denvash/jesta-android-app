package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.JestaCardReviewFragment
import com.jesta.utils.services.ImageReqService
import kotlinx.android.synthetic.main.jesta_card.view.*
import kotlinx.android.synthetic.main.jesta_main_activity.*

class JestaCardRecyclerViewAdapter internal constructor(
    private val postList: List<Mission>
) : RecyclerView.Adapter<JestaCardRecyclerViewAdapter.JestaCardViewHolder>() {

    private val mainInstance = MainActivity.instance

    inner class JestaCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var jestaCard: MaterialCardView = itemView.findViewById(R.id.jesta_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JestaCardViewHolder {
        val cardLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.jesta_card, parent, false)
        return JestaCardViewHolder(cardLayoutView)
    }

    override fun onBindViewHolder(holder: JestaCardViewHolder, position: Int) {
        val mission = postList[position]

        if (position < postList.size) {
            val card = holder.jestaCard
            ImageReqService.setImageFromUrl(card.jesta_card_image, mission.imageUrl)

            card.jesta_card_title.text = mission.title
            card.jesta_card_difficulty.text = mission.difficulty
            card.jesta_card_tag_1.text = mission.tags.first()
            card.jesta_card_tag_2.text = mission.tags[1]
            card.jesta_card_tag_3.text = mission.tags.last()
            card.jesta_card_diamonds.text = mission.diamonds.toString()
        }

        holder.jestaCard.setOnClickListener {
            Toast.makeText(it.context,"Clicked card=$position",Toast.LENGTH_LONG).show()
            mainInstance.fragNavController.replaceFragment(JestaCardReviewFragment.newInstance(mission))
            MainActivity.instance.jesta_bottom_navigation.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}


