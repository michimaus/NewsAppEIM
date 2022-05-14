package com.example.newsappeim.screens.login_register_ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.newsappeim.LoginRegisterActivity
import com.example.newsappeim.R
import com.example.newsappeim.databinding.LoginScreenFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginScreenFragment : Fragment() {

    companion object {
        fun newInstance() = LoginScreenFragment()
    }

    private lateinit var viewModel: LoginScreenViewModel

    private lateinit var binding: LoginScreenFragmentBinding

    private lateinit var goToRegisterButton: Button
    private lateinit var loginButton: Button

    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_screen_fragment, container, false)

        goToRegisterButton = view.findViewById<Button>(R.id.button_go_to_register)
        loginButton = view.findViewById<Button>(R.id.button_login)

        emailTextField = view.findViewById(R.id.login_username)
        passwordTextField = view.findViewById(R.id.login_password)

        return view;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToRegisterButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreenFragment_to_registerScreenFragment)
        }

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener {
            var userEmail: String = emailTextField.text.toString()
            var userPassword: String = passwordTextField.text.toString()

            mAuth!!.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this.activity, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                    (this.activity as LoginRegisterActivity).onActionTriggerNavigateToMainApp(view);
                } else {
                    Toast.makeText(this.activity, "Failed! " + it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}