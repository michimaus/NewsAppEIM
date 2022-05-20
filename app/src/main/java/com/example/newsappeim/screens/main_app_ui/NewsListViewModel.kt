package com.example.newsappeim.screens.main_app_ui

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsappeim.data.model.*
import com.example.newsappeim.repositories.NewsRepository
import kotlinx.coroutines.*

class NewsListViewModel constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val TAG: String = "LatestViewModel"

    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>().apply { value = true }
    val obtainedNewsList = MutableLiveData<List<ApiNewsModelView>>()
    val articleToLike = MutableLiveData<NewsStatusLike>()
    val articleToSave = MutableLiveData<NewsStatusSave>()
    val commentsToGet = MutableLiveData<GetCommentsHelperModel>()

    var job: Job? = null
    var likeJob: Job? = null
    var saveJob: Job? = null
    var commentsJob: Job? = null
    var addComment: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }


    fun getLatest() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = newsRepository.getLatest()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    obtainedNewsList.postValue(response.body)
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message} ")
                }
            }
        }
    }


    fun getHotNews() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = newsRepository.getHotNews()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    obtainedNewsList.postValue(response.body)
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message} ")
                }
            }
        }
    }


    fun likePost(article: ApiNewsModel, position: Int) {
        likeJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response: NewsStatusLike = newsRepository.likePost(article, position)

            withContext(Dispatchers.Main) {
                articleToLike.postValue(response)
            }
        }
    }


    fun savePost(article: ApiNewsModel, position: Int, preferences: SharedPreferences) {
        saveJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response: NewsStatusSave = newsRepository.savePost(article, position, preferences)

            withContext(Dispatchers.Main) {
                articleToSave.postValue(response)
            }
        }
    }


    fun getArticleComments(
        title: String,
        article: ApiNewsModel,
        newsViewModel: NewsListViewModel
    ) {
        commentsJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response: List<FireStoreNewsCommentModel> = newsRepository.getCommentsOfArticle(title)

            withContext(Dispatchers.Main) {
                commentsToGet.postValue(GetCommentsHelperModel(apiNewsModelWeb = article, comments = response))
            }
        }
    }


    fun addCommentToArticle(article: ApiNewsModel, title: String, newComment: FireStoreNewsCommentModel) {
        addComment = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            newsRepository.addCommentToPost(article, title, newComment)
            withContext(Dispatchers.Main) {
            }
        }
    }


    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        likeJob?.cancel()
        saveJob?.cancel()
        commentsJob?.cancel()
        addComment?.cancel()
    }
}