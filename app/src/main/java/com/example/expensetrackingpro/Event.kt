package com.example.expensetrackingpro

import java.util.Calendar

data class Event(
    val id: Int,
    val purpose: String,
    val amount: Double,
    val date: Calendar
)