package com.adeleke.samad.birthdayreminder.views.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.viewmodels.BirthdayListViewModel

class BirthdayListFragment : Fragment() {

    companion object {
        fun newInstance() = BirthdayListFragment()
    }

    private lateinit var viewModel: BirthdayListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_birthday_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BirthdayListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}