package com.example.newsappeim.screens.main_app_ui.latest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsappeim.repositories.NewsRepository

class LatestViewModelFactory constructor(private val repository: NewsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LatestViewModel::class.java)) {
            LatestViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("LatestViewModel Not Found")
        }
    }
}