package com.jesta.askJesta

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jesta.MainActivity
import com.jesta.R
import kotlinx.android.synthetic.main.jesta_post.*


class AskJestaFragment : Fragment() {

    private val RESULT_LOAD_IMAGE: Int = 1
    private val REQUEST_STORAGE_PERMISSION = 100
    private var filePath: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ask_jesta, container, false)

//        jesta_post_title.filters = jesta_post_title.filters + InputFilter.AllCaps()
//        jesta_post_location.filters = jesta_post_location.filters + InputFilter.AllCaps()
//        jesta_post_button_browse.setOnClickListener {
//            requestPermission(MainActivity())
//        }

//        jesta_post_button_accept.setOnClickListener {
//
//            if (isInvalidInput()) {
//                Toast.makeText(this@AskJestaFragment, "Fill all fields", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
//
//            val diamonds = when (jesta_post_difficulty.text.toString()) {
//                "Easy" -> 10000
//                "Medium" -> 20000
//                else -> 3000
//            }
//
//            val sysManager = SysManager(this@AskJestaFragment)
//
//            val jesta = Mission(
//                authorId = sysManager.currentUserFromDB.id,
//                id = UUID.randomUUID().toString(),
//                title = jesta_post_title.text.toString(),
//                difficulty = jesta_post_difficulty.text.toString(),
//                description = jesta_post_description.text.toString(),
//                imageUrl = "",
//                payment = jesta_post_payment.text.toString().toInt(),
//                numOfPeople = jesta_post_num_of_people.text.toString().toInt(),
//                duration = jesta_post_duration.text.toString().toInt(),
//                location = jesta_post_location.text.toString(),
//                diamonds = diamonds,
//                tags = listOf("Tag1", "Tag2", "Tag3")
//            )
//
//            uploadedImageToDB(jesta, sysManager)
//
////            val isUploadedImageToDB = jesta_post_check_box.isChecked
////            sysManager.setMissionOnDB(jesta,isUploadedImageToDB)
//
//            Toast.makeText(this@AskJestaFragment, "Jesta Sent to DB", Toast.LENGTH_LONG).show()
//            val intent = Intent(this@AskJestaFragment, DoJestaFragment::class.java)
//
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }

        return view
    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_ask_jesta)
//
//        jesta_post_title.filters = jesta_post_title.filters + InputFilter.AllCaps()
//        jesta_post_location.filters = jesta_post_location.filters + InputFilter.AllCaps()
//        jesta_post_button_browse.setOnClickListener {
////            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
//            requestPermission(this)
//        }
//        jesta_post_button_accept.setOnClickListener {
//
//            if (isInvalidInput()) {
//                Toast.makeText(this@AskJestaFragment, "Fill all fields", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
//
//            val diamonds = when (jesta_post_difficulty.text.toString()) {
//                "Easy" -> 10000
//                "Medium" -> 20000
//                else -> 3000
//            }
//
//            val sysManager = SysManager(this@AskJestaFragment)
//
//            val jesta = Mission(
//                authorId = sysManager.currentUserFromDB.id,
//                id = UUID.randomUUID().toString(),
//                title = jesta_post_title.text.toString(),
//                difficulty = jesta_post_difficulty.text.toString(),
//                description = jesta_post_description.text.toString(),
//                imageUrl = "",
//                payment = jesta_post_payment.text.toString().toInt(),
//                numOfPeople = jesta_post_num_of_people.text.toString().toInt(),
//                duration = jesta_post_duration.text.toString().toInt(),
//                location = jesta_post_location.text.toString(),
//                diamonds = diamonds,
//                tags = listOf("Tag1", "Tag2", "Tag3")
//            )
//
//            uploadedImageToDB(jesta, sysManager)
//
////            val isUploadedImageToDB = jesta_post_check_box.isChecked
////            sysManager.setMissionOnDB(jesta,isUploadedImageToDB)
//
//            Toast.makeText(this@AskJestaFragment, "Jesta Sent to DB", Toast.LENGTH_LONG).show()
//            val intent = Intent(this@AskJestaFragment, DoJestaFragment::class.java)
//
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }
//
//        jesta_bottom_navigation.selectedItemId = R.id.nav_ask_jesta
//        jesta_bottom_navigation.setOnNavigationItemSelectedListener {
//
//            if (it.itemId == jesta_bottom_navigation.selectedItemId) return@setOnNavigationItemSelectedListener true
//
//            val intent = when (it.itemId) {
//                R.id.nav_do_jesta -> {
//                    Intent(this@AskJestaFragment, DoJestaFragment::class.java)
//                }
//                R.id.nav_status -> {
//                    Intent(this@AskJestaFragment, StatusFragment::class.java)
//                }
//                // Settings Activity
//                else -> {
//                    Intent(this@AskJestaFragment, SettingsFragment::class.java)
//                }
//            }
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            finish()
//            startActivity(intent)
//            true
//        }
//    }

//    private fun uploadedImageToDB(jesta: Mission, sysManager: SysManager) {
//
//        val uploadAndGetUrl = sysManager.createDBTask(SysManager.DBTask.UPLOAD_FILE, filePath)
//        // note: async function, therefore it is in the end of current function
//        uploadAndGetUrl.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                System.err.println(task.exception)
//                Toast.makeText(
//                    this@AskJestaFragment,
//                    "Quota has been exceeded for this project.",
//                    Toast.LENGTH_LONG
//                ).show()
//                return@addOnCompleteListener
//            } else {
//                Toast.makeText(
//                    this@AskJestaFragment,
//                    "Image uploaded to Storage",
//                    Toast.LENGTH_LONG
//                ).show()
//                jesta.imageUrl = task.result.toString()
//            }
//            sysManager.setMissionOnDB(jesta)
//        }
//    }

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


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
//
//            val cursor = MediaStore.Images.Media.query(contentResolver,  data.data, arrayOf(MediaStore.Images.Media.DATA))
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            val comp = Compressor(this)
//            val file = comp.compressToFile(File(cursor.getString(idx)))
//
//            filePath = Uri.fromFile(file)
////            filePath = data.data
//            jesta_post_preview_mission_image.setImageURI(filePath)
//        }
//    }

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
