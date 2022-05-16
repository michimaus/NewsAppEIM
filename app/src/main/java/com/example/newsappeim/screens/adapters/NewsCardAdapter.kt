package com.example.newsappeim.screens.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newsappeim.data.model.ApiNewsModelView
import com.example.newsappeim.data.model.NewsStatusLike
import com.example.newsappeim.databinding.AdapterNewsBinding
import com.example.newsappeim.screens.main_app_ui.NewsListViewModel
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

    private var news = mutableListOf<ApiNewsModelView>()
    private lateinit var newsViewModel: NewsListViewModel

    fun setNewsModelList(apiNews: List<ApiNewsModelView>, newsViewModel: NewsListViewModel) {
        this.news = apiNews.toMutableList()
        this.newsViewModel = newsViewModel
        notifyDataSetChanged()
    }

    fun handleObservedLike(articleToLike: NewsStatusLike) {
        if (!articleToLike.hasStatusChange) {
            return
        }
        news[articleToLike.indexInList].didUserLike = articleToLike.hasUserLike
        news[articleToLike.indexInList].didUserSaved = false

        notifyItemChanged(articleToLike.indexInList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterNewsBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val article = news[position].apiNewsModelWeb
        val didLike = news[position].didUserLike
        val didSave = news[position].didUserSaved

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

            chip1.shapeAppearanceModel.withCornerSize(16f)
            chip2.shapeAppearanceModel.withCornerSize(16f)


            holder.binding.chipGroup.addView(chip1)
            holder.binding.chipGroup.addView(chip2)

            article.keyWords?.forEach {
                val chipAux = Chip(holder.binding.chipGroup.context)

                chipAux.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])
                chipAux.text = "#$it"
                chipAux.shapeAppearanceModel.withCornerSize(16f)

                holder.binding.chipGroup.addView(chipAux)
            }
        }

        if (didLike) {
            holder.binding.likeButton.visibility = View.GONE
            holder.binding.likeButtonFilled.visibility = View.VISIBLE
        } else {
            holder.binding.likeButton.visibility = View.VISIBLE
            holder.binding.likeButtonFilled.visibility = View.GONE
        }

        holder.binding.likeButton.setOnClickListener {
            this.newsViewModel.likePost(article, position)
        }

        holder.binding.likeButtonFilled.setOnClickListener {
            this.newsViewModel.likePost(article, position)
        }

        if (article.creator?.isNotEmpty() == true) {
            holder.binding.authorsTextView.text = "Written by:"

            article.creator.forEach {
                holder.binding.authorsTextView.append("\n \u2022 " + it)
            }
        } else {
            holder.binding.authorsTextView.text = "Written by:"
            holder.binding.authorsTextView.append("\n \u2022 " + "Well known author")
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