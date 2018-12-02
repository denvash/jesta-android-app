package com.jesta.doJesta

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jesta.R
import kotlinx.android.synthetic.main.jesta_card.view.*

class JestaCardRecyclerViewAdapter internal constructor(
    private val productList: List<JestaCard>
) : RecyclerView.Adapter<JestaCardRecyclerViewAdapter.JestaCardViewHolder>() {

    inner class JestaCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var jestaCard: MaterialCardView = itemView.findViewById(R.id.jesta_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JestaCardViewHolder {
        val cardLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.jesta_card, parent, false)
        return JestaCardViewHolder(cardLayoutView)
    }

    override fun onBindViewHolder(holder: JestaCardViewHolder, position: Int) {
        if (position < productList.size) {
            val jestaCard = productList[position]
            holder.jestaCard.jesta_card_difficulty.text = jestaCard.difficulty
            holder.jestaCard.jesta_card_description.text = jestaCard.description
            ImageReq.setImageFromUrl(holder.jestaCard.jesta_card_image, jestaCard.url)
        }

        holder.jestaCard.setOnClickListener {
            Toast.makeText(
                it.context,
                "Clicked card=" + holder.jestaCard.jesta_card_difficulty.text.toString(),
                Toast.LENGTH_LONG
            )
                .show()
            val intent = Intent(it.context, JestaPostActivity::class.java)
            intent.putExtra("description", "from clicked")
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }


}


