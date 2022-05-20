package com.example.newsappeim.repositories

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.example.newsappeim.MainAppActivity
import com.example.newsappeim.data.model.*
import com.example.newsappeim.services.NewsService
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class NewsRepository constructor(private val newsService: NewsService) {

    companion object {
        val gsonSerializer: Gson = Gson()
    }

    val TAG: String = "NewsRepository"

    private val fireStore = Firebase.firestore
    private val fireAuth = Firebase.auth

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun titleToIdProcess(string: String?): String {
        val aux = string!!.replace(".", "").replace("/", "").replace(" ", "")
        return md5(aux)
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
//        val auxResponse: Response<ListOfNewsModel> = newsService.getLatest(NewsService.myMap["country"])
        val auxResponse: Response<ListOfNewsModel> = newsService.getLatest(NewsService.countryCode)
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
                var didSave = false

                if (likedNews.contains(titleToIdProcess(it.title))) {
                    didLike = true
                }
                if (MainAppActivity.preferences.contains(titleToIdProcess(it.title))) {
                    didSave = true
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
            val auxDocuments = fireStoreData.documents.map { it.toObject(FireStoreNewsModel::class.java) }
            val newNewsList = auxDocuments.sortedBy { it!!.likes.size }.reversed().map {
                val auxObj = it

                var didLike = false
                var didSave = false

                if (auxObj!!.likes.contains(connectedUserEmail)) {
                    didLike = true
                }
                if (MainAppActivity.preferences.contains(titleToIdProcess(auxObj.title))) {
                    didSave = true
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
                body = newNewsList
            )
        }

        return ResponseProcessedWithLikes(
            isSuccessful = !fireStoreData.isEmpty,
            message = "",
            body = emptyList()
        )
    }


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

    suspend fun savePost(article: ApiNewsModel, position: Int, preferences: SharedPreferences): NewsStatusSave {
        val documentProcessedId: String = titleToIdProcess(article.title)

        val preferencesEditor: SharedPreferences.Editor = preferences.edit()

        if (preferences.contains(documentProcessedId)) {
            preferencesEditor.remove(documentProcessedId).apply()

            return NewsStatusSave(hasStatusChange = true, hasUserSave = false, indexInList = position)
        } else {
            var image: Bitmap
            try {
                image = BitmapFactory.decodeStream(URL(article.image_url).openConnection().getInputStream())
            } catch(e: Exception) {
                image = MainAppActivity.brokenImageDrawable.toBitmap()
                Log.e(TAG, e.stackTraceToString());
            }

            val articleToBeSaved = SharedPreferencesNewsModel(
                    title = article.title,
                    link = article.link,
                    keyWords = article.keyWords,
                    creator = article.creator,
                    description = article.description,
                    content = article.content,
                    pubDate = convertStringToDate(article.pubDate!!),
                    image_url = image.toString(),
                )

            preferencesEditor.putString(documentProcessedId, gsonSerializer.toJson(articleToBeSaved))
            preferencesEditor.apply()

            return NewsStatusSave(hasStatusChange = true, hasUserSave = true, indexInList = position)
        }
    }

    suspend fun getCommentsOfArticle(title: String): List<FireStoreNewsCommentModel> {
        val documentProcessedId: String = titleToIdProcess(title)

        return try {
            val data = fireStore
                .collection("news_to_consider")
                .document(documentProcessedId).get().await()

            if (data.exists()) {
                val dataCasted = data.toObject(FireStoreNewsModel::class.java)
                dataCasted!!.comments
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            emptyList()
        }
    }


    suspend fun addCommentToPost(article: ApiNewsModel, title: String, newComment: FireStoreNewsCommentModel): Unit {
        val documentProcessedId: String = titleToIdProcess(title)
        try {
            val data = fireStore
                .collection("news_to_consider")
                .document(documentProcessedId).get().await()

            if (data.exists()) {
                val dataCasted = data.toObject(FireStoreNewsModel::class.java)
                val connectedUserEmail: String = fireAuth.currentUser?.email!!

                fireStore.collection("news_to_consider").document(documentProcessedId)
                    .update("comments", FieldValue.arrayUnion(newComment))

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
                    likes = emptyList(),
                    comments = listOf(newComment)
                )

                fireStore.collection("news_to_consider").document(documentProcessedId).set(fireStoreArticle)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

}
