package com.example.newsappeim.screens.main_app_ui.latest

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsappeim.data.model.ApiNewsModel
import com.example.newsappeim.data.model.ApiNewsModelView
import com.example.newsappeim.data.model.ListOfNewsModel
import com.example.newsappeim.data.model.NewsStatusLike
import com.example.newsappeim.repositories.NewsRepository
import kotlinx.coroutines.*

class LatestViewModel constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val TAG: String = "LatestViewModel"

    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>().apply { value = true }
    val latestList = MutableLiveData<List<ApiNewsModelView>>()
    val articleToLike = MutableLiveData<NewsStatusLike>()

    var job: Job? = null
    var likeJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun getLatest() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            loading.postValue(true)
            val response = newsRepository.getLatest()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    latestList.postValue(response.body)
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message} ")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun likePost(article: ApiNewsModel, position: Int) {
        likeJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response: NewsStatusLike = newsRepository.likePost(article, position)

            withContext(Dispatchers.Main) {
                articleToLike.postValue(response)
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
    }
}