package com.adeleke.samad.birthdayreminder.archiveList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.birthdayreminder.R
//import com.adeleke.samad.birthdayreminder.birthdayList.BirthdayListFragmentDirections
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.util.colorMap
import com.adeleke.samad.birthdayreminder.util.getSimpleDate
import com.google.android.material.snackbar.Snackbar

class ArchiveListAdapter(activity: FragmentActivity) :
    RecyclerView.Adapter<ArchiveListAdapter.ArchiveViewHolder>() {
    var data = mutableListOf<Birthday>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("adapter", "data set: ${data.size}")
        }
    private var myActivity = activity

    // Variables for implementing the archive functionality
    private var archivedItem: Birthday? =  null
    private var archivedPosition = 0


    inner class ArchiveViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.birthdayNameItem)
        private val dobTv: TextView = itemView.findViewById(R.id.birthdayDateItem)
        private val imageView: ImageView = itemView.findViewById(R.id.birthdayColorItem)

        init {
            itemView.setOnClickListener {
                val currentBirthdayId = data[adapterPosition].id
            }
        }

        fun bind(item: Birthday) {
            nameTv.text = item.name
            dobTv.text = item.getSimpleDate()
            val month = item.monthOfBirth
            if (month in colorMap.keys) {
                colorMap[item.monthOfBirth]?.let { imageView.setImageResource(it) }
            } else {
                colorMap["default"]?.let { imageView.setImageResource(it) }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.archive_item, parent, false)
        return ArchiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        val birthdayItem = data[position]
        holder.bind(birthdayItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun deleteItem(position: Int, viewHolder: RecyclerView.ViewHolder): Boolean {
        var deleteWasUndone = false
        archivedItem = data[position]
        archivedPosition = position

        data.removeAt(position)
        notifyItemRemoved(position)

        Snackbar.make(viewHolder.itemView, myActivity.getString(R.string.birthday_archived), Snackbar.LENGTH_LONG).setAction(myActivity.getString(
            R.string.undo_snackbar_message)) {
            data.add(archivedPosition, archivedItem!!)
            notifyItemInserted(archivedPosition)
            deleteWasUndone = true
        }.setActionTextColor(myActivity.resources.getColor(R.color.yellow))
            .show()

        return deleteWasUndone
    }

}