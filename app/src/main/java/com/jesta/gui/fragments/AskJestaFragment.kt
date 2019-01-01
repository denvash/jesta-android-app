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
import androidx.fragment.app.Fragment
import com.jesta.gui.activities.INDEX_DO_JESTA
import com.jesta.gui.activities.MainActivity
import com.jesta.R
import com.jesta.data.Mission
import com.jesta.utils.db.SysManager
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_ask_jesta.view.*
import kotlinx.android.synthetic.main.jesta_post.*
import kotlinx.android.synthetic.main.jesta_post.view.*
import java.io.File
import java.util.*


class AskJestaFragment : Fragment() {

    private val RESULT_LOAD_IMAGE: Int = 1
    private val REQUEST_STORAGE_PERMISSION = 100
    private var filePath: Uri? = null

    private val sysManager = SysManager(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ask_jesta, container, false)

        view.jesta_post_title.filters = view.jesta_post_title.filters + InputFilter.AllCaps()
        view.jesta_post_location.filters = view.jesta_post_location.filters + InputFilter.AllCaps()
        view.jesta_post_button_browse.setOnClickListener {
            requestPermission(MainActivity.instance)
        }

        view.jesta_post_button_accept.setOnClickListener {

            if (isInvalidInput()) {
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val diamonds = when (view.jesta_post_difficulty.text.toString()) {
                "Easy" -> 10000
                "Medium" -> 20000
                else -> 3000
            }

            val jesta = Mission(
                authorId = sysManager.currentUserFromDB.id,
                id = UUID.randomUUID().toString(),
                title = view.jesta_post_title.text.toString(),
                difficulty = view.jesta_post_difficulty.text.toString(),
                description = view.jesta_post_description.text.toString(),
                imageUrl = "",
                payment = view.jesta_post_payment.text.toString().toInt(),
                numOfPeople = view.jesta_post_num_of_people.text.toString().toInt(),
                duration = view.jesta_post_duration.text.toString().toInt(),
                location = view.jesta_post_location.text.toString(),
                diamonds = diamonds,
                tags = listOf("Tag1", "Tag2", "Tag3")
            )

            uploadedImageToDB(jesta, sysManager)

//            sysManager.setMissionOnDB(jesta,isUploadedImageToDB)

            Toast.makeText(context, "Jesta Sent to DB", Toast.LENGTH_LONG).show()

            MainActivity.instance.fragNavController.switchTab(INDEX_DO_JESTA)
        }

        return view
    }


    private fun uploadedImageToDB(jesta: Mission, sysManager: SysManager) {

        val uploadAndGetUrl = sysManager.createDBTask(SysManager.DBTask.UPLOAD_FILE, filePath)
        // note: async function, therefore it is in the end of current function
        uploadAndGetUrl.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                System.err.println(task.exception)
                Toast.makeText(context, "Quota has been exceeded for this project.", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            } else {
                Toast.makeText(context, "Image uploaded to Storage", Toast.LENGTH_LONG).show()
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
                jesta_preview_tag_1.text.isNullOrEmpty() ||
                jesta_preview_tag_2.text.isNullOrEmpty() ||
                jesta_preview_tag_3.text.isNullOrEmpty()
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
