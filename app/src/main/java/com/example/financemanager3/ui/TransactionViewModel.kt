package com.example.financemanager3.ui

import androidx.lifecycle.ViewModel
import com.example.financemanager3.data.TransactionRepository
import com.example.financemanager3.model.TransactionType
import com.example.financemanager3.model.Transaction // DŮLEŽITÝ IMPORT
import java.util.Date

class TransactionViewModel : ViewModel() {

    val transactions = TransactionRepository.transactionsLiveData

    val allTransactions = TransactionRepository.allTransactionsLiveData
    val totalIncome = TransactionRepository.totalIncome
    val totalExpenses = TransactionRepository.totalExpenses
    val balance = TransactionRepository.balance
    val selectedDate = TransactionRepository.selectedDate
    val monthlyStats = TransactionRepository.monthlyStats

    fun addTransaction(name: String, amount: Double, date: Date, type: TransactionType, category: String) {
        TransactionRepository.addTransaction(name, amount, date, type, category)
    }

    // Tuto metodu jste pravděpodobně měli s chybou nebo chyběla
    fun deleteTransaction(transaction: Transaction) {
        TransactionRepository.deleteTransaction(transaction)
    }

    fun selectNextMonth() {
        TransactionRepository.selectNextMonth()
    }

    fun selectPreviousMonth() {
        TransactionRepository.selectPreviousMonth()
    }
}