package com.example.duralap.search.dto

data class SearchResponse<T>(
    val results: List<T>,
    val nextCursor: String?
)
