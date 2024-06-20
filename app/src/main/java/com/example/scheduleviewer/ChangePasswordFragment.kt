package com.example.scheduleviewer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class ChangePasswordFragment : Fragment() {

    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmNewPasswordEditText: EditText
    private lateinit var submitChangePasswordButton: Button
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText)
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText)
        confirmNewPasswordEditText = view.findViewById(R.id.confirmNewPasswordEditText)
        submitChangePasswordButton = view.findViewById(R.id.submitChangePasswordButton)

        // Получение токена из SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("TOKEN", "") ?: ""

        submitChangePasswordButton.setOnClickListener {
            handleChangePassword()
        }

        return view
    }

    private fun handleChangePassword() {
        val oldPassword = oldPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()
        val confirmNewPassword = confirmNewPasswordEditText.text.toString()

        if (newPassword != confirmNewPassword) {
            Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val gson = Gson()
        val json = gson.toJson(mapOf("oldPassword" to oldPassword, "newPassword" to newPassword))
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("http://192.168.0.106:8000/update/password")
            .addHeader("Authorization", token)
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                activity?.runOnUiThread {
                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        })
    }
}
