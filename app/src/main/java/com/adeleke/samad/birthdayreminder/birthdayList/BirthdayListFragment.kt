package com.adeleke.samad.birthdayreminder.birthdayList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentBirthdayListBinding

class BirthdayListFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: FragmentBirthdayListBinding
    private val viewModel: BirthdayListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_birthday_list, container, false)
        binding.viewmodel = viewModel
        setHasOptionsMenu(true)

        binding.birthdayRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = BirthdayListAdapter(requireActivity())
        binding.birthdayRecyclerView.adapter = adapter
        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.d(TAG, "dataSet has changed!")
                adapter.data = it
            }
        })

        binding.addNewBirthdayFab.setOnClickListener {
            navigateToDetail("-1")
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.populateRecyclerData()
    }


    private fun navigateToDetail(birthdayId: String) {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        val navController = navHostFragment.navController
        val action =
            BirthdayListFragmentDirections.actionBirthdayListFragmentToBirthdayDetail(birthdayId = birthdayId)
        navController.navigate(action)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {showFilteringPopUpMenu()}
        return super.onOptionsItemSelected(item)

    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.action_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_lists, menu)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.filter_alphabetically -> viewModel.filterByAlphabetically()
                    R.id.filter_month -> viewModel.filterByMonth()
                }
                true
            }
            show()
        }
    }

}