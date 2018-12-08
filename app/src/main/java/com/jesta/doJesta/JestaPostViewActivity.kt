package com.jesta.doJesta

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.util.Mission
import kotlinx.android.synthetic.main.activity_post_view.*
import kotlinx.android.synthetic.main.jesta_card.*

class JestaPostViewActivity : AppCompatActivity() {

    companion object {
        const val extra = "jesta-post-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)

        val mission = intent.getParcelableExtra(extra) as Mission
        jesta_card_description.text = mission.description
        jesta_card_difficulty.text = mission.difficulty
        ImageReq.setImageFromUrl(jesta_card_image, mission.imageUrl)

        chip_price.text = mission.payment.toString()
        chip_num_of_people.text = mission.numOfPeople.toString()
        chip_duration.text = mission.duration.toString()
        chip_location.text = mission.location

        jesta_post_button_accept.setOnClickListener {
            Toast.makeText(this@JestaPostViewActivity,"Jesta Accepted!",Toast.LENGTH_LONG).show()
        }

        jesta_post_fab_back.setOnClickListener{
            finish()
        }
    }
}