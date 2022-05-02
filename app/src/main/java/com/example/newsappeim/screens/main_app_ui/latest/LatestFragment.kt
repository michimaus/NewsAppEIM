package com.example.newsappeim.screens.main_app_ui.latest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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
    val newsAdapter = NewsCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val newsService = NewsService.getInstance()

        latestViewModel =
            ViewModelProvider(this, LatestViewModelFactory(NewsRepository(newsService))).get(LatestViewModel::class.java)

        _binding = FragmentLatestBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        val textView: TextView = binding.textLatest
//        val adapterNewsBinding: RecyclerView = binding.recyclerview
        binding.recyclerview.adapter = newsAdapter

//        latestViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        latestViewModel.errorMessage.observe(viewLifecycleOwner) {
            Log.wtf(TAG, it)
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        }

        latestViewModel.latestList.observe(viewLifecycleOwner) {
//            Log.wtf(TAG, it.toString())
            newsAdapter.setNewsModelList(it.results);
            binding.progressBar.visibility = View.GONE
        }

        latestViewModel.loading.observe(viewLifecycleOwner) {
            Log.wtf(TAG, "Should be loading $it")
            if (it) {
//                binding.progressDialog.visibility = View.VISIBLE
                print("Mergeeeeee")
            } else {
//                binding.progressDialog.visibility = View.GONE
                print("Gata.....")
            }
        }


        latestViewModel.getLatest()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val newsService = NewsService.getInstance()
//        val mainRepository = NewsRepository(newsService)
//        binding.recyclerview.adapter = adapter

//        latestViewModel.latestList.observe(viewLifecycleOwner, {
//            adapter.setMovies(it)
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}