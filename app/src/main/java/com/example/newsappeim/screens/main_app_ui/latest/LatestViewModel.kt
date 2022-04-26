package com.example.newsappeim.screens.main_app_ui.latest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsappeim.data.model.ListOfNewsModel
import com.example.newsappeim.repositories.NewsRepository
import kotlinx.coroutines.*

class LatestViewModel constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val TAG: String = "LatestViewModel"

    private val _text = MutableLiveData<String>().apply {
        value = "This is latest Fragment"
    }
    val text: LiveData<String> = _text

    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>().apply {
        value = true
    }
    val latestList = MutableLiveData<ListOfNewsModel>()

    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun getLatest() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            Log.wtf(TAG,"ya ready x111?!")
            loading.postValue(true)
            val response = newsRepository.getLatest()
            Log.wtf(TAG,"ya ready?!")

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    latestList.postValue(response.body())
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
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
    }
}