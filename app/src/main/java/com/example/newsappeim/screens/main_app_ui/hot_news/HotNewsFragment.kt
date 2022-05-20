package com.example.newsappeim.screens.main_app_ui.hot_news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newsappeim.databinding.FragmentHotNewsBinding
import com.example.newsappeim.repositories.NewsRepository
import com.example.newsappeim.screens.adapters.NewsCardAdapter
import com.example.newsappeim.screens.main_app_ui.NewsListViewModel
import com.example.newsappeim.services.NewsService

class HotNewsFragment : Fragment() {

    private var _binding: FragmentHotNewsBinding? = null

    private val TAG: String = "HotNewsFragment"

    private val binding get() = _binding!!
    lateinit var hotNewsViewModel: NewsListViewModel
    private val newsService = NewsService.getInstance()
    private val newsAdapter = NewsCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hotNewsViewModel =
            ViewModelProvider(this, HotNewsViewModelFactory(NewsRepository(newsService)))[NewsListViewModel::class.java]
        newsAdapter.setActualContext(requireContext())

        _binding = FragmentHotNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerview.adapter = newsAdapter

        hotNewsViewModel.errorMessage.observe(viewLifecycleOwner) {
            Log.wtf(TAG, it)
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        }

        hotNewsViewModel.obtainedNewsList.observe(viewLifecycleOwner) {
            newsAdapter.setNewsModelList(it, hotNewsViewModel)
            binding.progressBar.visibility = View.GONE
        }

        hotNewsViewModel.loading.observe(viewLifecycleOwner) {
            Log.wtf(TAG, "Should be loading $it")
            if (it) {
                print("Mergeeeeee")
            } else {
                print("Gata.....")
            }
        }

        hotNewsViewModel.articleToLike.observe(viewLifecycleOwner) {
            newsAdapter.handleObservedLike(it)
        }

        hotNewsViewModel.articleToSave.observe(viewLifecycleOwner) {
            newsAdapter.handleObservedSave(it)
        }

        hotNewsViewModel.commentsToGet.observe(viewLifecycleOwner) {
            newsAdapter.handleObservedComments(it)
        }

        hotNewsViewModel.getHotNews()

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