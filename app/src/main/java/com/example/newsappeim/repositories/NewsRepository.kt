package com.example.newsappeim.repositories

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newsappeim.data.model.*
import com.example.newsappeim.services.NewsService
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class NewsRepository constructor(private val newsService: NewsService) {

    val TAG: String = "NewsRepository"

    private val fireStore = Firebase.firestore
    private val fireAuth = Firebase.auth

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun titleToIdProcess(string: String?): String {
        val aux = string!!.replace(".", "").replace("/", "").replace(" ", "")
        return md5(aux);
    }


    @SuppressLint("NewApi")
    fun convertStringToDate(string: String): Timestamp {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return Timestamp(LocalDateTime.parse(string, formatter).atOffset(ZoneOffset.UTC).toInstant().epochSecond, 0)
    }

    @SuppressLint("NewApi")
    fun convertTimestampToStringFormat(timestamp: Timestamp): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
        return formatter.format(timestamp.toDate())
    }

    suspend fun getLatest(): ResponseProcessedWithLikes {
        val auxResponse: Response<ListOfNewsModel> = newsService.getLatest()
        if (auxResponse.isSuccessful) {
            val weNewsList = auxResponse.body()!!.results
            val connectedUserEmail: String = fireAuth.currentUser?.email!!

            val listOfHashedNames: List<String> = weNewsList.map { titleToIdProcess(it.title) }

            val fireStoreData = fireStore.collection("news_to_consider")
                .whereIn(FieldPath.documentId(), listOfHashedNames)
                .whereArrayContains("likes", connectedUserEmail)
                .get().await()

            val likedNews = fireStoreData.documents.map { it.id }
            val finalList = weNewsList.map {
                var didLike = false
                val didSave = false

                if (likedNews.contains(titleToIdProcess(it.title))) {
                    didLike = true
                }
                ApiNewsModelView(apiNewsModelWeb = it, didUserLike = didLike, didUserSaved = didSave)
            }

            return ResponseProcessedWithLikes(
                isSuccessful = auxResponse.isSuccessful,
                message = auxResponse.message(),
                body = finalList
            )
        }

        return ResponseProcessedWithLikes(
            isSuccessful = auxResponse.isSuccessful,
            message = auxResponse.message(),
            body = emptyList()
        )
    }


    @SuppressLint("NewApi")
    suspend fun getHotNews(): ResponseProcessedWithLikes {

        val offsetTimeHot =
            Timestamp(
                LocalDateTime.now().minusDays(1).minusHours(12).atOffset(ZoneOffset.UTC).toInstant().epochSecond,
                0
            )
        val connectedUserEmail: String = fireAuth.currentUser?.email!!

        val fireStoreData = fireStore.collection("news_to_consider")
            .whereGreaterThan("pubDate", offsetTimeHot)
            .get().await()

        if (!fireStoreData.isEmpty) {
            val weNewsList = fireStoreData.documents.map {
                val auxObj = it.toObject(FireStoreNewsModel::class.java)

                var didLike = false
                var didSave = false

                if (auxObj!!.likes.contains(connectedUserEmail)) {
                    didLike = true
                }

                ApiNewsModelView(
                    apiNewsModelWeb = ApiNewsModel(
                        title = auxObj.title,
                        link = auxObj.link,
                        keyWords = auxObj.keyWords,
                        creator = auxObj.creator,
                        description = auxObj.description,
                        content = auxObj.content,
                        pubDate = convertTimestampToStringFormat(auxObj.pubDate!!),
                        image_url = auxObj.image_url
                    ), didUserLike = didLike, didUserSaved = didSave
                )
            }

            return ResponseProcessedWithLikes(
                isSuccessful = !fireStoreData.isEmpty,
                message = "",
                body = weNewsList
            )
        }

        return ResponseProcessedWithLikes(
            isSuccessful = !fireStoreData.isEmpty,
            message = "",
            body = emptyList()
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun likePost(article: ApiNewsModel, position: Int): NewsStatusLike {
        val documentProcessedId: String = titleToIdProcess(article.title)
        try {
            val data = fireStore
                .collection("news_to_consider")
                .document(documentProcessedId).get().await()

            if (data.exists()) {
                val dataCasted = data.toObject(FireStoreNewsModel::class.java)
                val connectedUserEmail: String = fireAuth.currentUser?.email!!

                if (dataCasted?.likes!!.contains(connectedUserEmail)) {
                    fireStore.collection("news_to_consider").document(documentProcessedId)
                        .update("likes", FieldValue.arrayRemove(connectedUserEmail))
                    return NewsStatusLike(hasStatusChange = true, hasUserLike = false, indexInList = position)
                } else {
                    fireStore.collection("news_to_consider").document(documentProcessedId)
                        .update("likes", FieldValue.arrayUnion(connectedUserEmail))
                    return NewsStatusLike(hasStatusChange = true, hasUserLike = true, indexInList = position)
                }
            } else {
                val fireStoreArticle = FireStoreNewsModel(
                    title = article.title,
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

                fireStore.collection("news_to_consider").document(documentProcessedId).set(fireStoreArticle)
                return NewsStatusLike(hasStatusChange = true, hasUserLike = true, indexInList = position)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())

            return NewsStatusLike(hasStatusChange = false, hasUserLike = false, indexInList = position)
        }
    }
}
