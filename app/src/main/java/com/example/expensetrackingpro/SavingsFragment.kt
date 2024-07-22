package com.example.expensetrackingpro

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class SavingsFragment : Fragment() {

    private lateinit var containers: LinearLayout
    private val enteredAmounts: MutableList<Double> = mutableListOf()
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(
            "savings",
            Context.MODE_PRIVATE
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_savings, container, false)
        containers = view.findViewById(R.id.containers)

        // Retrieve and display previously added amounts
        loadEnteredAmounts()

        val fabAdd: FloatingActionButton = view.findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener { addAmount() }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Add this fragment to the fragment manager with the tag "creditFragmentTag"
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, this, "savingFragmentTag")
            .commit()
    }

    private fun loadEnteredAmounts() {
        val savedAmounts = sharedPreferences.getStringSet("entered_amounts", mutableSetOf())
        if (savedAmounts != null) {
            enteredAmounts.clear()
            enteredAmounts.addAll(savedAmounts.map { it.toDouble() })
            for (amount in enteredAmounts) {
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                val date = Date()
                val dateString = dateFormat.format(date)
                containers.addView(createTextViewWithOptions(amount, dateString))
            }
        }
    }

    private fun addAmount() {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.enter_amount, containers, false) as LinearLayout

        val editText: EditText = view.findViewById(R.id.editText)
        val okButton: Button = view.findViewById(R.id.okButton)

        okButton.setOnClickListener {
            val amountText = editText.text.toString()
            val amount = amountText.toDoubleOrNull()
            if (amount != null) {
                enteredAmounts.add(amount)

                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                val date = Date()
                val dateString = dateFormat.format(date)
                containers.addView(createTextViewWithOptions(amount, dateString))

                // Save entered amounts to SharedPreferences
                val editor = sharedPreferences.edit()
                val enteredAmountsSet = enteredAmounts.map { it.toString() }.toSet()
                editor.putStringSet("entered_amounts", enteredAmountsSet)
                editor.apply()
            } else {
                Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
            }
            containers.removeView(view)
        }

        containers.addView(view)
    }

    private fun createTextViewWithOptions(amount: Double, date: String): View {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.HORIZONTAL
        container.gravity = Gravity.CENTER_VERTICAL
        container.setPadding(16, 16, 16, 16)
        container.setBackgroundColor(Color.LTGRAY)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 8, 0, 8)
        container.layoutParams = layoutParams

        val textView = TextView(requireContext())
        textView.text = "$amount ($date)"
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(18f)
        val textParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        textView.layoutParams = textParams

        val deleteIcon = AppCompatImageView(requireContext())
        deleteIcon.setImageResource(R.drawable.baseline_delete_24)
        deleteIcon.setColorFilter(Color.BLACK)
        val deleteParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        deleteIcon.layoutParams = deleteParams
        deleteIcon.setOnClickListener {
            deleteAmount(
                amount,
                container
            )
        } // Pass container to the deleteAmount function

        container.addView(textView)
        container.addView(deleteIcon)

        return container
    }

    private fun deleteAmount(amount: Double, container: View) {
        enteredAmounts.remove(amount)
        containers.removeView(container)

        // Update SharedPreferences after deletion
        val editor = sharedPreferences.edit()
        val enteredAmountsSet = enteredAmounts.map { it.toString() }.toSet()
        editor.putStringSet("entered_amounts", enteredAmountsSet)
        editor.apply()
    }
    fun calculateTotalSum(): Double {
        return enteredAmounts.sum()
    }


}