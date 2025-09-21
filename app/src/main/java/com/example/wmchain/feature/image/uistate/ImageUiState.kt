package com.example.wmchain.feature.image.uistate

import androidx.work.WorkInfo

data class ImageUiState(
    val downloadImageState: WorkInfo.State? = null,
    val imageProcessingState: WorkInfo.State? = null,
    val imagePath: String? = null
)