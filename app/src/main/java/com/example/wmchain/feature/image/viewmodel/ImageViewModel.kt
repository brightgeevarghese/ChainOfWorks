package com.example.wmchain.feature.image.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.wmchain.DownloadWorker
import com.example.wmchain.GrayScaleWorker
import com.example.wmchain.feature.image.uistate.ImageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val UNIQUE_WORK_NAME = "unique_work_name"

class ImageViewModel(private val workManager: WorkManager): ViewModel() {
    private val _imageUiState = MutableStateFlow(ImageUiState())
    val imageUiState = _imageUiState.asStateFlow()
    private var downloadRequestId: UUID? = null
    private var processImageRequestId: UUID? = null

    init {
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_WORK_NAME)
                .collect {
                    val downloadInfo: WorkInfo? = it.find { workInfo ->
                        workInfo.id == downloadRequestId
                    }
                    val processImageInfo: WorkInfo? = it.find { workInfo ->
                        workInfo.id == processImageRequestId
                    }
                    val downloadState: WorkInfo.State? = downloadInfo?.state
                    val processImageState: WorkInfo.State? = processImageInfo?.state
                    if (downloadState != null) {
                        _imageUiState.update { imageUiState ->
                            imageUiState.copy(
                                downloadImageState = downloadState
                            )
                        }
                    }
                    if (processImageState != null) {
                        var imagePath: String? = null
                        if (processImageState == WorkInfo.State.SUCCEEDED) {
                            imagePath = processImageInfo?.outputData?.getString("final_path")
                        }
                        _imageUiState.update { imageUiState ->
                            imageUiState.copy(
                                imageProcessingState = processImageState,
                                imagePath = imagePath
                            )
                        }
                    }
                }
        }
    }

    fun startDownloadAndProcess() {
        val inputData = workDataOf("url" to "https://cdn.pixabay.com/photo/2022/08/22/11/04/skate-7403432_1280.jpg")
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(inputData)
            .build()
        downloadRequestId = downloadRequest.id
        val processImageRequest = OneTimeWorkRequestBuilder<GrayScaleWorker>()
            .build()
        processImageRequestId = processImageRequest.id
        workManager.beginUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        )
            .then(processImageRequest)
            .enqueue()
    }
}