package com.example.newsappeim.screens.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappeim.data.model.ApiNewsModelView
import com.example.newsappeim.data.model.FireStoreNewsCommentModel
import com.example.newsappeim.databinding.AdapterCommentsBinding

class CommentsAdapter : RecyclerView.Adapter<CommentsViewHolder>() {

    companion object {
        const val TAG = "NewsCardAdapter"

        val colorsOfChips = listOf(
            Color.rgb(205, 240, 234),
            Color.rgb(249, 200, 249),
            Color.rgb(247, 219, 240),
            Color.rgb(190, 174, 226)
        )
    }

    private var comments = mutableListOf<FireStoreNewsCommentModel>()

    fun setCommentsList(comments: List<FireStoreNewsCommentModel>) {
        this.comments = comments.toMutableList()
        notifyDataSetChanged()
    }

    fun addNewComment(comment: FireStoreNewsCommentModel){
        comments.add(comment)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCommentsBinding.inflate(inflater, parent, false)
        return CommentsViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val currentComment = comments[position]

        holder.binding.commentAuthor.text = currentComment.userEmailCommenting
        holder.binding.commentContent.text = currentComment.comment
        holder.binding.commentDate.text = currentComment.pubDate.toDate().toString()
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}

class CommentsViewHolder(val binding: AdapterCommentsBinding) : RecyclerView.ViewHolder(binding.root)