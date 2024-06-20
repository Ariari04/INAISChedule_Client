package com.example.scheduleviewer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class DayAdapter(private val daysOfWeek: List<String>, private val listener: OnDayClickListener) :
    RecyclerView.Adapter<DayAdapter.ViewHolder>() {

    private var selectedPosition = getCurrentDayOfWeek() - 1

    private fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
    interface OnDayClickListener {
        fun onDayClick(day: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDayClick(daysOfWeek[position])
                    notifyItemChanged(selectedPosition) // Обновить старый выбранный элемент
                    selectedPosition = position
                    notifyItemChanged(selectedPosition) // Обновить новый выбранный элемент
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayTextView.text = daysOfWeek[position]

        // Изменить цвет фона в зависимости от того, выбран ли элемент
        if (position == selectedPosition) {
            holder.dayTextView.setBackgroundColor(Color.BLUE)
            holder.dayTextView.setTextColor(Color.WHITE)
        } else {
            holder.dayTextView.setBackgroundColor(Color.TRANSPARENT)
            holder.dayTextView.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = daysOfWeek.size
}
