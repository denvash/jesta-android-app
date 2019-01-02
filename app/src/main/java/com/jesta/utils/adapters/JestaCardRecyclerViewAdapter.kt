package com.jesta.utils.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jesta.R
import com.jesta.data.MISSION_EMPTY_AUTHOR_IMAGE
import com.jesta.data.Mission
import com.jesta.data.USER_EMPTY_PHOTO
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.JestaCardReviewFragment
import com.jesta.utils.services.ImageReqService
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.jesta_card.view.*
import kotlinx.android.synthetic.main.jesta_card_preview.view.*
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

            card.jesta_card_tags_layout.tagTypeface =
                    ResourcesCompat.getFont(MainActivity.instance, R.font.montserrat_light_italic)
            card.jesta_card_tags_layout.tags = mission.tags

            ImageReqService.setImageFromUrl(card.jesta_card_image, mission.imageUrl)

            if (mission.authorImage != MISSION_EMPTY_AUTHOR_IMAGE) {
                card.jesta_card_avatar_icon.setImageURI(Uri.parse(mission.authorImage))
            }
            card.jesta_card_title.text = mission.title
            card.jesta_card_difficulty.text = mission.difficulty

            card.jesta_card_diamonds.text = mission.diamonds.toString()
        }

        holder.jestaCard.setOnClickListener {
            Toast.makeText(it.context,"Clicked card=$position",Toast.LENGTH_LONG).show()


//            MainActivity.instance.jesta_main_activity_relative_layout.visibility = View.INVISIBLE
//            val layoutParams = MainActivity.instance.jesta_bottom_navigation.layoutParams
//            mainInstance.fragNavController.replaceFragment(JestaCardReviewFragment.newInstance(mission))
            mainInstance.fragNavController.pushFragment(JestaCardReviewFragment.newInstance(mission))

//            val decorView = MainActivity.instance.window.decorView
//            val uiOptions =
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            decorView.systemUiVisibility = uiOptions

//            layoutParams.height = 0
//            MainActivity.instance.jesta_bottom_navigation.layoutParams = layoutParams

        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}


