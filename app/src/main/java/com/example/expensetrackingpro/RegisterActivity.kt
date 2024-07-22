package com.example.expensetrackingpro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var inputUserName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var btnReg: Button

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
        return emailRegex.matches(email)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inputUserName = findViewById(R.id.inputUsername)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        btnReg = findViewById(R.id.btnReg)

        btnReg.setOnClickListener {
            val dbHelper = DatabaseHelper(this)
            val username = inputUserName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val cpassword = inputConfirmPassword.text.toString().trim()

            if (username.isEmpty()) {
                inputUserName.error = "Username Required"
                return@setOnClickListener
            } else if (email.isEmpty()) {
                inputEmail.error = "Email Required"
                return@setOnClickListener
            } else if (!isValidEmail(email)) {
                inputEmail.error = "Invalid Email"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                inputPassword.error = "Password Required"
                return@setOnClickListener
            } else if (password.length < 4) {
                inputPassword.error = "Password must be at least 4 characters long"
                return@setOnClickListener
            } else if (!password.matches(Regex(".*\\d.*"))) {
                inputPassword.error = "Password must contain at least one digit"
                return@setOnClickListener
            } else if (!password.matches(Regex(".*[a-zA-Z].*"))) {
                inputPassword.error = "Password must contain at least one alphabetic character"
                return@setOnClickListener
            } else if (cpassword.isEmpty()) {
                inputConfirmPassword.error = "Confirm Password Required"
                return@setOnClickListener
            } else {
                if (password != cpassword) {
                    // Show error message if passwords don't match
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    val newRowId = dbHelper.insertUser(username, email, password)
                    if (newRowId > -1) {
                        val prefs = PreferenceManager.getDefaultSharedPreferences(this@RegisterActivity)
                        val editor = prefs.edit()
                        editor.putString("uname", username) // Assuming "uname" is the key for the username preference
                        editor.apply()
                        Toast.makeText(
                            this,
                            "User registered successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Redirect to login activity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val register = findViewById<TextView>(R.id.alreadyHaveAccount)
        register.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
