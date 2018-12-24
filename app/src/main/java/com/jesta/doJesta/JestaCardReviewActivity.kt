package com.jesta.doJesta

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.util.ImageReq
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.jesta_preview.*
import kotlinx.android.synthetic.main.jesta_preview_accept.*

class JestaCardReviewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jesta_preview_accept)

        SysManager(this@JestaCardReviewActivity)
        val mission = intent.getParcelableExtra(extra) as Mission

        jesta_preview_title.text = mission.title
        jesta_preview_difficulty.text = mission.difficulty

        ImageReq.setImageFromUrl(jesta_card_preview_mission_image, mission.imageUrl)

        jesta_preview_description.text = mission.description
        jesta_preview_payment.text = mission.payment.toString()
        jesta_preview_crew.text = mission.numOfPeople.toString()
        jesta_preview_duration.text = mission.duration.toString()
        jesta_preview_location.text = mission.location
        jesta_preview_diamonds.text = mission.diamonds.toString()

        jesta_preview_tag_1.text = mission.tags.first()
        jesta_preview_tag_2.text = mission.tags[1]
        jesta_preview_tag_3.text = mission.tags.last()

        jesta_preview_accept_back_button.setOnClickListener {
            finish()
        }


        jesta_preview_accept_button.setOnClickListener {

            val sysManager = SysManager(this@JestaCardReviewActivity)
            var jestaAuthor = sysManager.getUserByID(mission.authorId)

            if (jestaAuthor == null) {
                jestaAuthor = sysManager.currentUserFromDB
            }

            sysManager.askTodoJestaForUser(mission).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // todo some error
                }
                Toast.makeText(
                    this@JestaCardReviewActivity,
                    "A message was sent to " + jestaAuthor.displayName,
                    Toast.LENGTH_LONG
                ).show()

            }

        }
    }
}