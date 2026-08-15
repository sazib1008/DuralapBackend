package com.example.duralap.media.application.service

import com.example.duralap.media.api.MediaModuleApi
import com.example.duralap.media.domain.model.MediaFile
import org.springframework.stereotype.Service

@Service
class MediaModuleApiImpl(
    private val mediaService: MediaService
) : MediaModuleApi {

    override fun getMediaById(id: String): MediaFile? {
        return mediaService.getMediaById(id)
    }

    override fun generateDownloadUrl(id: String): String {
        return mediaService.generateDownloadUrl(id)
    }
}
