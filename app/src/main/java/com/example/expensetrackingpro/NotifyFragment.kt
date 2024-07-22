package com.example.expensetrackingpro

import EventDbHelper
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class NotifyFragment : Fragment() {

    private lateinit var reminderTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notify, container, false)
        reminderTextView = view.findViewById(R.id.reminderTextView)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Check if notifications are enabled
        val notificationsEnabled = sharedPreferences.getBoolean("notify", true)

        if (notificationsEnabled) {
            displayReminders()
        } else {
            reminderTextView.text = "Notifications are disabled"
        }

        return view
    }


    private fun displayReminders() {
        // Retrieve the current date
        val currentDate = Calendar.getInstance().timeInMillis

        // Query the database for events associated with the current date
        val eventDetails = getEventsForDate(currentDate)

        // Display the event details in the TextView
        reminderTextView.text = eventDetails
    }

    private fun getEventsForDate(date: Long): String {
        val dbHelper = EventDbHelper(requireContext())
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            EventDbHelper.COLUMN_DATE,
            EventDbHelper.COLUMN_AMOUNT,
            EventDbHelper.COLUMN_PURPOSE
        )

        val selection = "${EventDbHelper.COLUMN_DATE} = ?"
        val selectionArgs = arrayOf(formatDate(date))

        val cursor = db.query(
            EventDbHelper.TABLE_EVENTS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val eventDetails = StringBuilder()
        while (cursor.moveToNext()) {
            val eventDate = cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.COLUMN_DATE))
            val amount = cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.COLUMN_AMOUNT))
            val purpose = cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.COLUMN_PURPOSE))

            // Append each value with a line break after each
            eventDetails.append("Date: $eventDate\n")
            eventDetails.append("Amount: $amount\n")
            eventDetails.append("Purpose: $purpose\n") // Add an extra line break between entries
        }
        cursor.close()
        db.close()

        return eventDetails.toString()
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(date))
    }
}