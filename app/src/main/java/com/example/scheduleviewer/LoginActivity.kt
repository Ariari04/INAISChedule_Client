package com.example.scheduleviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("email", email)
            put("pass", password)
        }
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url("http://192.168.0.106:8000/account/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LoginActivity", "Failed to login", e)
                showErrorMessage("Failed to login")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("LoginActivity", "Unexpected code $response")
                    showErrorMessage("Unexpected code ${response.code}")
                    return
                }

                response.body?.string()?.let { responseBody ->
                    Log.d("LoginActivity", "Response: $responseBody")
                    val token = JSONObject(responseBody).getString("token")

                    // Сохранение токена в SharedPreferences
                    val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("TOKEN", token)
                        apply()
                    }

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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
