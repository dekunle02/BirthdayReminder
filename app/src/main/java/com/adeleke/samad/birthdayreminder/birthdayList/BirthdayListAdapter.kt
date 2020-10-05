package com.adeleke.samad.birthdayreminder.birthdayList

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.birthdayDetail.BirthdayDetailActivity
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.model.getSimpleDate
import com.adeleke.samad.birthdayreminder.util.ITEM_DETAIL_TAG
import com.adeleke.samad.birthdayreminder.util.colorMap

class BirthdayListAdapter(activity: FragmentActivity) :
    RecyclerView.Adapter<BirthdayListAdapter.BirthdayViewHolder>() {
    private val TAG = javaClass.simpleName
    var data = mutableListOf<Birthday>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var myActivity = activity

    // Variables for implementing the archive functionality
    private var archivedItem: Birthday? = null
    private var archivedPosition = 0


    inner class BirthdayViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.birthdayNameItem)
        private val dobTv: TextView = itemView.findViewById(R.id.birthdayDateItem)
        private val imageView: ImageView = itemView.findViewById(R.id.birthdayColorItem)

        init {
            itemView.setOnClickListener {
                val currentBirthdayId = data[adapterPosition].id
                navigateToDetail(currentBirthdayId!!)
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
            val intent = Intent(myActivity.applicationContext, BirthdayDetailActivity::class.java)
            intent.putExtra(ITEM_DETAIL_TAG, birthdayId)
            myActivity.startActivity(intent)
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