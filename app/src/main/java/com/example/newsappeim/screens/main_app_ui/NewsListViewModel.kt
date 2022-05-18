package com.example.newsappeim.screens.main_app_ui

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsappeim.data.model.ApiNewsModel
import com.example.newsappeim.data.model.ApiNewsModelView
import com.example.newsappeim.data.model.NewsStatusLike
import com.example.newsappeim.data.model.NewsStatusSave
import com.example.newsappeim.repositories.NewsRepository
import kotlinx.coroutines.*

class NewsListViewModel constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val TAG: String = "LatestViewModel"

    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>().apply { value = true }
    val obtainedNewsList = MutableLiveData<List<ApiNewsModelView>>()
    val articleToLike = MutableLiveData<NewsStatusLike>()
    val articleToSave = MutableLiveData<NewsStatusSave>()

    var job: Job? = null
    var likeJob: Job? = null
    var saveJob: Job? = null

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

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        likeJob?.cancel()
        saveJob?.cancel()
    }
}