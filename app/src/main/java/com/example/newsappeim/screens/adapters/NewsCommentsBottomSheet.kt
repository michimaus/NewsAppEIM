package com.example.newsappeim.screens.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsappeim.data.model.ApiNewsModel
import com.example.newsappeim.data.model.FireStoreNewsCommentModel
import com.example.newsappeim.databinding.ModalBottomSheetNewsCommentsBinding
import com.example.newsappeim.screens.main_app_ui.NewsListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class NewsCommentsBottomSheet : BottomSheetDialogFragment() {

    private var binding: ModalBottomSheetNewsCommentsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalBottomSheetNewsCommentsBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"

        val colorsOfChips = listOf(
            Color.rgb(205, 240, 234),
            Color.rgb(249, 200, 249),
            Color.rgb(247, 219, 240),
            Color.rgb(190, 174, 226)
        )
    }

    fun setCommentsData(
        article: ApiNewsModel,
        comments: List<FireStoreNewsCommentModel>,
        newsViewModel: NewsListViewModel
    ) {

        val commentsAdapter = CommentsAdapter()
        commentsAdapter.setCommentsList(comments)

        binding!!.commentsList.adapter = commentsAdapter
        binding!!.title.text = article.title
        binding!!.articleDate.text = article.pubDate

        if (article.creator?.isNotEmpty() == true) {
            binding!!.authorsTextView.text = "Written by:"

            article.creator.forEach {
                binding!!.authorsTextView.append("\n \u2022 " + it)
            }
        } else {
            binding!!.authorsTextView.text = "Written by:"
            binding!!.authorsTextView.append("\n \u2022 " + "Well known author")
        }

        binding!!.submitButton.setOnClickListener {
            if (binding!!.newComments.text.toString().isNotEmpty()) {

                val newComment = FireStoreNewsCommentModel(
                    comment = binding!!.newComments.text.toString(),
                    userEmailCommenting = FirebaseAuth.getInstance().currentUser?.email!!,
                    pubDate = Timestamp.now()
                )

                newsViewModel.addCommentToArticle(
                    article,
                    article.title!!,
                    newComment
                )
                commentsAdapter.addNewComment(newComment)
            }
        }
    }

}
