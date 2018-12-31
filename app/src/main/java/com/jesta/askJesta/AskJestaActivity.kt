package com.jesta.askJesta

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.lujun.androidtagview.TagView
import com.jesta.R
import com.jesta.doJesta.DoJestaActivity
import com.jesta.settings.SettingsActivity
import com.jesta.status.StatusActivity
import com.jesta.util.Mission
import com.jesta.util.SysManager
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.abc_activity_chooser_view_list_item.view.*
import kotlinx.android.synthetic.main.activity_ask_jesta.*
import kotlinx.android.synthetic.main.frame_bottom_navigation_view.*
import kotlinx.android.synthetic.main.jesta_post.*
import java.io.File
import java.util.*


class AskJestaActivity : AppCompatActivity() {

    private val RESULT_LOAD_IMAGE: Int = 1
    private val REQUEST_STORAGE_PERMISSION = 100
    private var filePath: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_jesta)

        jesta_post_tag_layout.tags = listOf("Heavy", "Help", "Now")

        btn_add_tag.setOnClickListener {
            if (!text_tag.text.isNullOrEmpty()) {
                jesta_post_tag_layout.addTag(text_tag.text.toString())
            }
            text_tag.text = null
        }

        jesta_post_tag_layout.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                Toast.makeText(
                    this@AskJestaActivity, "click-position:$position, text:$text",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onTagLongClick(position: Int, text: String) {
                val dialog = AlertDialog.Builder(this@AskJestaActivity)
                    .setTitle("long click")
                    .setMessage("You will delete this tag!")
                    .setPositiveButton("Delete") { _, _ ->
                        if (position < jesta_post_tag_layout.childCount) {
                            jesta_post_tag_layout.removeTag(position)
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                    .create()
                dialog.show()
            }

            override fun onSelectedTagDrag(position: Int, text: String) {}

            override fun onTagCrossClick(position: Int) {
                jesta_post_tag_layout.removeTag(position)
                Toast.makeText(
                    this@AskJestaActivity, "Click TagView cross! position = $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        jesta_post_title.filters = jesta_post_title.filters + InputFilter.AllCaps()
        jesta_post_location.filters = jesta_post_location.filters + InputFilter.AllCaps()
        jesta_post_button_browse.setOnClickListener {
            requestPermission(this)
        }
        jesta_post_button_accept.setOnClickListener {

            if (isInvalidInput()) {
                Toast.makeText(this@AskJestaActivity, "Fill all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val diamonds = when (jesta_post_difficulty.text.toString()) {
                "Easy" -> 10000
                "Medium" -> 20000
                else -> 3000
            }

            val sysManager = SysManager(this@AskJestaActivity)

            val jesta = Mission(
                authorId = sysManager.currentUserFromDB.id,
                id = UUID.randomUUID().toString(),
                title = jesta_post_title.text.toString(),
                difficulty = jesta_post_difficulty.text.toString(),
                description = jesta_post_description.text.toString(),
                imageUrl = "",
                payment = jesta_post_payment.text.toString().toInt(),
                numOfPeople = jesta_post_num_of_people.text.toString().toInt(),
                duration = jesta_post_duration.text.toString().toInt(),
                location = jesta_post_location.text.toString(),
                diamonds = diamonds,
                tags = jesta_post_tag_layout.tags
            )

            uploadedImageToDB(jesta, sysManager)

            Toast.makeText(this@AskJestaActivity, "Jesta Sent to DB", Toast.LENGTH_LONG).show()
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

    private fun uploadedImageToDB(jesta: Mission, sysManager: SysManager) {

        val uploadAndGetUrl = sysManager.createDBTask(SysManager.DBTask.UPLOAD_FILE, filePath)
        // note: async function, therefore it is in the end of current function
        uploadAndGetUrl.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                System.err.println(task.exception)
                Toast.makeText(
                    this@AskJestaActivity,
                    "Quota has been exceeded for this project.",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnCompleteListener
            } else {
                Toast.makeText(
                    this@AskJestaActivity,
                    "Image uploaded to Storage",
                    Toast.LENGTH_LONG
                ).show()
                jesta.imageUrl = task.result.toString()
            }
            sysManager.setMissionOnDB(jesta)
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
                jesta_post_tag_layout.tags.size < 3
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            val cursor =
                MediaStore.Images.Media.query(contentResolver, data.data, arrayOf(MediaStore.Images.Media.DATA))
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val comp = Compressor(this)
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
