package com.jesta.doJesta

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.util.Mission
import kotlinx.android.synthetic.main.activity_jesta_accept.*
import kotlinx.android.synthetic.main.jesta_preview.*

class JestaCardReviewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jesta_accept)

        val mission = intent.getParcelableExtra(extra) as Mission

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

        jesta_accept_back_button.setOnClickListener {
            finish()
        }


        jesta_accept_button.setOnClickListener {
            Toast.makeText(this@JestaCardReviewActivity,"Jesta Accepted!", Toast.LENGTH_LONG).show()
//            val intent = Intent(this@JestaCardReviewActivity, Main2Activity::class.java)
//            startActivity(intent)
        }

//        jesta_card_preview_button_fab.setOnClickListener{
//            finish()
//        }

    }
}