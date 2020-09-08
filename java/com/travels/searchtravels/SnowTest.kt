package com.travels.searchtravels

import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.Feature
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.utils.ImageHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class SnowTest {

    fun snowTestMethod() {

        val appContext: Context = InstrumentationRegistry.getInstrumentation().context

        val bitmap = ImageHelper.resizeBitmap(MediaStore.Images.Media.getBitmap(appContext.contentResolver, "com/travels/searchtravels/screenshots/snowPhoto.jpg".toUri()))

        val token = AppPreferences.getToken(appContext)

        val credential = GoogleCredential().setAccessToken(token)

        val httpTransport = AndroidHttp.newCompatibleTransport()

        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        val builder = Vision.Builder(httpTransport, jsonFactory, credential)

        val vision = builder.build()

        val featureList: MutableList<Feature> = ArrayList()

        val textDetection = Feature()
        textDetection.type = "WEB_DETECTION"
        textDetection.maxResults = 10
        featureList.add(textDetection)

        val landmarkDetection = Feature()
        landmarkDetection.type = "LANDMARK_DETECTION"
        landmarkDetection.maxResults = 10
        featureList.add(landmarkDetection)

        val imageList: MutableList<AnnotateImageRequest> = ArrayList()

        val annotateImageRequest = AnnotateImageRequest()

        val base64EncodedImage = ImageHelper.getBase64EncodedJpeg(bitmap)
        annotateImageRequest.image = base64EncodedImage
        annotateImageRequest.features = featureList
        imageList.add(annotateImageRequest)

        val batchAnnotateImagesRequest = BatchAnnotateImagesRequest()
        batchAnnotateImagesRequest.requests = imageList

        val annotateRequest = vision.images().annotate(batchAnnotateImagesRequest)
        annotateRequest.disableGZipContent = true
        val response = annotateRequest.execute()
        Assert.assertEquals("snow", response)
    }
}