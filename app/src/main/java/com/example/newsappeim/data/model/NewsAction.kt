package com.example.newsappeim.data.model

data class NewsStatusLike(
    val hasStatusChange: Boolean,
    val hasUserLike: Boolean,
    val indexInList: Int
)

data class NewsStatusSave(
    val hasStatusChange: Boolean,
    val hasUserSave: Boolean,
    val indexInList: Int
)





