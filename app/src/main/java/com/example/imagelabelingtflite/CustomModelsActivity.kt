package com.example.imagelabelingtflite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.hitanshudhawan.firebasemlkitexample.custommodels.CustomModelsModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_custom_models.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL


class CustomModelsActivity : AppCompatActivity() {

    private val imageView by lazy { findViewById<ImageView>(R.id.custom_models_image_view)!! }

    private val bottomSheetButton by lazy { findViewById<ImageView>(R.id.bottom_sheet_button_image)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_models)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bottomSheetButton.setOnClickListener {
            CropImage.activity().start(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val imageUri = result.uri
                analyzeImage(MediaStore.Images.Media.getBitmap(contentResolver, imageUri))
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "There was some error : ${result.error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun analyzeImage(image: Bitmap?) {
//
        val localSource = FirebaseLocalModel.Builder("developers_data")
                .setAssetFilePath("manifest.json")
                .build()
        FirebaseModelManager.getInstance().registerLocalModel(localSource)

        val firebaseImage = FirebaseVisionImage.fromBitmap(image!!)

        val labelerOptions = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                .setLocalModelName("developers_data")    // Skip to not use a local model
                .build()
        val labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions)

        labeler.processImage(firebaseImage)
                .addOnSuccessListener { labels ->
                    imageView.setImageBitmap(image)
                    val firstLabel = labels.firstOrNull()
                    if (firstLabel?.text != null) {
                        if (firstLabel.confidence > 0.4) {
                            personName.setText(firstLabel?.text)
                        }

                    } else {
                        personName.setText("not recognized")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "There was some error in labeler ${e.message}", Toast.LENGTH_SHORT).show()
                }

    }

}