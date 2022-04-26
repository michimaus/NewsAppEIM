package com.example.newsappeim.screens.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappeim.data.model.NewsModel
import com.example.newsappeim.databinding.AdapterNewsBinding

class NewsCardAdapter : RecyclerView.Adapter<MainViewHolder>() {

    val TAG: String = "NewsCardAdapter"

    var news = mutableListOf<NewsModel>()
    fun setNewsModelList(movies: List<NewsModel>) {
        this.news = movies.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterNewsBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val article = news[position]
        holder.binding.title.text = article.title
        holder.binding.textView.text = article.description

        if (article.image_url != null) {
            holder.binding.imageview.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(holder.itemView.context).load(article.image_url).into(holder.binding.imageview)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }
}

class MainViewHolder(val binding: AdapterNewsBinding) : RecyclerView.ViewHolder(binding.root) {
}