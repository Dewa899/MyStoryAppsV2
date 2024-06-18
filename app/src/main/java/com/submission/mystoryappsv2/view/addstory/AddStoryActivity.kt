package com.submission.mystoryappsv2.view.addstory

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.view.ViewModelFactory
import com.submission.mystoryappsv2.view.custom.ClearableEditText
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addPhotoImageView: ImageView
    private lateinit var choosePhotoButton: Button
    private lateinit var addButton: Button
    private lateinit var descriptionEditText: ClearableEditText
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
        val fileName = getFileName(uri) ?: return null
        val tempFile = File(applicationContext.cacheDir, fileName)

        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(tempFile)
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return tempFile
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    private fun copyStream(input: InputStream, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
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
        val description = descriptionEditText.getText()
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
                    setResult(RESULT_OK)
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
