package com.adeleke.samad.birthdayreminder.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentSignUpBinding
import com.adeleke.samad.birthdayreminder.network.googleSignIn
import com.adeleke.samad.birthdayreminder.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout


class SignUpFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.viewmodel = viewModel


        binding.haveAnAccountButton.setOnClickListener {
            navigateToSignIn()
        }

        // Edit Text settings
        binding.signUpEmailTI.setOnEditorActionListener { textView, i, keyEvent ->
            binding.signUpPasswordTI.requestFocus()
            true
        }
        binding.signUpPasswordTI.setOnEditorActionListener { textView, i, keyEvent ->
            hideSoftKeyboard(textView)
            true
        }

        binding.signUpEmailTI.setOnKeyListener { _, _, _ ->
            if (binding.signUpEmailTI.text.toString().isEmailFormatted()) {
                (binding.signUpEmailEditText).error = null
            }
            false
        }

        binding.signUpPasswordTI.setOnKeyListener { _, _, _ ->
            if (binding.signUpPasswordTI.text.toString().isPasswordFormatted()) {
                (binding.signUpPasswordEditText).error = null
            }
            false

        }

        binding.confirmPasswordTI.setOnKeyListener { _, _, _ ->
            if (binding.confirmPasswordTI.text.toString() == binding.signUpEmailTI.text.toString()) {
                binding.confirmPasswordEditText.error = null
            }
            false
        }

        // Button CLick Listeners
        binding.signUpWithGoogleButton.setSize(SignInButton.SIZE_WIDE)
        binding.signUpWithGoogleButton.setOnClickListener { it ->
            binding.signUpProgressBar.visibility = View.VISIBLE
            googleSignIn()
        }

        binding.signUpButton.setOnClickListener {
            if (inputFieldsAreCorrect()) {
                binding.signUpProgressBar.visibility = View.VISIBLE
                viewModel.register()
            }
        }

        // Observables
        viewModel.showProgressBar.observe(viewLifecycleOwner, Observer { canShow ->
            binding.signUpProgressBar.visibility = if (canShow!!) View.VISIBLE else View.GONE
            binding.logoImageView.visibility = if(canShow) View.INVISIBLE else View.VISIBLE
        })

        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.mainFrameSignUp.makeSimpleSnack(message!!)
        })

        viewModel.canNavigateToMain.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Log.d(TAG, "canNavigateToMain -> True")
                requireActivity().navigateToMain()
                viewModel.doneNavigateToMain()
            }
        })
        //

        return binding.root
    }


    private fun navigateToSignIn() {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_auth) as NavHostFragment
        val navController = navHostFragment.navController
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        navController.navigate(action)
    }

    // Handle Google Sign In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                viewModel.firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "Google sign in failed + ${e.toString()}")
            }
        }
    }

    // Verify input Fields
    private fun inputFieldsAreCorrect(): Boolean {
        val enteredEmail = binding.signUpEmailTI.text.toString()
        val enteredPassword = binding.signUpPasswordTI.text.toString()
        val confirmedPassword = binding.confirmPasswordTI.text.toString()

        return if (!enteredEmail.isEmailFormatted()) {
            binding.signUpEmailEditText.error = getString(R.string.enter_valid_email)
            false
        } else if (!enteredPassword.isPasswordFormatted()) {
            binding.signUpPasswordEditText.error = getString(R.string.enter_valid_password)
            false
        } else if (!confirmedPassword.isPasswordFormatted()){
            binding.confirmPasswordEditText.error = getString(R.string.enter_valid_password)
            false
        } else if (confirmedPassword != enteredPassword) {
            binding.confirmPasswordEditText.error = getString(R.string.password_not_match)
            false
        }
        else {
            true
        }
    }

}
