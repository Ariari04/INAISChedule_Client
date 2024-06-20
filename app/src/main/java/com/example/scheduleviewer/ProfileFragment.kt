package com.example.scheduleviewer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {

    private lateinit var token: String
    private lateinit var nameTextView: TextView
    private lateinit var surnameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var groupTextView: TextView
    private lateinit var roleTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Получение токена из SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("TOKEN", "") ?: ""

        nameTextView = view.findViewById(R.id.nameTextView)
        surnameTextView = view.findViewById(R.id.surnameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        groupTextView = view.findViewById(R.id.groupTextView)
        roleTextView = view.findViewById(R.id.roleTextView)

        fetchProfile()

        val changePasswordButton = view.findViewById<Button>(R.id.changePasswordButton)
        changePasswordButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }
        return view
    }

    private fun fetchProfile() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.0.106:8000/get/user")
            .addHeader("Authorization", token)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showErrorMessage("Failed to fetch profile")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    showErrorMessage("Unexpected code ${response.code}")
                    return
                }

                response.body?.string()?.let { responseBody ->
                    val json = JSONObject(responseBody)
                    activity?.runOnUiThread {
                        nameTextView.text = "Name: " + json.getString("name")
                        surnameTextView.text = "Surname: " + json.getString("surname")
                        emailTextView.text = "Email: " + json.getString("email")
                        groupTextView.text = "Group: " + json.getString("group")
                        roleTextView.text = json.getString("role")
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
}
