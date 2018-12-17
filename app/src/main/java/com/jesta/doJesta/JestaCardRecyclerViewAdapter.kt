package com.jesta.doJesta

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jesta.R
import com.jesta.util.Mission
import kotlinx.android.synthetic.main.jesta_card.view.*

class JestaCardRecyclerViewAdapter internal constructor(
    private val postList: List<Mission>
) : RecyclerView.Adapter<JestaCardRecyclerViewAdapter.JestaCardViewHolder>() {

    inner class JestaCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var jestaCard: MaterialCardView = itemView.findViewById(R.id.jesta_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JestaCardViewHolder {
        val cardLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.jesta_card, parent, false)
        return JestaCardViewHolder(cardLayoutView)
    }

    override fun onBindViewHolder(holder: JestaCardViewHolder, position: Int) {
        if (position < postList.size) {
            val jestaPost = postList[position]
            ImageReq.setImageFromUrl(holder.jestaCard.jesta_card_image, jestaPost.imageUrl)
            holder.jestaCard.jesta_card_tag_difficulty.text = jestaPost.difficulty
            holder.jestaCard.jesta_card_tag_1.text = jestaPost.tags.first()
            holder.jestaCard.jesta_card_tag_2.text = jestaPost.tags.last()
            holder.jestaCard.jesta_card_total_reward.text = jestaPost.diamonds.toString()
        }

        holder.jestaCard.setOnClickListener {
            Toast.makeText(
                it.context,
                "Clicked card=$position",
                Toast.LENGTH_LONG
            )
                .show()
            val intent = Intent(it.context, JestaCardReviewActivity::class.java)
            intent.putExtra(JestaCardReviewActivity.extra, postList[position])
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}


