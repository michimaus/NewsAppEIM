package com.example.newsappeim.data.model

import com.google.firebase.Timestamp

data class ApiNewsModel(
    val title: String?,
    val link: String?,
    val keyWords: List<String>?,
    val creator: List<String>?,
    val description: String?,
    val content: String?,
    val pubDate: String?,
    val image_url: String?
) {
    constructor() : this("", "", emptyList(), emptyList(), "", "", "", "")
}

data class ApiNewsModelView(
    val apiNewsModelWeb: ApiNewsModel,
    var didUserLike: Boolean,
    var didUserSaved: Boolean,
) {
    constructor() : this( ApiNewsModel(), false, false)
}

data class ResponseProcessedWithLikes(
    val message: String,
    val isSuccessful: Boolean,
    val body: List<ApiNewsModelView>
)


data class ListOfNewsModel(
    val status: String,
    val totalResults: Int,
    val results: List<ApiNewsModel>
)

data class FireStoreNewsModel(
    val title: String?,
    val link: String?,
    val keyWords: List<String>?,
    val creator: List<String>?,
    val description: String?,
    val content: String?,
    val pubDate: Timestamp?,
    val image_url: String?,
    val likes: List<String>,
    val comments: List<FireStoreNewsCommentModel>,
) {
    constructor() : this("", "", emptyList(), emptyList(), "", "", Timestamp.now(), "", emptyList(), emptyList())
}

data class FireStoreNewsCommentModel(
    val comment: String,
    val userEmailCommenting: String,
    val pubDate: Timestamp,
) {
    constructor() : this("", "", Timestamp.now())
}