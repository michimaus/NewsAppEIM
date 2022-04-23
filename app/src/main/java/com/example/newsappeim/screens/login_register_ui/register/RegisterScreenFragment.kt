package com.example.newsappeim.screens.login_register_ui.register

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
import com.example.newsappeim.databinding.RegisterScreenFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterScreenFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterScreenFragment()
    }

    private lateinit var viewModel: RegisterScreenViewModel

    private lateinit var binding: RegisterScreenFragmentBinding

    private lateinit var goToLoginButton: Button
    private lateinit var registerButton: Button

    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var repeatPasswordTextField: EditText

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_screen_fragment, container, false)

        goToLoginButton = view.findViewById(R.id.button_go_to_register)
        registerButton = view.findViewById(R.id.button_register)

        emailTextField = view.findViewById(R.id.register_username)
        passwordTextField = view.findViewById(R.id.register_password)
        repeatPasswordTextField = view.findViewById(R.id.register_repeat_password)

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerScreenFragment_to_loginScreenFragment)
        }

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener {
            var newCreatedUserEmail: String = emailTextField.text.toString()
            var newCreatedUserPassword: String = passwordTextField.text.toString()

            mAuth!!.createUserWithEmailAndPassword(newCreatedUserEmail, newCreatedUserPassword).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this.activity, "Successfully Registered", Toast.LENGTH_SHORT).show()
                    (this.activity as LoginRegisterActivity).onActionTriggerNavigateToMainApp(view);
                } else {
                    Toast.makeText(this.activity, "Failed!! " + it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}