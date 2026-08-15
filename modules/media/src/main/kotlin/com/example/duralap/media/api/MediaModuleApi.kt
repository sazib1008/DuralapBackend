package com.example.duralap.media.api

import com.example.duralap.media.domain.model.MediaFile

interface MediaModuleApi {
    fun getMediaById(id: String): MediaFile?
    fun generateDownloadUrl(id: String): String
}
