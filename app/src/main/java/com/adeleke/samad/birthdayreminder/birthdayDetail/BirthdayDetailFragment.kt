package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentBirthdayDetailBinding
import com.google.android.material.datepicker.MaterialDatePicker


class BirthdayDetailFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var binding: FragmentBirthdayDetailBinding
    private lateinit var viewModel: BirthdayDetailViewModel
    private val args: BirthdayDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_birthday_detail, container, false)

        setHasOptionsMenu(true)
        val ab = (activity as AppCompatActivity).supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_close)
        ab.setDisplayHomeAsUpEnabled(true)

        binding.yearEdit.setOnClickListener {
            showDatePicker()
        }

        // set up viewModel
        val application = requireActivity().application
        val id = args.birthdayId
        val viewModelFactory = BirthdayDetailViewModel.BirthdayDetailViewModelFactory(id, application )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BirthdayDetailViewModel::class.java)
        binding.viewmodel = viewModel


        return binding.root
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                viewModel.addOrUpdateBirthday()
                requireActivity().onBackPressed();
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            binding.yearEdit.setText(picker.headerText)
        }
        picker.show(parentFragmentManager, TAG)
    }


}