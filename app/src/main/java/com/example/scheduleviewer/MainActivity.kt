package com.example.scheduleviewer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scheduleviewer.model.Lesson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), DayAdapter.OnDayClickListener {
    private lateinit var lessonsAdapter: EventListRecyclerViewAdapter
    private var lessons: List<Lesson> = listOf()
    private lateinit var token: String
    private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    fun getCurrentDay(): String {
        val daysArray =
            arrayOf("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")

        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]

        return daysArray[day]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        token = intent.getStringExtra("token") ?: ""


        lessonsAdapter = EventListRecyclerViewAdapter(lessons)
        findViewById<RecyclerView>(R.id.lessonsRecyclerView).apply {
            this.adapter = lessonsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val daysAdapter = DayAdapter(daysOfWeek, this)
        findViewById<RecyclerView>(R.id.daysRecyclerView).apply {
            this.adapter = daysAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        fetchLessons(getCurrentDay())

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
                Log.e("MainActivity", "Failed to fetch lessons", e)
                showErrorMessage("Failed to fetch lessons")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("MainActivity", "Unexpected code $response")
                    showErrorMessage("Unexpected code ${response.code}")
                    return
                }

                response.body?.string()?.let { responseBody ->
                    Log.d("MainActivity", "Response: $responseBody")
                    val lessonType = object : TypeToken<List<Lesson>>() {}.type
                    val fetchedLessons = gson.fromJson<List<Lesson>>(responseBody, lessonType)

                    runOnUiThread {
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
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}