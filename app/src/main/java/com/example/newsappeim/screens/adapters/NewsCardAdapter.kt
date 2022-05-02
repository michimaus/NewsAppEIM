package com.example.newsappeim.screens.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newsappeim.data.model.NewsModel
import com.example.newsappeim.databinding.AdapterNewsBinding
import com.google.android.material.chip.Chip
import java.util.*

class NewsCardAdapter : RecyclerView.Adapter<MainViewHolder>() {

    val TAG: String = "NewsCardAdapter"

    private val colorsOfChips = listOf(
        Color.rgb(205, 240, 234),
        Color.rgb(249, 200, 249),
        Color.rgb(247, 219, 240),
        Color.rgb(190, 174, 226)
    )

    var news = mutableListOf<NewsModel>()
    fun setNewsModelList(news: List<NewsModel>) {
        this.news = news.toMutableList()
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
        holder.binding.articleShortDescription.text = article.description
        holder.binding.articleDate.text = article.pubDate

        if (holder.binding.chipGroup.isEmpty()) {
            val rnd = Random()
            val chip1 = Chip(holder.binding.chipGroup.context)
            val chip2 = Chip(holder.binding.chipGroup.context)

            chip1.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])
            chip2.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])

            chip1.text = "#tag1"
            chip2.text = "#tag2"

            holder.binding.chipGroup.addView(chip1)
            holder.binding.chipGroup.addView(chip2)

            Log.d(TAG, article.keyWords?.isNotEmpty().toString())
            Log.d(TAG, "PLM...")

            article.keyWords?.forEach {
                val chipAux = Chip(holder.binding.chipGroup.context)

                chipAux.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])
                chipAux.text = "#$it"

                holder.binding.chipGroup.addView(chipAux)
            }
        }

        if (article.creator?.isNotEmpty() == true) {
            holder.binding.authorsTextView.text = "Written by:"

            article.creator.forEach {
                holder.binding.authorsTextView.append("\n \u2022 " + it)
            }
        } else {
            holder.binding.authorsTextView.visibility = View.GONE
        }

        if (article.image_url != null) {
            holder.binding.imageview.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide
                .with(holder.itemView.context)
                .load(article.image_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imageview)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }
}

class MainViewHolder(val binding: AdapterNewsBinding) : RecyclerView.ViewHolder(binding.root)