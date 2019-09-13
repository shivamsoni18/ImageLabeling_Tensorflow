Implementation Image Labeling with Custom TensorFlow Lite model in Android.

Image Labeling is the process of recognizing different entities in an image.
You can recognize various entities like animals, plants, food, activities, colors, things, fictional characters, drinks etc with Image Labeling.

ML Kit's pre-built models don't meet your needs, you can use a custom TensorFlow Lite model with ML Kit.


For that, you need to follow steps as below,

Step 1: Add Firebase to your app
Add Firebase to your app by following these steps.

Step 2: Include the dependencies
dependencies {
  ...
    
  implementation 'com.google.firebase:firebase-ml-vision:18.0.1'
  implementation 'com.google.firebase:firebase-ml-vision-image-label-model:17.0.2'
}

Step 3: To enable this feature you need to specify your models in your app’s AndroidManifest.xml file.
<application ...>
  ...
  <meta-data
      android:name="com.google.firebase.ml.vision.DEPENDENCIES"
      android:value="label" />
</application>

Step 4: Include 3 files in your assets directory,
To generate these files you need to add a Data set by clicking on AutoML option in ML Kit and Upload images with specific Labels.


you need to upload minimum 10 Images of a particular label and if you add more images you will get more accurate result.

after completion of Training (It will take 30-60mins to train), you will get required files then add them into "assets" directory. 


Sample Code: 

analyzeImage(MediaStore.Images.Media.getBitmap(contentResolver, imageUri))

private fun analyzeImage(image: Bitmap?) {
    val localSource = FirebaseLocalModel.Builder("employee_data_2")
            .setAssetFilePath("manifest.json")
            .build()
    FirebaseModelManager.getInstance().registerLocalModel(localSource)

    val firebaseImage = FirebaseVisionImage.fromBitmap(image!!)

    val labelerOptions = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
            .setLocalModelName("employee_data_2")
            .build()

    val labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions)

    labeler.processImage(firebaseImage)
            .addOnSuccessListener { labels ->
                imageView.setImageBitmap(image)
                val firstLabel = labels.firstOrNull()
                if (firstLabel?.text != null || firstLabel?.confidence!! > 0.5) {
                    if (firstLabel.confidence > 0.6) {
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




Links Referred :
https://firebase.google.com/docs/ml-kit/automl-image-labeling
https://firebase.google.com/docs/ml-kit/android/use-custom-models
https://firebase.google.com/docs/ml-kit/android/label-images-with-automl