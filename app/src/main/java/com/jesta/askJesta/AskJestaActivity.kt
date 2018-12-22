package com.jesta.askJesta

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.doJesta.DoJestaActivity
import com.jesta.settings.SettingsActivity
import com.jesta.status.StatusActivity
import com.jesta.util.Mission
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_ask_jesta.*
import kotlinx.android.synthetic.main.frame_bottom_navigation_view.*
import kotlinx.android.synthetic.main.jesta_post.*
import java.util.*

class AskJestaActivity : AppCompatActivity() {

    private val RESULT_LOAD_IMAGE: Int = 1
    private var filePath: Uri? = null
    private var imageResult = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_jesta)

        jesta_post_title.filters = jesta_post_title.filters + InputFilter.AllCaps()
        jesta_post_location.filters = jesta_post_location.filters + InputFilter.AllCaps()
        jesta_post_button_browse.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
        }
        jesta_post_button_accept.setOnClickListener {

            val diamonds = when (jesta_post_difficulty.text.toString()) {
                "Easy" -> 10000
                "Medium" -> 20000
                else -> 3000
            }
            val sysManager = SysManager(this@AskJestaActivity)

            if (isInvalidInput()) {
                Toast.makeText(this@AskJestaActivity, "Fill all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            //val currentUser = sysManager.getCurrentUserFromDB()
            val jesta = Mission(
                id = UUID.randomUUID().toString(),
                title = jesta_post_title.text.toString(),
                difficulty = jesta_post_difficulty.text.toString(),
                description = jesta_post_description.text.toString(),
                imageUrl = imageResult,
                payment = jesta_post_payment.text.toString().toInt(),
                numOfPeople = jesta_post_num_of_people.text.toString().toInt(),
                duration = jesta_post_duration.text.toString().toInt(),
                location = jesta_post_location.text.toString(),
                diamonds = diamonds,
                tags = listOf(
                    jesta_preview_tag_1.text.toString(),
                    jesta_preview_tag_2.text.toString(),
                    jesta_preview_tag_3.text.toString()
                )
            )
            sysManager.setMissionOnDB(jesta)

            Toast.makeText(this@AskJestaActivity, "Jesta posted", Toast.LENGTH_LONG).show()
            val intent = Intent(this@AskJestaActivity, DoJestaActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        jesta_bottom_navigation.selectedItemId = R.id.nav_ask_jesta
        jesta_bottom_navigation.setOnNavigationItemSelectedListener {

            if (it.itemId == jesta_bottom_navigation.selectedItemId) return@setOnNavigationItemSelectedListener true

            val intent = when (it.itemId) {
                R.id.nav_do_jesta -> {
                    Intent(this@AskJestaActivity, DoJestaActivity::class.java)
                }
                R.id.nav_status -> {
                    Intent(this@AskJestaActivity, StatusActivity::class.java)
                }
                // Settings Activity
                else -> {
                    Intent(this@AskJestaActivity, SettingsActivity::class.java)
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
            true
        }
    }

    private fun isInvalidInput(): Boolean {
        return jesta_post_title.text.isNullOrEmpty() ||
                jesta_post_difficulty.text.isNullOrEmpty() ||
                jesta_post_description.text.isNullOrEmpty() ||
                jesta_post_payment.text.isNullOrEmpty() ||
                jesta_post_num_of_people.text.isNullOrEmpty() ||
                jesta_post_duration.text.isNullOrEmpty() ||
                jesta_post_location.text.isNullOrEmpty() ||
                jesta_preview_tag_1.text.isNullOrEmpty() ||
                jesta_preview_tag_2.text.isNullOrEmpty() ||
                jesta_preview_tag_3.text.isNullOrEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data

            jesta_post_preview_mission_image.setImageURI(filePath)

            val sysManager = SysManager(this@AskJestaActivity)
            val uploadAndGetUrl = sysManager.createDBTask(SysManager.DBTask.UPLOAD_FILE, filePath)

            // note: async function, therefore it is in the end of current function
            uploadAndGetUrl.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Todo error here! e.g. start ErrorActivity here
                    return@addOnCompleteListener
                }

                imageResult = task.result.toString()
            }

        }
    }
}
