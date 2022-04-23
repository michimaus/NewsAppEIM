package com.example.newsappeim.screens.main_app_ui.new_post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewPostViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is new post Fragment"
    }
    val text: LiveData<String> = _text
}