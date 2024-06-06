package com.submission.mystoryappsv2.view.addstory

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.view.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
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

    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            addPhotoImageView.setImageURI(it)

            
            selectedPhotoFile = getFileFromUri(it)
        }
    }

    
    private fun getFileFromUri(uri: Uri): File? {
        val contentResolver = applicationContext.contentResolver
        var filePath: String? = null

        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    filePath = it.getString(columnIndex)
                }
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            filePath = uri.path
        }

        return filePath?.let { File(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        
        descriptionEditText = findViewById(R.id.ed_add_description)
        addPhotoImageView = findViewById(R.id.iv_add_photo)
        choosePhotoButton = findViewById(R.id.button_choose_photo)
        addButton = findViewById(R.id.button_add)

        
        choosePhotoButton.setOnClickListener {
            openPhotoPicker()
        }

        
        addButton.setOnClickListener {
            addStory()
        }
    }

    
    private fun openPhotoPicker() {
        getContent.launch("image/*")
    }

    
    private fun addStory() {
        val description = descriptionEditText.text.toString()
        val file = selectedPhotoFile

        
        if (description.isNotEmpty() && file != null) {
            Log.d("AddStoryActivity", "Description: $description, File: $file")

            val descriptionPart = description.toRequestBody("text/plain".toMediaType())
            val photoPart = MultipartBody.Part.createFormData(
                "photo", file.name, file.asRequestBody("image/jpeg".toMediaType())
            )

            
            viewModel.addStory(descriptionPart, photoPart).observe(this) { response ->
                if (!response.error) {
                    
                    Log.d("AddStoryActivity", "Story added successfully")
                    finish() 
                } else {
                    
                    Log.e("AddStoryActivity", "Error adding story: ${response.message}")
                }
            }
        } else {
            
            Log.e("AddStoryActivity", "Description or file is empty")
        }
    }
}
