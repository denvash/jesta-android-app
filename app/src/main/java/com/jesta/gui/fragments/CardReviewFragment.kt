package com.jesta.gui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.*
import com.jesta.data.chat.ChatManager
import com.jesta.data.chat.ChatRoom
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.jesta.utils.services.ImageReqService
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.fragment_card_preview.view.*
import kotlinx.android.synthetic.main.jesta_card_preview.view.*
import kotlinx.android.synthetic.main.jesta_main_activity.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*

class CardReviewFragment : Fragment() {
    companion object {
        private val TAG = CardReviewFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(mission: Mission) = CardReviewFragment().apply {
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

        ImageReqService.setImageFromUrl(
            view.jesta_card_preview_mission_image,
            if (mission.imageUrl != IMAGE_EMPTY) mission.imageUrl else IMAGE_DEFAULT_JESTA
        )

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

            sysManager.getUserRelations(sysManager.currentUserFromDB.id).addOnCompleteListener { getRelationsTask ->
                val result: List<Relation> = (getRelationsTask.result as List<*>).filterIsInstance<Relation>()

                val currMissionRelation = result.find { relation -> relation.missionID == mission.id }

                if (mission.posterID == sysManager.currentUserFromDB.id) {
                    Alerter.create(MainActivity.instance)
                        .setTitle("You are a Doer already! \uD83D\uDCAA")
                        .setText("Check out the Status tab!")
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_jesta_diamond_normal)
                        .show()
                    return@addOnCompleteListener
                } else if (currMissionRelation != null && currMissionRelation.doerID == sysManager.currentUserFromDB.id) {
                    Alerter.create(MainActivity.instance)
                        .setTitle("Don't be Silly! \uD83D\uDE1C")
                        .setText("You can't do your own Jesta!")
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_jesta_diamond_normal)
                        .show()
                    return@addOnCompleteListener
                } else {
                    Alerter.create(MainActivity.instance)
                        .setTitle("You Offered to Do a Jesta!\uD83E\uDD19")
                        .setText(
                            "Great Job! Check out the Status tab! " +
                                    "The poster has been notified!"
                        )
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_jesta_diamond_normal)
                        .show()

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
                    }
                    // note subscribeToChatRoom must be after askTodoJestaForUser as it relays on relation id
                    // and relation set only on askTodoJesta
                    sysManager.askTodoJestaForUser(mission).addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            // todo some error
                            return@addOnCompleteListener
                        }
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
                        }
                    }
                }
                MainActivity.instance.fragNavController.popFragment()
                MainActivity.instance.fragNavController.switchTab(INDEX_STATUS)
                MainActivity.instance.jesta_bottom_navigation.selectedItemId = R.id.nav_status
            }
        }

        OverScrollDecoratorHelper.setUpOverScroll(view.jesta_preview_nested_scroll_view)
        return view
    }

}