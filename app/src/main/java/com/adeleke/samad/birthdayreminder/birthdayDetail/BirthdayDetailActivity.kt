package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.ActivityBirthdayDetailBinding
import com.adeleke.samad.birthdayreminder.util.CONTACT_REQUEST_CODE
import com.adeleke.samad.birthdayreminder.util.ITEM_DETAIL_TAG
import com.google.android.material.datepicker.MaterialDatePicker

class BirthdayDetailActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    enum class Mode { EDIT, VIEW }

    private lateinit var binding: ActivityBirthdayDetailBinding
    private lateinit var viewModel: BirthdayDetailViewModel
    private var mode = Mode.VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirthdayDetailBinding.inflate(layoutInflater)


        // Setting up Toolbar
        this.setSupportActionBar(binding.detailToolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // EditText settings
        binding.editNameEditText.setOnKeyListener { _, _, _ ->
            if (!binding.nameEdit.text.toString().isNullOrEmpty()) {
                (binding.editNameEditText).error = null
            }
            false
        }
        binding.editYearEditText.setOnKeyListener { _, _, _ ->
            if (!binding.yearEdit.text.toString().isNullOrEmpty()) {
                (binding.editYearEditText).error = null
            }
            false
        }
        binding.editPhoneEditText.setOnKeyListener { _, _, _ ->
            if (!binding.phoneEdit.text.toString().isNullOrEmpty()) {
                (binding.editPhoneEditText).error = null
            }
            false
        }
        binding.editMessageEditText.setOnKeyListener { _, _, _ ->
            if (!binding.messageEdit.text.toString().isNullOrEmpty()) {
                (binding.editMessageEditText).error = null
            }
            false
        }

        // Receive id for item to display and pass into viewModel
        val intent = intent
        val passedArgId = intent.getStringExtra(ITEM_DETAIL_TAG)
        val viewModelFactory =
            BirthdayDetailViewModel.BirthdayDetailViewModelFactory(passedArgId!!, application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(BirthdayDetailViewModel::class.java)
        binding.viewmodel = viewModel

        //Click handlers
        binding.editYearEditText.setOnClickListener {
            showDatePicker()
        }
        binding.calendarButton.setOnClickListener {
            showDatePicker()
        }
        binding.loadFromContactButton.setOnClickListener {
            getContact()
        }

        // observables
        viewModel.fieldName.observe(this, Observer {
            binding.nameEdit.setText(it)
        })
        viewModel.fieldDate.observe(this, Observer {
            binding.yearEdit.setText(it)
        })
        viewModel.fieldPhoneNumber.observe(this, Observer {
            binding.phoneEdit.setText(it)
        })
        viewModel.fieldMessage.observe(this, Observer {
            binding.messageEdit.setText(it)
        })

        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                if (inputFieldsAreCorrect()) {
                    viewModel.addOrUpdateBirthday()
                    onBackPressed()
                }
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
        picker.show(supportFragmentManager, TAG)
    }

    private fun inputFieldsAreCorrect(): Boolean {
        val enteredName = binding.nameEdit.text.toString()
        val enteredDate = binding.yearEdit.text.toString()
        val enteredNumber = binding.phoneEdit.text.toString()
        val enteredMessage = binding.messageEdit.text.toString()

        return if (enteredName.isNullOrEmpty()) {
            binding.editNameEditText.error = getString(R.string.cannot_be_blank)
            false
        } else if (enteredDate.isNullOrEmpty()) {
            binding.editYearEditText.error = getString(R.string.cannot_be_blank)
            false
        } else if (enteredNumber.isNullOrEmpty()) {
            binding.editPhoneEditText.error = getString(R.string.cannot_be_blank)
            false
        } else if (enteredMessage.isNullOrEmpty()) {
            binding.editMessageEditText.error = getString(R.string.cannot_be_blank)
            false
        } else {
            true
        }
    }

    private fun getContact() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CONTACT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTACT_REQUEST_CODE && resultCode == RESULT_OK) {
            val contactUri = data!!.data
            viewModel.loadFieldsWithContact(contactUri)
        }
    }


}