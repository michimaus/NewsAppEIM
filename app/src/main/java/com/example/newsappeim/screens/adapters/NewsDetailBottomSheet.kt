package com.example.newsappeim.screens.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsappeim.R
import com.example.newsappeim.data.model.ApiNewsModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewsDetailBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.modal_bottom_sheet_news_details, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    fun setNewsData(article : ApiNewsModel) {
//        val modalBottomSheetBehavior = (this.dialog as BottomSheetDialog).behavior
//        modalBottomSheetBehavior.peekHeight = 300
    }

}
