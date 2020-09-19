package com.adeleke.samad.birthdayreminder.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentSignInBinding
import com.adeleke.samad.birthdayreminder.hideSoftKeyboard
import com.adeleke.samad.birthdayreminder.makeSimpleSnack
import com.adeleke.samad.birthdayreminder.navigateToMain

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

        binding.signInEmailEditText.setOnEditorActionListener { p0, p1, p2 ->
            binding.signInPasswordEditText.requestFocus()
            true
        }

        binding.signInPasswordEditText.setOnEditorActionListener { textView, i, keyEvent ->
            hideSoftKeyboard(textView)
            viewModel.signIn()
            true
        }

        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.signInRootLayout.makeSimpleSnack(message!!)
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


}