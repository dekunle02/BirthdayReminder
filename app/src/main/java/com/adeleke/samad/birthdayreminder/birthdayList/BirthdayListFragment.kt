package com.adeleke.samad.birthdayreminder.birthdayList

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

        binding.birthdayRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = BirthdayListAdapter(requireActivity())
        binding.birthdayRecyclerView.adapter = adapter
        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.d(TAG, "size of List: ${it.size}")
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

    inner class CustomClickListener(private val position: Int) : View.OnClickListener {
        override fun onClick(p0: View?) {
            viewModel.navigateWithPosition(position)
        }
    }


}