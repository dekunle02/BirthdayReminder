package com.adeleke.samad.birthdayreminder.birthdayList

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
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.util.colorMap
import com.adeleke.samad.birthdayreminder.util.getSimpleDate

class BirthdayListAdapter(activity: FragmentActivity) :
    RecyclerView.Adapter<BirthdayListAdapter.BirthdayViewHolder>() {
    var data = mutableListOf<Birthday>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("adapter", "data set: ${data.size}")
        }
    var myActivity = activity

    inner class BirthdayViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.birthdayNameItem)
        private val dobTv: TextView = itemView.findViewById(R.id.birthdayDateItem)
        private val imageView: ImageView = itemView.findViewById(R.id.birthdayColorItem)

        init {
            itemView.setOnClickListener {
                val currentBirthdayId = data[adapterPosition].id
                navigateToDetail(currentBirthdayId)
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

        private fun navigateToDetail(birthdayId: String) {
            val navHostFragment =
                myActivity.supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
            val navController = navHostFragment.navController
            val action =
                BirthdayListFragmentDirections.actionBirthdayListFragmentToBirthdayDetail(birthdayId = birthdayId)
            navController.navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.birthday_item, parent, false)
        return BirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        val birthdayItem = data[position]
        holder.bind(birthdayItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}