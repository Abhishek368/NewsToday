package com.example.indiannewsapp.ui.models

import com.example.indiannewsapp.ui.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)