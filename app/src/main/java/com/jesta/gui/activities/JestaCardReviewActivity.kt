package com.jesta.gui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R

class JestaCardReviewActivity : Fragment() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_preview_accept, container, false)

//        SysManager(activity as MainActivity)
//        val mission = intent.getParcelableExtra(extra) as Mission
//
//        jesta_preview_title.text = mission.title
//        jesta_preview_difficulty.text = mission.difficulty
//
//        ImageReqService.setImageFromUrl(jesta_card_preview_mission_image, mission.imageUrl)
//
//        jesta_preview_description.text = mission.description
//        jesta_preview_payment.text = mission.payment.toString()
//        jesta_preview_crew.text = mission.numOfPeople.toString()
//        jesta_preview_duration.text = mission.duration.toString()
//        jesta_preview_location.text = mission.location
//        jesta_preview_diamonds.text = mission.diamonds.toString()
//
//        jesta_preview_tag_1.text = mission.tags.first()
//        jesta_preview_tag_2.text = mission.tags[1]
//        jesta_preview_tag_3.text = mission.tags.last()
//
//        jesta_preview_accept_back_button.setOnClickListener {
//            finish()
//        }
//
//
//        jesta_preview_accept_button.setOnClickListener {
//
//            val sysManager = SysManager(activity as MainActivity)
//            var jestaAuthor = sysManager.getUserByID(mission.authorId)
//
//            if (jestaAuthor == null) {
//                jestaAuthor = sysManager.currentUserFromDB
//            }
//
//            sysManager.askTodoJestaForUser(mission).addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    // todo some error
//                }
//                Toast.makeText(
//                    activity as MainActivity,
//                    "A message was sent to " + jestaAuthor.displayName,
//                    Toast.LENGTH_LONG
//                ).show()
//
//            }

//        }
        return view
    }

}