package com.submission.mystoryappsv2.view.addstory

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.view.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var descriptionEditText: EditText
    private lateinit var addPhotoImageView: ImageView
    private lateinit var choosePhotoButton: Button
    private lateinit var addButton: Button
    private var selectedPhotoUri: Uri? = null
    private var selectedPhotoFile: File? = null

    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        descriptionEditText = findViewById(R.id.ed_add_description)
        addPhotoImageView = findViewById(R.id.iv_add_photo)
        choosePhotoButton = findViewById(R.id.button_choose_photo)
        addButton = findViewById(R.id.button_add)

        choosePhotoButton.setOnClickListener {
            openGallery()
        }

        addButton.setOnClickListener {
            addStory()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = data?.data
            selectedPhotoUri?.let {
                addPhotoImageView.setImageURI(it)
                selectedPhotoFile = uriToFile(it)
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return File(filePath)
    }

    private fun addStory() {
        val description = descriptionEditText.text.toString()
        val file = selectedPhotoFile

        if (description.isNotEmpty() && file != null) {
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData(
                "photo", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
            )

            viewModel.addStory(descriptionPart, photoPart).observe(this) { response ->
                if (!response.error) {
                    // Berhasil menambahkan cerita
                    finish() // Kembali ke StoryListActivity
                } else {
                    // Tangani error
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 1
    }
}
