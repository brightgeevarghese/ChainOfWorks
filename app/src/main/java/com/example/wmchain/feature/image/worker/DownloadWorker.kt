package com.example.wmchain

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import java.io.File
import java.net.URL

class DownloadWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return runCatching {
            val urlString = inputData.getString("url") ?: return Result.failure()
            Log.d("DownloadWorker", "Downloading image from $urlString")
            val url = URL(urlString)
            val inputStream = url.openStream()
            val imageBytes = inputStream.readBytes()
            val file = File(applicationContext.cacheDir, "image.jpg")
            file.writeBytes(imageBytes)
            Log.d("DownloadWorker", "Image downloaded to ${file.absolutePath}")
            delay(3000)
            val output = workDataOf("image_path" to file.absolutePath)
            Result.success(output)
        }.fold(
            onSuccess = {
                it
            },
            onFailure = {
//                Result.failure(workDataOf("error" to it.message))
                Result.retry()
            }
        )
    }

}