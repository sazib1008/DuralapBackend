package com.example.duralap.media.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SupabaseStorageService(
    @Value("\${supabase.url}") private val supabaseUrl: String,
    @Value("\${supabase.bucket}") private val bucket: String,
    @Value("\${supabase.api.key}") private val apiKey: String
) {
    private val restClient = RestClient.builder()
        .baseUrl(supabaseUrl)
        .defaultHeader("Authorization", "Bearer $apiKey")
        .defaultHeader("apikey", apiKey)
        .build()

    fun uploadFile(storagePath: String, fileBytes: ByteArray, mimeType: String): String {
        val url = "/storage/v1/object/$bucket/$storagePath"
        restClient.post()
            .uri(url)
            .contentType(MediaType.parseMediaType(mimeType))
            .body(fileBytes)
            .retrieve()
            .toBodilessEntity()
        return "$bucket/$storagePath"
    }

    fun deleteFile(storagePath: String) {
        val url = "/storage/v1/object/$bucket"
        restClient.method(org.springframework.http.HttpMethod.DELETE)
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("prefixes" to listOf(storagePath)))
            .retrieve()
            .toBodilessEntity()
    }

    fun getSignedUrl(storagePath: String, expiresInSeconds: Int = 3600): String {
        val url = "/storage/v1/object/sign/$bucket/$storagePath"
        val responseBody = restClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("expiresIn" to expiresInSeconds))
            .retrieve()
            .body(Map::class.java) ?: throw RuntimeException("Failed to get signed URL")
        
        val relativeUrl = responseBody["signedURL"] as? String 
            ?: responseBody["signedUrl"] as? String 
            ?: throw RuntimeException("signedURL not found in Supabase response: $responseBody")
            
        return if (relativeUrl.startsWith("http")) relativeUrl else "$supabaseUrl$relativeUrl"
    }
}
