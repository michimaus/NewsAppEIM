package com.example.newsappeim.screens.main_app_ui.hot_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newsappeim.databinding.FragmentHotNewsBinding

class HotNewsFragment : Fragment() {

    private var _binding: FragmentHotNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(HotNewsViewModel::class.java)

        _binding = FragmentHotNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHotNews
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}