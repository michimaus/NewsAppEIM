package com.example.newsappeim.screens.main_app_ui.latest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newsappeim.databinding.FragmentLatestBinding
import com.example.newsappeim.repositories.NewsRepository
import com.example.newsappeim.screens.adapters.NewsCardAdapter
import com.example.newsappeim.services.NewsService

class LatestFragment : Fragment() {

    private var _binding: FragmentLatestBinding? = null

    private val TAG: String = "LatestFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var latestViewModel: LatestViewModel
    private val newsService = NewsService.getInstance()
    private val newsAdapter = NewsCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        latestViewModel =
            ViewModelProvider(this, LatestViewModelFactory(NewsRepository(newsService)))[LatestViewModel::class.java]

        _binding = FragmentLatestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerview.adapter = newsAdapter

        latestViewModel.errorMessage.observe(viewLifecycleOwner) {
            Log.wtf(TAG, it)
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        }

        latestViewModel.latestList.observe(viewLifecycleOwner) {
            newsAdapter.setNewsModelList(it, latestViewModel)
            binding.progressBar.visibility = View.GONE
        }

        latestViewModel.loading.observe(viewLifecycleOwner) {
            Log.wtf(TAG, "Should be loading $it")
            if (it) {
                print("Mergeeeeee")
            } else {
                print("Gata.....")
            }
        }

        latestViewModel.articleToLike.observe(viewLifecycleOwner) {
            newsAdapter.handleObservedLike(it)
        }

        latestViewModel.getLatest()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}