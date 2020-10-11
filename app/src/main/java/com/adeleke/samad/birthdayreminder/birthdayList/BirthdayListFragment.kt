package com.adeleke.samad.birthdayreminder.birthdayList

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.birthdayDetail.BirthdayDetailActivity
import com.adeleke.samad.birthdayreminder.databinding.FragmentBirthdayListBinding
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.ITEM_DETAIL_TAG
import com.adeleke.samad.birthdayreminder.util.NEW_BIRTHDAY_ID
import com.adeleke.samad.birthdayreminder.util.makeSimpleSnack
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class BirthdayListFragment : Fragment() {
    private lateinit var binding: FragmentBirthdayListBinding
    private val viewModel: BirthdayListViewModel by viewModels()

    private lateinit var firebaseUtil: FirebaseUtil
    private lateinit var user: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_birthday_list, container, false)
        binding.viewmodel = viewModel

        // Have access to Toolbar
        setHasOptionsMenu(true)

        // Make it show the empty birthdays by default
        binding.birthdayRecyclerView.visibility = View.GONE
        binding.emptyListLayout.visibility = View.VISIBLE


        // Set up recyclerView
        binding.birthdayRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = BirthdayListAdapter(requireActivity())
        binding.birthdayRecyclerView.adapter = adapter
        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.data = it
                if (it.isEmpty()) {
                    binding.birthdayRecyclerView.visibility = View.GONE
                    binding.emptyListLayout.visibility = View.VISIBLE
                } else {
                    binding.birthdayRecyclerView.visibility = View.VISIBLE
                    binding.emptyListLayout.visibility = View.GONE
                }
            }
        })

        // Set up the touch helper for swiping items on the recyclerview and adding them to the Archive List
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            val colorArchiveBackground = ColorDrawable(resources.getColor(R.color.archiveColor))
            val archiveIcon =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_archive_white) }!!

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                val pos = viewHolder.adapterPosition
                val birthdaySwiped = viewModel.recyclerData.value!![pos]
                viewModel.archiveBirthdayAtPosition(pos)
                adapter.notifyItemRemoved(pos)
                viewModel.populateRecyclerData()
                Snackbar.make(
                    binding.constraintBirthdayList,
                    getString(R.string.birthday_archived),
                    Snackbar.LENGTH_LONG
                ).setAction(
                    getString(R.string.undo_snackbar_message)
                ) {
                    viewModel.insertBirthday(pos, birthdaySwiped)
                    adapter.notifyItemInserted(pos)
                }.setActionTextColor(resources.getColor(R.color.yellow))
                    .show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical =
                    (viewHolder.itemView.height - archiveIcon.intrinsicHeight) / 2
                // swiped towards left
                if (dX > 0) {
                    colorArchiveBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    archiveIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + archiveIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                    // swiped towards left

                } else {

                    colorArchiveBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    archiveIcon.setBounds(
                        itemView.right - iconMarginVertical - archiveIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    archiveIcon.level = 0
                }

                colorArchiveBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                archiveIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.birthdayRecyclerView)


        // Click Listeners
        binding.addNewBirthdayFab.setOnClickListener {
            firebaseUtil = FirebaseUtil.getInstance(requireContext())
            user = FirebaseAuth.getInstance().currentUser!!

            if (user.isEmailVerified) {
                navigateToNewBirthday()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                val dialogView: View =
                    LayoutInflater.from(binding.addNewBirthdayFab.context).inflate(R.layout.dialog_resend_verification, container, false)
                builder.setView(dialogView)
                val alertDialog = builder.create()
                alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog.show()
                val btnConfirmSubmit: MaterialButton = dialogView.findViewById(R.id.btn_resend_verification)
                btnConfirmSubmit.setOnClickListener {
                    alertDialog.dismiss()
                    firebaseUtil.sendVerificationEmail()
                    binding.addNewBirthdayFab.makeSimpleSnack("verification Email Sent!")
                }
            }
        }

        // Observables
        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.constraintBirthdayList.makeSimpleSnack(message!!)
        })

        return binding.root
    }


    // Each time the view is created, the viewModel should re populate the recyclerView Data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.populateRecyclerData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            showFilteringPopUpMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    // Shows the popUp for filtering the birthday List recyclerView
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

    // Navigates to the Birthday Detail Activity with a new Birthday ID so that a new Birthday object is created and shown to the user
    private fun navigateToNewBirthday() {
        val intent = Intent(activity, BirthdayDetailActivity::class.java)
        intent.putExtra(ITEM_DETAIL_TAG, NEW_BIRTHDAY_ID)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.populateRecyclerData()
    }

}