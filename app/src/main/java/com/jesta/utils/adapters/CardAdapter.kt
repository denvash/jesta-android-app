package com.jesta.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jesta.R
import com.jesta.data.IMAGE_DEFAULT_JESTA
import com.jesta.data.IMAGE_EMPTY
import com.jesta.data.MISSION_EMPTY_AUTHOR_IMAGE
import com.jesta.data.Mission
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.CardReviewFragment
import com.jesta.utils.db.SysManager
import com.jesta.utils.services.ImageReqService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.jesta_card.view.*

class CardAdapter internal constructor(
    private val postList: List<Mission>
) : RecyclerView.Adapter<CardAdapter.JestaCardViewHolder>() {

    private val mainInstance = MainActivity.instance
    private val sysManager = SysManager(MainActivity.instance)

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

            card.jesta_card_tags_layout.tagTypeface =
                    ResourcesCompat.getFont(MainActivity.instance, R.font.montserrat_light_italic)
            card.jesta_card_tags_layout.tags = mission.tags

            ImageReqService.setImageFromUrl(
                card.jesta_card_image,
                if (mission.imageUrl != IMAGE_EMPTY) mission.imageUrl else IMAGE_DEFAULT_JESTA
            )

            val posterAvatar = sysManager.getUserByID(mission.posterID).photoUrl
            if (posterAvatar != MISSION_EMPTY_AUTHOR_IMAGE) {
                Picasso.get().load(posterAvatar).noFade().into(card.jesta_card_avatar_icon)
            }
            card.jesta_card_title.text = mission.title
            card.jesta_card_difficulty.text = mission.difficulty

            card.jesta_card_diamonds.text = mission.diamonds.toString()
        }

        holder.jestaCard.setOnClickListener {
            Toast.makeText(it.context, "Clicked card=$position", Toast.LENGTH_LONG).show()
            mainInstance.fragNavController.pushFragment(CardReviewFragment.newInstance(mission))
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}


