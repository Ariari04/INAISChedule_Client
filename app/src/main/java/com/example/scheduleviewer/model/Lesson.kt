package com.example.scheduleviewer.model

data class Lesson(
    val _id: String,
    val groups: List<Group>,
    val startTime: Int,
    val day_of_week: Int,
    val typeLesson: String,
    val subject: Subject,
    val room: Room,
    val teacher: Teacher
)
