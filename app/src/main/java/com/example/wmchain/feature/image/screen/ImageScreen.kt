package com.example.wmchain.feature.image.screen

import android.R.attr.onClick
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.example.wmchain.feature.image.viewmodel.ImageViewModel

@Composable
fun ImageScreen(modifier: Modifier = Modifier) {
    Scaffold {innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            val context = LocalContext.current
            val viewModel: ImageViewModel = viewModel{
                ImageViewModel(
                    WorkManager.getInstance(context)
                )
            }
            val imageUiState by viewModel.imageUiState.collectAsStateWithLifecycle()
            val downloadState = imageUiState.downloadImageState
            val processImageState = imageUiState.imageProcessingState
            Button (
                onClick = {
                    viewModel.startDownloadAndProcess()
                }
            ) {
                Text(text = "Start Download and Process")
            }
            Text(text = "Download State: $downloadState")
            Text(text = "Process Image State: $processImageState")
            when {
                downloadState == WorkInfo.State.SUCCEEDED -> {
                    Text(text = "Image Downloaded")
                }
                processImageState == WorkInfo.State.SUCCEEDED -> {
                    Text(text = "Image Processed")
                }
                downloadState == WorkInfo.State.RUNNING -> {
                    Text(text = "Downloading Image")
                }
                processImageState == WorkInfo.State.RUNNING -> {
                    Text(text = "Processing Image")
                }
                downloadState == WorkInfo.State.FAILED -> {
                    Text(text = "Download Failed")
                }
                processImageState == WorkInfo.State.FAILED -> {
                    Text(text = "Process Image Failed")
                }
                downloadState == WorkInfo.State.CANCELLED -> {
                    Text(text = "Download Cancelled")
                }
                processImageState == WorkInfo.State.CANCELLED -> {
                    Text(text = "Process Image Cancelled")
                }
                downloadState == WorkInfo.State.ENQUEUED -> {
                    Text(text = "Download Enqueued")
                }
                processImageState == WorkInfo.State.ENQUEUED -> {
                    Text(text = "Process Image Enqueued")
                }
                downloadState == WorkInfo.State.BLOCKED -> {
                    Text(text = "Download Blocked")
                }
                processImageState == WorkInfo.State.BLOCKED -> {
                    Text(text = "Process Image Blocked")
                }
                else -> {
                    Text(text = "Idle")
                }
            }
            when {
                imageUiState.imagePath != null -> {
                    Text(text = "Image Path: ${imageUiState.imagePath}")
                    AsyncImage(
                        model = imageUiState.imagePath,
                        contentDescription = "Image"
                    )
                }
            }
        }
    }
}