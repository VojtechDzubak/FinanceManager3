package com.example.financemanager3.model

data class MonthlyStat(
    val year: Int,
    val month: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)