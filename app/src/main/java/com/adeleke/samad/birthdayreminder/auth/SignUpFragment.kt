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
import com.adeleke.samad.birthdayreminder.*
import com.adeleke.samad.birthdayreminder.databinding.FragmentSignUpBinding
import com.adeleke.samad.birthdayreminder.network.googleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

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

        binding.haveAccountButton.setOnClickListener {
            navigateToSignIn()
        }

        binding.signUpEmailEditText.setOnEditorActionListener { p0, p1, p2 ->
            binding.signUpPasswordEditText.requestFocus()
            true
        }

        binding.signUpPasswordEditText.setOnEditorActionListener { textView, i, keyEvent ->
            hideSoftKeyboard(textView)
            viewModel.register()
            true
        }

        binding.signInWithGoogleButton.setOnClickListener { it ->
            googleSignIn()
        }

        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.signUpRootLayout.makeSimpleSnack(message!!)
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


    private fun navigateToSignIn() {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_auth) as NavHostFragment
        val navController = navHostFragment.navController
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        navController.navigate(action)
    }

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


}
