package com.example.scheduleviewer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scheduleviewer.model.Lesson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.util.Calendar
import java.util.Locale

class ScheduleFragment : Fragment(), DayAdapter.OnDayClickListener {
    private lateinit var lessonsAdapter: EventListRecyclerViewAdapter
    private var lessons: List<Lesson> = listOf()
    private lateinit var token: String
    private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        // Получение токена из SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("TOKEN", "") ?: ""

        lessonsAdapter = EventListRecyclerViewAdapter(lessons)
        view.findViewById<RecyclerView>(R.id.lessonsRecyclerView).apply {
            this.adapter = lessonsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val daysAdapter = DayAdapter(daysOfWeek, this)
        view.findViewById<RecyclerView>(R.id.daysRecyclerView).apply {
            this.adapter = daysAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        fetchLessons(getCurrentDay())

        return view
    }

    override fun onDayClick(day: String) {
        fetchLessons(day)
    }

    private fun fetchLessons(day: String) {
        val client = OkHttpClient()
        val gson = Gson()
        val json = gson.toJson(mapOf("day" to day.toLowerCase(Locale.ROOT)))
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("http://192.168.0.106:8000/get/lessons")
            .addHeader("Authorization", token)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ScheduleFragment", "Failed to fetch lessons", e)
                showErrorMessage("Failed to fetch lessons")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("ScheduleFragment", "Unexpected code $response")
                    showErrorMessage("Unexpected code ${response.code}")
                    return
                }

                response.body?.string()?.let { responseBody ->
                    Log.d("ScheduleFragment", "Response: $responseBody")
                    val lessonType = object : TypeToken<List<Lesson>>() {}.type
                    val fetchedLessons: List<Lesson> = gson.fromJson(responseBody, lessonType) ?: emptyList()

                    activity?.runOnUiThread {
                        if (fetchedLessons.isNotEmpty()) {
                            lessons = fetchedLessons
                            lessonsAdapter.updateEvents(lessons)
                        } else {
                            showErrorMessage("No lessons found")
                            lessonsAdapter.updateEvents(emptyList())
                        }
                    }
                } ?: run {
                    showErrorMessage("Empty response body")
                }

            }
        })
    }

    private fun showErrorMessage(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDay(): String {
        val daysArray = arrayOf("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        val s = daysArray[day - 1]
        return s
    }
}
