package com.example.expensetrackingpro

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmergencyFragment : Fragment() {

    private lateinit var containers: LinearLayout
    private var enteredAmount: Int? = null
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("emergency_amount", Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_emergency, container, false)
        containers = view.findViewById(R.id.containers)

        // Retrieve and display previously entered amount
        loadEnteredAmount()

        val fabAdd: FloatingActionButton = view.findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener { addAmount() }
        return view
    }

    private fun loadEnteredAmount() {
        enteredAmount = sharedPreferences.getInt("entered_amount", 0)
        if (enteredAmount != null) {
            containers.addView(createTextView(enteredAmount.toString()))
        }
    }

    private fun addAmount() {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.enter_amount, containers, false) as LinearLayout

        val editText: EditText = view.findViewById(R.id.editText)
        val okButton: Button = view.findViewById(R.id.okButton)

        okButton.setOnClickListener {
            val newText = editText.text.toString().trim()
            val newAmount = newText.toIntOrNull()
            if (newAmount != null && newAmount != enteredAmount) {
                enteredAmount = newAmount
                containers.removeAllViews()
                containers.addView(createTextView(enteredAmount.toString()))

                // Save entered amount to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putInt("entered_amount", enteredAmount!!)
                editor.apply()
            } else {
                Toast.makeText(requireContext(), "Please enter a different valid emergency amount", Toast.LENGTH_SHORT).show()
            }
        }

        containers.removeAllViews()
        containers.addView(view)
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.setTextColor(Color.GRAY)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        textView.setPadding(16, 16, 16, 16)
        return textView
    }
}
