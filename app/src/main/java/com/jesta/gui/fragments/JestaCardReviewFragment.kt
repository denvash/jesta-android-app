package com.jesta.gui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.BUNDLE_MISSION
import com.jesta.data.Mission
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.services.ImageReqService
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.fragment_jesta_card_preview.view.*
import kotlinx.android.synthetic.main.jesta_card_preview.view.*
import kotlinx.android.synthetic.main.jesta_main_activity.*

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

    private lateinit var mission : Mission

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getParcelable<Mission>(BUNDLE_MISSION)?.let {
            mission = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_jesta_card_preview, container, false)

        Log.d(TAG, "Accepted mission: $mission")

        view.jesta_preview_title.text = mission.title
        view.jesta_preview_difficulty.text = mission.difficulty

        ImageReqService.setImageFromUrl(view.jesta_card_preview_mission_image, mission.imageUrl)

//        Ion.with(view.jesta_card_preview_mission_image)
//            .placeholder(R.drawable.ic_jesta_default_image)
//            .load(mission.imageUrl)

        view.jesta_preview_description.text = mission.description
        view.jesta_preview_payment.text = mission.payment.toString()
        view.jesta_preview_crew.text = mission.numOfPeople.toString()
        view.jesta_preview_duration.text = mission.duration.toString()
        view.jesta_preview_location.text = mission.location
        view.jesta_preview_diamonds.text = mission.diamonds.toString()

        view.jesta_preview_tag_1.text = mission.tags.first()
        view.jesta_preview_tag_2.text = mission.tags[1]
        view.jesta_preview_tag_3.text = mission.tags.last()

        view.jesta_preview_button_back.setOnClickListener {
            MainActivity.instance.fragNavController.replaceFragment(DoJestaFragment())
            MainActivity.instance.jesta_bottom_navigation.visibility = View.VISIBLE
        }

        view.jesta_preview_accept_button.setOnClickListener {
//            val sysManager = SysManager(this@JestaCardReviewFragment)
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
//                    this@JestaCardReviewFragment,
//                    "A message was sent to " + jestaAuthor.displayName,
//                    Toast.LENGTH_LONG
//                ).show()
//
//            }
        }

        return view
    }

}