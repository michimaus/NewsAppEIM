package com.example.newsappeim.data.model

import com.google.type.DateTime

data class NewsModel(
    val title: String?,
    val link: String?,
    val keyWords: List<String>?,
    val creator: List<String>?,
    val description: String?,
    val content: String?,
    val pubDate: String?,
    val image_url: String?
)


data class ListOfNewsModel(
    val status: String,
    val totalResults: Int,
    val results: List<NewsModel>
)