package com.jesta.gui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import co.lujun.androidtagview.TagView
import com.jesta.R
import com.jesta.data.*
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.tapadoo.alerter.Alerter
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_ask_jesta.view.*
import kotlinx.android.synthetic.main.jesta_main_activity.*
import kotlinx.android.synthetic.main.jesta_post.*
import kotlinx.android.synthetic.main.jesta_post.view.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog
import java.io.File
import java.util.*


class AskJestaFragment : Fragment() {


    private var filePath: Uri? = null

    private val sysManager = MainActivity.instance.sysManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ask_jesta, container, false)
        if (sysManager.currentUserFromDB == null) return view

        setLayoutOnLastMission(view, sysManager.currentUserFromDB.lastMission)

        val ints = MainActivity.instance
        view.jesta_post_time_start.setOnClickListener {
            LinearTimePickerDialog.Builder.with(ints)
                .setDialogBackgroundColor(ContextCompat.getColor(ints, R.color.semiWhite))
                .setPickerBackgroundColor(ContextCompat.getColor(ints, R.color.semiWhite))
                .setLineColor(ContextCompat.getColor(ints, R.color.black))
                .setTextColor(ContextCompat.getColor(ints, R.color.colorPrimary))
                .setTextBackgroundColor(ContextCompat.getColor(ints, R.color.semiTransparentGrey))
                .setButtonColor(ContextCompat.getColor(ints, R.color.colorPrimary))
                .build()
                .show()

        }


        view.jesta_post_title.filters = view.jesta_post_title.filters + InputFilter.AllCaps()

        view.jesta_post_tag_layout.tagTypeface =
                ResourcesCompat.getFont(MainActivity.instance, R.font.montserrat_light_italic)

        view.jesta_post_button_add_tag.setOnClickListener {
            if (jesta_post_tag_layout.tags.size == 5) jesta_post_button_add_tag.isEnabled = false
            if (!view.text_tag.text.isNullOrEmpty()) {
                view.jesta_post_tag_layout.addTag(text_tag.text.toString())
            }
            view.text_tag.text = null
        }

        view.jesta_post_tag_layout.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                Toast.makeText(
                    MainActivity.instance, "click-position:$position, text:$text",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onTagLongClick(position: Int, text: String) {}

            override fun onSelectedTagDrag(position: Int, text: String) {}

            override fun onTagCrossClick(position: Int) {
                view.jesta_post_tag_layout.removeTag(position)
                view.jesta_post_button_add_tag.isEnabled = true
            }
        })

        view.jesta_post_difficulty.attachDataSource(listOf(DIFFICULTY_EASY, DIFFICULTY_MEDIUM, DIFFICULTY_HARD))

        view.jesta_post_location.filters = view.jesta_post_location.filters + InputFilter.AllCaps()
        view.jesta_post_button_browse.setOnClickListener {
            requestPermission(MainActivity.instance)
        }

        view.jesta_post_button_accept.setOnClickListener {

            if (isInvalidInput()) {
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val jesta = Mission(
                posterID = sysManager.currentUserFromDB.id,
                id = UUID.randomUUID().toString(),
                title = view.jesta_post_title.text.toString(),
                difficulty = view.jesta_post_difficulty.text.toString(),
                description = view.jesta_post_description.text.toString(),
                imageUrl = IMAGE_EMPTY,
                payment = view.jesta_post_payment.text.toString().toInt(),
                numOfPeople = view.jesta_post_num_of_people.value,
                duration = view.jesta_post_duration.progress,
                location = view.jesta_post_location.text.toString(),
                diamonds = view.jesta_post_fluid_slider.bubbleText?.toInt()!!,
                tags = view.jesta_post_tag_layout.tags
            )

            val rel = Relation(
                id = UUID.randomUUID().toString(),
                missionID = jesta.id,
                posterID = jesta.posterID,
                doerID = RELATION_EMPTY_ID,
                status = RELATION_STATUS_INIT
            )

            uploadMissionToDB(jesta, rel, sysManager)
            Toast.makeText(context, "Jesta Sent to DB", Toast.LENGTH_LONG).show()

            val userCacheMission = sysManager.currentUserFromDB
            userCacheMission.lastMission = jesta.copy()

            sysManager.setUserOnDB(userCacheMission)
            sysManager.createDBTask(SysManager.DBTask.RELOAD_USERS)
                .addOnCompleteListener {
                    Toast.makeText(context, "User Last Mission updated", Toast.LENGTH_LONG).show()
                }

            Alerter.create(MainActivity.instance)
                .setTitle("Nice one! You posted a Jesta! \uD83E\uDD20")
                .setText("Remember to refresh the Jestas list!")
                .setBackgroundColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_jesta_diamond_normal)
                .show()

            MainActivity.instance.fragNavController.switchTab(INDEX_DO_JESTA)
            MainActivity.instance.jesta_bottom_navigation.selectedItemId = R.id.nav_do_jesta
        }

        val max = 10000
        val min = 100
        val total = max - min
        val slider = view.jesta_post_fluid_slider
        slider.positionListener = { pos -> slider.bubbleText = "${min + (total * pos).toInt()}" }
        slider.position = 0.3f
        slider.startText = "$min"
//        slider.endText = "$max"

        OverScrollDecoratorHelper.setUpOverScroll(view.jesta_post_view_nested_scroll_view)


        return view
    }

    private fun setLayoutOnLastMission(view: View, mission: Mission) {
        if (mission.id == MISSION_EMPTY_ID || !mission.isAvailable) return

        view.jesta_post_title.setText(mission.title)
        view.jesta_post_description.setText(mission.description)
        view.jesta_post_tag_layout.tags = mission.tags
        view.jesta_post_payment.setText(mission.payment.toString())
        view.jesta_post_location.setText(mission.location)
        view.jesta_post_num_of_people.value = mission.numOfPeople
        view.jesta_post_duration.setProgress(mission.duration.toFloat())
        view.jesta_post_difficulty.selectedIndex = when (mission.difficulty) {
            DIFFICULTY_EASY -> 0
            DIFFICULTY_MEDIUM -> 1
            else -> 2
        }
    }

    private fun uploadMissionToDB(jesta: Mission, rel: Relation, sysManager: SysManager) {
        if (filePath == null) {
            jesta.imageUrl = IMAGE_DEFAULT_JESTA
            sysManager.setMissionOnDB(jesta)
            sysManager.setRelationOnDB(rel)
        } else {
            sysManager.createDBTask(SysManager.DBTask.UPLOAD_FILE, filePath)
                // note: async function, therefore it is in the end of current function
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        System.err.println(task.exception)
                        Toast.makeText(context, "Quota has been exceeded for this project.", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    } else {
                        Toast.makeText(context, "Image uploaded to Storage", Toast.LENGTH_LONG).show()
                        jesta.imageUrl = task.result.toString()
                    }
                    sysManager.setMissionOnDB(jesta)
                    sysManager.setRelationOnDB(rel)
                }
        }
    }

    private fun isInvalidInput(): Boolean {
        return jesta_post_title.text.isNullOrEmpty() ||
                jesta_post_difficulty.text.isNullOrEmpty() ||
                jesta_post_description.text.isNullOrEmpty() ||
                jesta_post_payment.text.isNullOrEmpty() ||
                jesta_post_location.text.isNullOrEmpty()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            val cursor =
                MediaStore.Images.Media.query(
                    MainActivity.instance.contentResolver,
                    data.data,
                    arrayOf(MediaStore.Images.Media.DATA)
                )
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val comp = Compressor(context)
            val file = comp.compressToFile(File(cursor.getString(idx)))

            filePath = Uri.fromFile(file)
            jesta_post_preview_mission_image.setImageURI(filePath)
        }
    }

    fun requestPermission(activity: Activity) {

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
            } else {
                //Yeah! I want both block to do the same thing, you can write your own logic, but this works for me.
                ActivityCompat.requestPermissions(
                    activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
            }
        } else {
            //Permission Granted, lets go pick photo
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
        }
    }
}
