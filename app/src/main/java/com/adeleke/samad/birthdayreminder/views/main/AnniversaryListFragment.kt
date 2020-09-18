package com.adeleke.samad.birthdayreminder.views.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.viewmodels.AnniversaryListViewModel

class AnniversaryListFragment : Fragment() {

    companion object {
        fun newInstance() = AnniversaryListFragment()
    }

    private lateinit var viewModel: AnniversaryListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anniversary_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AnniversaryListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}