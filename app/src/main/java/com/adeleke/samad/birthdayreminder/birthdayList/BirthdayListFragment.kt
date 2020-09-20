package com.adeleke.samad.birthdayreminder.birthdayList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentBirthdayListBinding
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.navigateToMain
import kotlinx.android.synthetic.main.fragment_birthday_list.*

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
//        viewModel.recyclerAdapter.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                Log.d(TAG, "Sucessss")
//                binding.birthdayRecyclerView.adapter = it
//            }
//        })

        binding.addNewBirthdayFab.setOnClickListener {
            //navigateToDetail("-1")
//            viewModel.populateRecyclerView()
            val allBirthdays = FirebaseUtil.getInstance(requireContext()).getAllBirthdays()
            Log.d(TAG, "size-> : ${allBirthdays.size}")
            for (v in allBirthdays) {
                Log.d(TAG, "onCreateView: $v")
            }
            Log.d(TAG, "onCreateView: ${allBirthdays.size}")
            val adapter = BirthdayListAdapter(allBirthdays)
//            adapter.data = allBirthdays
            birthdayRecyclerView.adapter = adapter
        }


        return binding.root
    }


    private fun navigateToDetail(birthdayId: String ) {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        val navController = navHostFragment.navController
        val action = BirthdayListFragmentDirections.actionBirthdayListFragmentToBirthdayDetail(birthdayId = birthdayId)
        navController.navigate(action)
    }



}