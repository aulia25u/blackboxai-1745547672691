package com.example.imageuploadapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.imageuploadapp.api.RetrofitClient
import com.example.imageuploadapp.models.LoginRequest
import com.example.imageuploadapp.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        progressBar = findViewById(R.id.progressBar)

        // Pre-fill credentials for testing
        usernameEditText.setText(Constants.DEFAULT_USERNAME)
        passwordEditText.setText(Constants.DEFAULT_PASSWORD)

        loginButton.setOnClickListener {
            val username = usernameEditText.text?.toString() ?: ""
            val password = passwordEditText.text?.toString() ?: ""

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(username, password)
        }
    }

    private fun performLogin(username: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(
                    request = LoginRequest(username, password)
                )

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.status == Constants.STATUS_SUCCESS) {
                        Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_LONG).show()
                        // Here you might want to handle OTP verification
                        // For now, we'll just proceed to Dashboard
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
    }
}
