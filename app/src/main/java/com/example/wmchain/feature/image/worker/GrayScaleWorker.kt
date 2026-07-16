package com.example.wmchain

import android.R.attr.path
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

class GrayScaleWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            // Read the output Data from DownloadWorker
            val imagePath = inputData.getString("image_path") ?: return Result.failure()
            // Decode the image file into a Bitmap.
            // If decoding fails, return failure.
            val bitmap = BitmapFactory.decodeFile(imagePath) ?: return Result.failure()

            // Create a new bitmap to hold the grayscale image.
            val grayBitmap = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height,
                Bitmap.Config.ARGB_8888
            )

            // Create a canvas to draw onto the new bitmap.
            val canvas = Canvas(grayBitmap)

            // Create a Paint object with a grayscale color filter.
            // Setting saturation to 0 maps the color to gray-scale.
            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(
                    ColorMatrix().apply {
                        setSaturation(0f)
                    }
                )
            }
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            // Save new file
            val grayFile = File(applicationContext.cacheDir, "gray_image.png")
            FileOutputStream(grayFile).use { out ->
                grayBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            delay(3000)
            Result.success(workDataOf("final_path" to grayFile.absolutePath))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("GrayScaleWorker", "Error converting image to grayscale: ${e.message}")
            Result.failure()
        }
    }
}

