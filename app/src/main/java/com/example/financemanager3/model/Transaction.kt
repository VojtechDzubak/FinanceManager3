package com.example.financemanager3.model

import java.util.Date

data class Transaction(
    val id: Long,
    val name: String,
    val amount: Double,
    val date: Date,
    val type: TransactionType,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME, EXPENSE
}