package com.jesta.gui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.*
import com.jesta.gui.activities.MainActivity
import com.jesta.data.chat.ChatManager
import com.jesta.data.chat.ChatRoom
import com.jesta.utils.db.SysManager
import com.jesta.utils.services.ImageReqService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_card_preview.view.*
import kotlinx.android.synthetic.main.jesta_card_preview.view.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*

class JestaCardReviewFragment : Fragment() {
    companion object {
        private val TAG = JestaCardReviewFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(mission: Mission) = JestaCardReviewFragment().apply {
            arguments = Bundle().apply {
                putParcelable(BUNDLE_MISSION, mission)
            }
        }
    }

    private val sysManager = SysManager(this)
    private lateinit var mission: Mission

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<Mission>(BUNDLE_MISSION)?.let {
            mission = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_card_preview, container, false)


        Log.d(TAG, "Accepted mission: $mission")

        val posterAvatar = sysManager.getUserByID(mission.posterID).photoUrl
        if (posterAvatar != MISSION_EMPTY_AUTHOR_IMAGE) {
            Picasso.get().load(posterAvatar).noFade().into(view.jesta_preview_avatar_icon)
        }

        view.jesta_preview_title.text = mission.title
        view.jesta_preview_difficulty.text = mission.difficulty

        ImageReqService.setImageFromUrl(view.jesta_card_preview_mission_image, mission.imageUrl)

        view.jesta_preview_description.text = mission.description
        view.jesta_preview_payment.text = mission.payment.toString()
        view.jesta_preview_crew.text = mission.numOfPeople.toString()
        view.jesta_preview_duration.text = mission.duration.toString()
        view.jesta_preview_location.text = mission.location
        view.jesta_preview_diamonds.text = mission.diamonds.toString()

        view.jesta_card_preview_tags_layout.tagTypeface =
                ResourcesCompat.getFont(MainActivity.instance, R.font.montserrat_light_italic)
        view.jesta_card_preview_tags_layout.tags = mission.tags

        view.jesta_preview_button_back.setOnClickListener {
            MainActivity.instance.fragNavController.popFragment()
        }

        view.jesta_preview_accept_button.setOnClickListener {

            if (mission.posterID == sysManager.currentUserFromDB.id) {
                Toast.makeText(MainActivity.instance,"You can't do your own post!",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sysManager.getUserRelations(sysManager.currentUserFromDB.id).addOnCompleteListener { getRelationsTask ->
                val result: List<Relation> = (getRelationsTask.result as List<*>).filterIsInstance<Relation>()

                val currMissionRelation = result.find { relation -> relation.missionID == mission.id }

                if (currMissionRelation != null && currMissionRelation.doerID == sysManager.currentUserFromDB.id) {
                    Toast.makeText(MainActivity.instance,"You already Assigned as a Doer",Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                val relation = Relation(
                    id = UUID.randomUUID().toString(),
                    doerID = sysManager.currentUserFromDB.id,
                    posterID = mission.posterID,
                    missionID = mission.id,
                    status = RELATION_STATUS_INIT
                )
                sysManager.setRelationOnDB(relation)


                // subscribe doer to chat room
                val doer = sysManager.currentUserFromDB
                val poster = sysManager.getUserByID(mission.posterID)
                val chatRoom = ChatRoom(doer, poster, mission)
                val chatManager = ChatManager()
                chatManager.subscribeToChatRoom(chatRoom).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // todo some error
                        return@addOnCompleteListener
                    }
                    MainActivity.instance.fragNavController.popFragment()
                }

            }
        }

        OverScrollDecoratorHelper.setUpOverScroll(view.jesta_preview_nested_scroll_view)
        return view
    }

}