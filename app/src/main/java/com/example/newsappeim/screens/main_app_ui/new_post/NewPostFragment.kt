package com.example.newsappeim.screens.main_app_ui.new_post

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsappeim.BuildConfig
import com.example.newsappeim.R
import com.example.newsappeim.databinding.FragmentNewPostBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var latestTmpUri: Uri? = null

    private var imageView: ImageView? = null

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                Log.w(TAG, "1")
                Glide
                    .with(requireContext())
                    .load(uri)
                    .into(imageView!!)
            }
        }
    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.w(TAG, "2")
                Glide
                    .with(requireContext())
                    .load(uri)
                    .into(imageView!!)
            }
        }

    companion object {
        const val TAG = "NewPostFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(NewPostViewModel::class.java)

        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.imageView = binding.imageview

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        this.binding.takePhotoButton.setOnClickListener {
            takeImage()
        }

        this.binding.getPhotoFromFilesButton.setOnClickListener {
            selectImageFromGallery()
        }

        this.binding.clearPhoto.setOnClickListener {
            imageView!!.setImageResource(R.drawable.ic_outline_broken_image_24)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
}