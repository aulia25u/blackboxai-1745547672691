package com.example.imageuploadapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class ProfileActivity : AppCompatActivity() {

    private lateinit var imeiTextView: TextView
    private lateinit var fingerprintStatusTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imeiTextView = findViewById(R.id.textViewIMEI)
        fingerprintStatusTextView = findViewById(R.id.textViewFingerprintStatus)
        logoutButton = findViewById(R.id.buttonLogout)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    fingerprintStatusTextView.text = "Authentication error: $errString"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fingerprintStatusTextView.text = "Fingerprint authentication succeeded"
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    fingerprintStatusTextView.text = "Fingerprint authentication failed"
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Authenticate using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        checkImei()
        checkFingerprintSupport()

        fingerprintStatusTextView.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun checkImei() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val imei = telephonyManager.imei ?: "Unavailable"
            imeiTextView.text = "IMEI: $imei"
        } catch (e: SecurityException) {
            imeiTextView.text = "IMEI: Permission denied"
        }
    }

    private fun checkFingerprintSupport() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                fingerprintStatusTextView.text = "Fingerprint authentication is available. Tap to authenticate."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                fingerprintStatusTextView.text = "No fingerprint hardware available."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                fingerprintStatusTextView.text = "Fingerprint hardware currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                fingerprintStatusTextView.text = "No fingerprints enrolled. Please set up in settings."
            else ->
                fingerprintStatusTextView.text = "Fingerprint authentication not available."
        }
    }
}
