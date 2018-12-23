package com.jesta.doJesta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.chat.ChatActivity
import com.jesta.messaging.Topic
import com.jesta.messaging.TopicDescriptor
import com.jesta.util.ImageReq
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.jesta_preview_accept.*
import kotlinx.android.synthetic.main.jesta_preview.*

class JestaCardReviewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jesta_preview_accept)

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
        jesta_preview_tag_2.text = mission.tags.last()

        jesta_preview_accept_back_button.setOnClickListener {
            finish()
        }


        jesta_preview_accept_button.setOnClickListener {
//            Toast.makeText(this@JestaCardReviewActivity,"Jesta Accepted!", Toast.LENGTH_LONG).show()
//            val intent = Intent(this@JestaCardReviewActivity, ChatActivity::class.java)
//            startActivity(intent)

            val sysManager = SysManager(this@JestaCardReviewActivity)
            var currentUser = sysManager.currentUserFromDB

            var jestaAuthor = sysManager.getUserByID(mission.authorId);

            if (jestaAuthor == null) {
                jestaAuthor = currentUser; // todo remove this debug hack - when all missions will have jestaAuthor!
            }

            // create a new topic on which currentUser asking to do a mission
            val topic = Topic(TopicDescriptor.USER_INBOX, jestaAuthor, null)

            sysManager.sendMessageToTopic(topic, currentUser.displayName + " wants to do you a jesta: ", mission.title).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // todo some error
                }
                Toast.makeText(this@JestaCardReviewActivity,"A message was sent to " + jestaAuthor.displayName, Toast.LENGTH_LONG).show()

            }
        }
    }
}