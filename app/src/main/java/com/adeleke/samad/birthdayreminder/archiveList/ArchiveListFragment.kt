package com.adeleke.samad.birthdayreminder.archiveList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.birthdayList.BirthdayListAdapter
import com.adeleke.samad.birthdayreminder.birthdayList.BirthdayListViewModel
import com.adeleke.samad.birthdayreminder.databinding.FragmentArchiveListBinding
import com.adeleke.samad.birthdayreminder.databinding.FragmentBirthdayListBinding
import com.adeleke.samad.birthdayreminder.util.makeSimpleSnack


class ArchiveListFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: FragmentArchiveListBinding
    private val viewModel: ArchiveViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_archive_list, container, false)

        // Set up recyclerView
        binding.archiveRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ArchiveListAdapter(requireActivity())
        binding.archiveRecyclerView.adapter = adapter
        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.data = it
            }
        })

        // Observables
        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.constraintArchiveList.makeSimpleSnack(message!!)
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.populateRecyclerData()
    }


}