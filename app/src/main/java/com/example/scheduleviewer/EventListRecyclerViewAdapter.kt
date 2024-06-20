package com.example.scheduleviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scheduleviewer.model.Lesson

class EventListRecyclerViewAdapter(private var lessons: List<Lesson>) :
    RecyclerView.Adapter<EventListRecyclerViewAdapter.ViewHolder>() {

    fun updateEvents(lessons: List<Lesson>) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectTextView: TextView = itemView.findViewById(R.id.subjectTextView)
        val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        val dayTimeTextView: TextView = itemView.findViewById(R.id.dayTimeTextView)
        val roomTextView: TextView = itemView.findViewById(R.id.roomTextView)
        val teacherTextView: TextView = itemView.findViewById(R.id.teacherTextView)
        val groupsTextView: TextView = itemView.findViewById(R.id.groupsTextView)
        val emptyTextView: TextView = itemView.findViewById(R.id.emptyTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (lessons.isEmpty()) {
            holder.emptyTextView.visibility = View.VISIBLE
            holder.subjectTextView.visibility = View.GONE
            holder.typeTextView.visibility = View.GONE
            holder.dayTimeTextView.visibility = View.GONE
            holder.roomTextView.visibility = View.GONE
            holder.teacherTextView.visibility = View.GONE
            holder.groupsTextView.visibility = View.GONE
        } else {
            holder.emptyTextView.visibility = View.GONE
            holder.subjectTextView.visibility = View.VISIBLE
            holder.typeTextView.visibility = View.VISIBLE
            holder.dayTimeTextView.visibility = View.VISIBLE
            holder.roomTextView.visibility = View.VISIBLE
            holder.teacherTextView.visibility = View.VISIBLE
            holder.groupsTextView.visibility = View.VISIBLE

            val lesson = lessons[position]
            holder.apply {
                subjectTextView.text = lesson.subject.name
                typeTextView.text = lesson.typeLesson
                dayTimeTextView.text = when (lesson.startTime) {
                    1 -> "Start: 08:00"
                    2 -> "Start: 09:30"
                    3 -> "Start: 11:00"
                    4 -> "Start: 12:30"
                    5 -> "Start: 14:00"
                    6 -> "Start: 15:30"
                    7 -> "Start: 17:00"
                    8 -> "Start: 18:30"
                    9 -> "Start: 20:00"
                    else -> "Start: Unknown"
                }
                roomTextView.text = "Room: ${lesson.room.name}"
                teacherTextView.text = "Teacher: ${lesson.teacher.name} ${lesson.teacher.surname}"
                groupsTextView.text = "Group: ${lesson.groups.joinToString(", ") { it.name }}"
            }
        }
    }

    override fun getItemCount() = if (lessons.isEmpty()) 1 else lessons.size
}