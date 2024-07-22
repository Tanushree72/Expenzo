package com.example.expensetrackingpro

import EventDbHelper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            Log.d("CalendarFragment", "Selected date: $selectedDate")

            // Query the database for events associated with the selected date
            val dbHelper = EventDbHelper(requireContext())
            val db = dbHelper.readableDatabase

            val projection = arrayOf(
                EventDbHelper.COLUMN_AMOUNT,
                EventDbHelper.COLUMN_PURPOSE
            )

            val selection = "${EventDbHelper.COLUMN_DATE} = ?"
            val selectionArgs = arrayOf(formatDate(selectedDate))

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
                val amount = cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.COLUMN_AMOUNT))
                val purpose = cursor.getString(cursor.getColumnIndexOrThrow(EventDbHelper.COLUMN_PURPOSE))
                eventDetails.append("Amount: $amount, Purpose: $purpose\n")
            }
            cursor.close()

            // Display the event details in a popup
            if (eventDetails.isNotEmpty()) {
                showEventDetailsPopup(eventDetails.toString(), selectedDate)
            } else {
                // If no events found, navigate to EventCreationFragment with the selected date
                navigateToEventCreationFragment(selectedDate)
            }
        }

        return view
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(date))
    }

    private fun showEventDetailsPopup(eventDetails: String, selectedDate: Long) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Event Details")
        dialogBuilder.setMessage(eventDetails)
        dialogBuilder.setPositiveButton("Edit") { _, _ ->
            // Navigate to EventCreationFragment to edit the values
            navigateToEventCreationFragment(selectedDate)
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun navigateToEventCreationFragment(selectedDate: Long) {
        val args = Bundle().apply {
            putLong(EventCreationFragment.SELECTED_DATE_KEY, selectedDate)
        }

        val eventCreationFragment = EventCreationFragment()
        eventCreationFragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, eventCreationFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToNotificationFragment() {
        val currentDate = Calendar.getInstance().timeInMillis

        val args = Bundle().apply {
            putLong("current_date", currentDate) // Use a different key here
        }

        val notificationFragment = NotifyFragment()
        notificationFragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, notificationFragment)
            .addToBackStack(null)
            .commit()
    }


}
