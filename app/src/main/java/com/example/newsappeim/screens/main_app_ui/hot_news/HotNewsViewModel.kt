package com.example.newsappeim.screens.main_app_ui.hot_news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HotNewsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is hot news Fragment"
    }
    val text: LiveData<String> = _text
}