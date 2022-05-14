package com.example.newsappeim.repositories

import android.os.Build
import android.os.Parcel
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newsappeim.data.model.ApiNewsModel
import com.example.newsappeim.data.model.FireStoreNewsCommentModel
import com.example.newsappeim.data.model.FireStoreNewsModel
import com.example.newsappeim.services.NewsService
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NewsRepository constructor(private val newsService: NewsService) {

    val TAG: String = "NewsRepository"

    private val fireStore = Firebase.firestore
    private val fireAuth = Firebase.auth

    private fun titleToIdProcess(string: String?): String {
        return string!!.replace(".", "").replace("/", "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToDate(string: String): Timestamp {
        val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss")

        return Timestamp(
            LocalDateTime.parse(string, formatter).toEpochSecond(ZoneOffset.UTC),
            LocalDateTime.parse(string, formatter).nano
        )
    }

    suspend fun getLatest() = newsService.getLatest()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun likePost(article: ApiNewsModel) {

        Log.wtf(TAG, "ceva ceva ceva")

        fireStore
            .collection("news_to_consider")
            .document(titleToIdProcess(article.title)).get()
            .addOnCompleteListener {
                Log.wtf(TAG, "ceva ceva we iiiiiiinnnn")

                if (it.isSuccessful) {

                } else {
                    Log.wtf(TAG, "ceva ceva cevayeeeeeee")

                    val fireStoreArticle: FireStoreNewsModel = FireStoreNewsModel(
                        title = titleToIdProcess(article.title),
                        link = article.link,
                        keyWords = article.keyWords,
                        creator = article.creator,
                        description = article.description,
                        content = article.content,
                        pubDate = convertStringToDate(article.pubDate!!),
                        image_url = article.image_url,
                        likes = listOf(fireAuth.currentUser?.email!!),
                        comments = emptyList()
                    )

                    Log.wtf(TAG, "ceva mrrrrrrrrr")

                    fireStore.collection("news_to_consider").document(article.title!!).set(fireStoreArticle)
                }
            }
    }
}
