package com.example.expensetrackingpro

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class Statistics : Fragment() {

    private lateinit var totalCreditSumTextView: TextView
    private lateinit var totalDebitSumTextView: TextView
    private lateinit var totalSavingSumTextView: TextView
    private var creditFragment: CreditFragment? = null
    private var debitFragment: DebitFragment? = null
    private var savingsFragment: SavingsFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        totalCreditSumTextView = view.findViewById(R.id.totalCreditSumTextView)
        totalDebitSumTextView = view.findViewById(R.id.totalDebitSumTextView)
        totalSavingSumTextView=view.findViewById(R.id.totalSavingSumTextView)

        // Get the instances of CreditFragment and DebitFragment
        creditFragment = parentFragmentManager.findFragmentByTag("creditFragmentTag") as? CreditFragment
        debitFragment = parentFragmentManager.findFragmentByTag("debitFragmentTag") as? DebitFragment
        savingsFragment=parentFragmentManager.findFragmentByTag("savingFragmentTag") as? SavingsFragment

        // Check if creditFragment and debitFragment are not null before accessing their totals
        val creditTotalSum = creditFragment?.getTotalCategorySums()
        val debitTotalSum = debitFragment?.getTotalCategorySums()
        val savingTotalSum = savingsFragment?.calculateTotalSum()

        // Display the total sum for credit and debit
        val creditTotalSumText = creditTotalSum?.let { formatCategorySums(it) } ?: "No credit transactions"
        val debitTotalSumText= debitTotalSum?.let { formatCategorySums(it) } ?: "No debit transactions"
        val savingTotalSumText = savingTotalSum?.let { String.format("%.2f", it) } ?: "No saving transactions"
        totalCreditSumTextView.text = "Total Credit Sum:\n $creditTotalSumText"
        totalDebitSumTextView.text = "Total Debit Sum:\n $debitTotalSumText"
        totalSavingSumTextView.text = "Total Saving Sum:\n $savingTotalSumText"

        // Calculate category sums and display them
        displayCategorySums()

        return view
    }

    private fun formatCategorySums(categorySums: Map<String, Double>): String {
        val formattedText = StringBuilder()
        for ((category, sum) in categorySums) {
            formattedText.append("$category: $sum\n")
        }
        return formattedText.toString().trim()
    }

    private fun displayCategorySums() {
        val categorySumsContainer = view?.findViewById<LinearLayout>(R.id.categorySumsContainer)
        categorySumsContainer?.removeAllViews()

        // Display category sums for credit
        creditFragment?.let {
            val creditCategorySums = it.calculateCategorySums()
            for ((category, sum) in creditCategorySums) {
                val textView = TextView(requireContext())
                textView.text = "Credit $category: $sum"
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Set text size
                textView.setPadding(8, 8, 8, 8) // Set padding for better appearance
                textView.setBackgroundResource(R.drawable.border_background) // Set background drawable for border
                categorySumsContainer?.addView(textView)
            }
        }

        // Display category sums for debit
        debitFragment?.let {
            val debitCategorySums = it.calculateCategorySums()
            for ((category, sum) in debitCategorySums) {
                val textView = TextView(requireContext())
                textView.text = "Debit $category: $sum"
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Set text size
                textView.setPadding(8, 8, 8, 8) // Set padding for better appearance
                textView.setBackgroundResource(R.drawable.border_background) // Set background drawable for border
                categorySumsContainer?.addView(textView)
            }
        }
    }
}
