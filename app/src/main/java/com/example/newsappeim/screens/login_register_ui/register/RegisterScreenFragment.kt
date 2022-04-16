package com.example.newsappeim.screens.login_register_ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.newsappeim.R
import com.example.newsappeim.databinding.RegisterScreenFragmentBinding

class RegisterScreenFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterScreenFragment()
    }

    private lateinit var viewModel: RegisterScreenViewModel

    private lateinit var binding: RegisterScreenFragmentBinding

    private lateinit var goToLoginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_screen_fragment, container, false)
        goToLoginButton = view.findViewById<Button>(R.id.button_go_to_register)
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerScreenFragment_to_loginScreenFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}