package com.example.newsappeim.screens.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isEmpty
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newsappeim.R
import com.example.newsappeim.data.model.ApiNewsModel
import com.example.newsappeim.databinding.ModalBottomSheetNewsDetailsBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.util.*

class NewsDetailBottomSheet : BottomSheetDialogFragment() {

    private var binding: ModalBottomSheetNewsDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalBottomSheetNewsDetailsBinding.inflate(inflater, container, false)

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

        const val LipsumText =
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
                    "Why do we use it?\n" +
                    "\n" +
                    "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
                    "\n" +
                    "Where does it come from?\n" +
                    "\n" +
                    "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
                    "\n" +
                    "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.\n"
    }

    fun setNewsData(article: ApiNewsModel) {
        binding!!.title.text = article.title
        binding!!.articleDate.text = article.pubDate


        if (article.content != null && article.content != "") {
            binding!!.articleContent.text = article.content
        } else {
            binding!!.articleContent.text = LipsumText
        }

        if (article.link != null && article.link != "") {
            binding!!.articleExternalLink.text = article.link
            binding!!.articleExternalLink.setOnClickListener {
                val newBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link))
                startActivity(newBrowserIntent)
            }
        }

        if (binding!!.chipGroup.isEmpty()) {
            val rnd = Random()
            val chip1 = Chip(binding!!.chipGroup.context)
            val chip2 = Chip(binding!!.chipGroup.context)

            chip1.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])
            chip2.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])

            chip1.text = "#tag1"
            chip2.text = "#tag2"

            chip1.shapeAppearanceModel.withCornerSize(16f)
            chip2.shapeAppearanceModel.withCornerSize(16f)


            binding!!.chipGroup.addView(chip1)
            binding!!.chipGroup.addView(chip2)

            article.keyWords?.forEach {
                val chipAux = Chip(binding!!.chipGroup.context)

                chipAux.chipBackgroundColor = ColorStateList.valueOf(colorsOfChips[rnd.nextInt(colorsOfChips.size)])
                chipAux.text = "#$it"
                chipAux.shapeAppearanceModel.withCornerSize(16f)

                binding!!.chipGroup.addView(chipAux)
            }
        }

        if (article.creator?.isNotEmpty() == true) {
            binding!!.authorsTextView.text = "Written by:"

            article.creator.forEach {
                binding!!.authorsTextView.append("\n \u2022 " + it)
            }
        } else {
            binding!!.authorsTextView.text = "Written by:"
            binding!!.authorsTextView.append("\n \u2022 " + "Well known author")
        }

        if (article.image_url != null) {
            binding!!.imageview.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide
                .with(binding!!.root.context)
                .load(article.image_url)
                .error(Glide.with(binding!!.imageview).load(R.drawable.ic_outline_broken_image_24))
                .into(binding!!.imageview)
        }

        var barData = emptyArray<BarEntry>()
        var statusData = emptyArray<RadarEntry>()

        val myRand = kotlin.random.Random(com.google.firebase.Timestamp.now().seconds)

        for (i in 1..5) {
            val newValue = ((myRand.nextInt() % 50) + 50).toFloat()
            barData += BarEntry(i.toFloat(), newValue)
            statusData += RadarEntry(newValue, "")
        }

        val barChart = binding!!.barChart
        barChart.legend.isEnabled = true

        barChart.axisLeft.setDrawGridLines(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            barChart.outlineAmbientShadowColor = Color.rgb(200, 0, 150)
        }
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        val barChartDescription = Description()
        barChartDescription.text = "Bar chart"
        barChart.description = barChartDescription
        val barDataSet = BarDataSet(barData.toMutableList(), "Bar chart")
        barDataSet.color = Color.rgb(200, 100, 150)
        barChart.data = BarData(barDataSet)


        val statusChart = binding!!.statusChart
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            statusChart.outlineAmbientShadowColor = Color.rgb(200, 100, 150)
        }

        statusChart.legend.isEnabled = true
        val statusChartDescription = Description()
        statusChartDescription.text = "Status chart"
        statusChart.description = statusChartDescription
        val radarDataSet = RadarDataSet(statusData.toMutableList(), "Status chart")
        radarDataSet.color = Color.rgb(200, 100, 150)
        radarDataSet.fillColor = Color.rgb(200, 100, 150)
        radarDataSet.setDrawFilled(true)
        statusChart.data = RadarData(radarDataSet)

    }

}
