package com.jesta.doJesta

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.util.Mission
import kotlinx.android.synthetic.main.activity_card_review.*
import kotlinx.android.synthetic.main.jesta_card_description.*
import kotlinx.android.synthetic.main.jesta_card_preview.*

class JestaCardReviewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-card-review-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_review)

        val mission = intent.getParcelableExtra(extra) as Mission

        jesta_card_description_tag_difficulty.text = mission.difficulty
        ImageReq.setImageFromUrl(jesta_card_preview_mission_image, mission.imageUrl)

        jesta_card_description_main_text.text = mission.description
        jesta_card_preview_chip_payment.text = mission.payment.toString()
        jesta_card_preview_chip_crew.text = mission.numOfPeople.toString()
        jesta_card_preview_chip_duration.text = mission.duration.toString()
        jesta_card_preview_chip_location.text = mission.location
        jesta_card_preview_chip_diamonds.text = mission.diamonds.toString()

        jesta_card_description_tag_1.text = mission.tags.first()
        jesta_card_description_tag_2.text = mission.tags.last()

        jesta_card_review_button_accept.setOnClickListener {
            Toast.makeText(this@JestaCardReviewActivity,"Jesta Accepted!", Toast.LENGTH_LONG).show()
        }

        jesta_card_preview_button_fab.setOnClickListener{
            finish()
        }

    }
}