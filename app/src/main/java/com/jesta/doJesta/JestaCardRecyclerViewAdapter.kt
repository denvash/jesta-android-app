package com.jesta.doJesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import com.jesta.R

class JestaCardRecyclerViewAdapter internal constructor(
    private val productList: List<JestaCard>
) : RecyclerView.Adapter<JestaCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JestaCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.jesta_card, parent, false)
        return JestaCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: JestaCardViewHolder, position: Int) {
        if (position < productList.size) {
            val jestaCard = productList[position]
            holder.jestaDifficulty.text = jestaCard.difficulty
            holder.jestaDescription.text = jestaCard.description
            ImageReq.setImageFromUrl(holder.jestaImage, jestaCard.url)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }


}

class JestaCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var jestaImage: NetworkImageView = itemView.findViewById(R.id.jesta_card_image)
    var jestaDifficulty: TextView = itemView.findViewById(R.id.jesta_card_difficulty)
    var jestaDescription: TextView = itemView.findViewById(R.id.jesta_card_description)
}
