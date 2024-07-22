package com.example.expensetrackingpro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class LoginActivity : AppCompatActivity() {
    private lateinit var inputUserName: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnlog: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputUserName = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)
        btnlog = findViewById(R.id.btnlog)

        btnlog.setOnClickListener {
            val username = inputUserName.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (username.isEmpty()) {
                inputUserName.error = "Username Required"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                inputPassword.error = "Password Required"
                return@setOnClickListener
            } else {
                val dbHelper = DatabaseHelper(this)
                if (dbHelper.getUser(username, password)) {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                    val editor = prefs.edit()
                    editor.putString("uname", username) // Assuming "uname" is the key for the username preference
                    editor.apply()
                    // User authenticated, proceed to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close the login activity to prevent user from returning
                } else {
                    // Invalid username or password, show error message
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val signUpTextView = findViewById<TextView>(R.id.textViewSignUp)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.change_password_dialog, null)
        val usernameEditText = dialogView.findViewById<EditText>(R.id.usernameEditText)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
        val changePasswordButton = dialogView.findViewById<Button>(R.id.changePasswordButton)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()

        changePasswordButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()


            val dbHelper = DatabaseHelper(this)
            dbHelper.updateUserPassword(username, newPassword)

            // Show success message
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
            // Perform password change logic here

            dialog.dismiss()
        }

        dialog.show()
    }
}