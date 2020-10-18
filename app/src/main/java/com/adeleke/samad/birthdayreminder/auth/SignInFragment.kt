package com.adeleke.samad.birthdayreminder.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentSignInBinding
import com.adeleke.samad.birthdayreminder.network.googleSignIn
import com.adeleke.samad.birthdayreminder.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException

class SignInFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        binding.viewmodel = viewModel


        // Edit Text settings
        binding.signInEmailTI.setOnEditorActionListener { p0, p1, p2 ->
            binding.signInPasswordTI.requestFocus()
            true
        }
        binding.signInPasswordTI.setOnEditorActionListener { textView, i, keyEvent ->
            hideSoftKeyboard(textView)
            true
        }
        binding.signInEmailTI.setOnKeyListener { _, _, _ ->
            if (binding.signInEmailTI.text.toString().isEmailFormatted()) {
                (binding.signInEmailEditText).error = null
            }
            false
        }

        binding.signInPasswordTI.setOnKeyListener { _, _, _ ->
            if (binding.signInPasswordTI.text.toString().isPasswordFormatted()) {
                (binding.signInPasswordEditText).error = null
            }
            false
        }

        // Button Listeners
        binding.signInWithGoogleButton.setSize(SignInButton.SIZE_WIDE)
        binding.signInWithGoogleButton.setOnClickListener { _ ->
            binding.signInProgressBar.visibility = View.VISIBLE
            binding.logoImageView.visibility = View.INVISIBLE
            googleSignIn()
        }
        binding.signInButton.setOnClickListener {
            if (inputFieldsAreCorrect()) {
                binding.logoImageView.visibility = View.INVISIBLE
                binding.signInProgressBar.visibility = View.VISIBLE
                hideSoftKeyboard(it)
                viewModel.signIn()
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            if (binding.signInEmailTI.text.toString().isEmailFormatted()) {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setIcon(R.drawable.ic_warning)
                    .setCancelable(true)
                    .setTitle("Send password recovery email")
                    .setMessage("Do you want to send to ${binding.signInEmailTI.text.toString()}?")
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { p0, p1 ->
                        viewModel.sendForgotPasswordEmail()
                    }
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { dialogInterface, p1 ->
                        dialogInterface!!.cancel()
                    }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                binding.signInEmailEditText.error =  getString(R.string.enter_valid_email)
            }
        }


        // Observables
        viewModel.showProgressBar.observe(viewLifecycleOwner, Observer { canShow ->
            binding.signInProgressBar.visibility = if (canShow!!) View.VISIBLE else View.GONE
            binding.logoImageView.visibility = if (canShow) View.INVISIBLE else View.VISIBLE
        })

        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.mainFrameSignIn.makeSimpleSnack(message!!)
        })

        viewModel.canNavigateToMain.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Log.d(TAG, "canNavigateToMain -> True")
                requireActivity().navigateToMain()
                viewModel.doneNavigateToMain()
            }
        })

        return binding.root
    }

    // Handle Google Sign In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        binding.logoImageView.visibility = View.VISIBLE
        binding.signInProgressBar.visibility = View.INVISIBLE
        if (requestCode == GOOGLE_RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                viewModel.firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                binding.signInWithGoogleButton.makeSimpleSnack("Google sign in failed. ${e.message}")
                Log.d(TAG, "Google sign in failed + ${e.toString()}")
            }
        }
    }

    // Verify input Fields
    private fun inputFieldsAreCorrect(): Boolean {
        val enteredEmail = binding.signInEmailTI.text.toString()
        val enteredPassword = binding.signInPasswordTI.text.toString()

        return if (!enteredEmail.isEmailFormatted()) {
            binding.signInEmailEditText.error = getString(R.string.enter_valid_email)
            false
        } else if (!enteredPassword.isPasswordFormatted()) {
            binding.signInPasswordEditText.error = getString(R.string.enter_valid_password)
            false
        } else {
            true
        }
    }

}