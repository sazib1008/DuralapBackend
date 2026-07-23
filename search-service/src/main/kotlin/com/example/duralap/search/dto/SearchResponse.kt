package com.example.duralap.search.dto

data class SearchResponse<T>(
    val items: List<T>,
    val nextCursor: String?
)
