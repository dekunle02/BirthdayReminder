package com.adeleke.samad.birthdayreminder.archiveList

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.databinding.FragmentArchiveListBinding
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

        setHasOptionsMenu(true)

        // Set up recyclerView
        binding.archiveRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ArchiveListAdapter(requireActivity())
        binding.archiveRecyclerView.adapter = adapter
        viewModel.recyclerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.data = it
            }
        })

        // Set up the touch helper for swiping items on the recyclerview
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            val colorDeleteBackground = ColorDrawable(resources.getColor(R.color.deleteColor))
            val deleteIcon =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete) }!!

            val colorRestoreBackground = ColorDrawable(resources.getColor(R.color.archiveColor))
            val restoreIcon =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_restore) }!!


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                val pos = viewHolder.adapterPosition
                Log.d(TAG, "onSwiped: $pos")
                val birthdaySwiped = viewModel.recyclerData.value!![pos]

                if (swipeDirection == ItemTouchHelper.RIGHT) {
                    viewModel.restoreBirthdayFromArchive(birthdaySwiped)
                    adapter.notifyItemRemoved(pos)
                    viewModel.populateRecyclerData()
                    binding.archiveRecyclerView.makeSimpleSnack(getString(R.string.birthday_restored))

                } else if (swipeDirection == ItemTouchHelper.LEFT) {
                    val alertDialogBuilder = AlertDialog.Builder(context!!)
                    alertDialogBuilder.setIcon(R.drawable.ic_warning)
                        .setCancelable(true)
                        .setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.are_you_sure_delete))
                        .setPositiveButton(
                            getString(R.string.yes)
                        ) { p0, p1 ->
                            viewModel.finalDeleteBirthday(birthdaySwiped)
                            adapter.notifyItemRemoved(pos)
                            viewModel.populateRecyclerData()
                        }
                        .setNegativeButton(
                            getString(R.string.no)
                        ) { dialogInterface, p1 ->
                            dialogInterface!!.cancel()
                            viewModel.populateRecyclerData()
                        }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()

                }

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
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                // swiped towards right
                if (dX > 0) {
                    colorRestoreBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    restoreIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + restoreIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                    restoreIcon.level = 0

                    colorRestoreBackground.draw(c)
                    // swiped towards left
                } else {

                    colorDeleteBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.level = 0

                    colorDeleteBackground.draw(c)
                }


                c.save()

                if (dX > 0) {
                    c.clipRect(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    restoreIcon.draw(c)
                } else {
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.draw(c)
                }

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
        itemTouchHelper.attachToRecyclerView(binding.archiveRecyclerView)


        // Observables
        viewModel.snackMessage.observe(viewLifecycleOwner, Observer { message ->
            binding.constraintArchiveList.makeSimpleSnack(message!!)
        })


        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.menu_archive, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete_all) {

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setIcon(R.drawable.ic_warning)
                .setCancelable(true)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_everything_message))
                .setPositiveButton(
                    getString(R.string.yes)
                ) { p0, p1 ->
                    viewModel.deleteAll()
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialogInterface, p1 ->
                    dialogInterface!!.cancel()
                }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.populateRecyclerData()
    }


}