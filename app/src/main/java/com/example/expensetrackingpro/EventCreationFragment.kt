package com.example.expensetrackingpro

import EventDbHelper
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class EventCreationFragment : Fragment() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextPurpose: EditText
    private lateinit var buttonSaveEvent: Button
    private var selectedDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_creation, container, false)

        editTextAmount = view.findViewById(R.id.amountInput)
        editTextPurpose = view.findViewById(R.id.purposeInput)
        buttonSaveEvent = view.findViewById(R.id.saveButton)

        buttonSaveEvent.setOnClickListener { saveEvent() }

        // Retrieve the selected date from arguments
        selectedDate = arguments?.getLong(SELECTED_DATE_KEY) ?: 0

        // Display the selected date in a TextView or another appropriate view
        val selectedDateTextView = view.findViewById<TextView>(R.id.selectedDateText)
        selectedDateTextView.text = formatDate(selectedDate)

        return view
    }

    private fun saveEvent() {
        val amount = editTextAmount.text.toString().trim()
        val purpose = editTextPurpose.text.toString().trim()

        // Perform validation if needed

        // Delete previous record for the selected date
        deletePreviousRecord(selectedDate)

        // Save the event details to the database along with the selectedDate
        val dbHelper = EventDbHelper(requireContext())
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(EventDbHelper.COLUMN_AMOUNT, amount)
            put(EventDbHelper.COLUMN_PURPOSE, purpose)
            put(EventDbHelper.COLUMN_DATE, formatDate(selectedDate)) // Save the selected date
        }

        val newRowId = db.insert(EventDbHelper.TABLE_EVENTS, null, values)

        // Check if the insertion was successful
        if (newRowId != -1L) {
            Toast.makeText(requireContext(), "Event saved successfully", Toast.LENGTH_SHORT).show()

            // Schedule notification after saving the event
            scheduleNotification(selectedDate, amount, purpose)

            // Navigate back to the CalendarFragment
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Failed to save event", Toast.LENGTH_SHORT).show()
        }

        db.close()

        // Clear the input fields after saving
        editTextAmount.text.clear()
        editTextPurpose.text.clear()
    }

    private fun deletePreviousRecord(selectedDate: Long) {
        val dbHelper = EventDbHelper(requireContext())
        val db = dbHelper.writableDatabase

        val selection = "${EventDbHelper.COLUMN_DATE} = ?"
        val selectionArgs = arrayOf(formatDate(selectedDate))

        val deletedRows = db.delete(
            EventDbHelper.TABLE_EVENTS,
            selection,
            selectionArgs
        )

        if (deletedRows > 0) {
            Log.d("EventCreationFragment", "Deleted $deletedRows previous record for date: $selectedDate")
        }

        db.close()
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(date))
    }

    private fun scheduleNotification(eventDate: Long, amount: String, purpose: String) {
        // Implement notification scheduling using NotificationManager
        // Use eventDate, amount, and purpose to create the notification
    }

    companion object {
        const val SELECTED_DATE_KEY = "selected_date"
    }
}
