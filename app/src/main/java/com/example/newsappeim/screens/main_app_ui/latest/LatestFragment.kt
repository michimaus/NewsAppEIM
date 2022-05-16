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
import com.example.newsappeim.screens.main_app_ui.NewsListViewModel
import com.example.newsappeim.services.NewsService

class LatestFragment : Fragment() {

    private var _binding: FragmentLatestBinding? = null

    private val TAG: String = "LatestFragment"

    private val binding get() = _binding!!
    lateinit var newsListViewModel: NewsListViewModel
    private val newsService = NewsService.getInstance()
    private val newsAdapter = NewsCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newsListViewModel =
            ViewModelProvider(this, LatestViewModelFactory(NewsRepository(newsService)))[NewsListViewModel::class.java]

        _binding = FragmentLatestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerview.adapter = newsAdapter

        newsListViewModel.errorMessage.observe(viewLifecycleOwner) {
            Log.wtf(TAG, it)
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        }

        newsListViewModel.latestList.observe(viewLifecycleOwner) {
            newsAdapter.setNewsModelList(it, newsListViewModel)
            binding.progressBar.visibility = View.GONE
        }

        newsListViewModel.loading.observe(viewLifecycleOwner) {
            Log.wtf(TAG, "Should be loading $it")
            if (it) {
                print("Mergeeeeee")
            } else {
                print("Gata.....")
            }
        }

        newsListViewModel.articleToLike.observe(viewLifecycleOwner) {
            newsAdapter.handleObservedLike(it)
        }

        newsListViewModel.getLatest()

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