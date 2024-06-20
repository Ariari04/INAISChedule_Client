package com.example.scheduleviewer.model

data class User(
    val _id: String,
    val name: String,
    val surname: String,
    val email: String,
    val pass: String,
    val group: String,
    val role: String
)