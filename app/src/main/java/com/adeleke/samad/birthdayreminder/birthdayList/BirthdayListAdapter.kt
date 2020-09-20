package com.adeleke.samad.birthdayreminder.birthdayList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.util.colorMap
import com.adeleke.samad.birthdayreminder.util.getSimpleDate

class BirthdayListAdapter(data: MutableList<Birthday>) : RecyclerView.Adapter<BirthdayListAdapter.BirthdayViewHolder>() {
    var data = data
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("adapter", "data set: ${data.size}")
        }

    class BirthdayViewHolder private constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.birthdayNameItem)
        val dobTv: TextView = itemView.findViewById(R.id.birthdayDateItem)
        val imageView: ImageView = itemView.findViewById(R.id.birthdayColorItem)

        fun bind(item: Birthday) {
            nameTv.text = item.name
            dobTv.text = item.getSimpleDate()
            val month = item.monthOfBirth
            if (month in colorMap.keys) {
                colorMap.get(item.monthOfBirth)?.let { imageView.setImageResource(it) }
            } else {
                colorMap.get("default")?.let { imageView.setImageResource(it) }
            }
        }

        companion object {
            fun from(parent: ViewGroup): BirthdayViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.birthday_item, parent, false)

                return BirthdayViewHolder(view)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        return BirthdayViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        val birthdayItem = data[position]
        holder.bind(birthdayItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}