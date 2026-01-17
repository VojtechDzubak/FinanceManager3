package com.example.financemanager3.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.financemanager3.model.MonthlyStat
import com.example.financemanager3.model.Transaction
import com.example.financemanager3.model.TransactionType
import java.util.Calendar
import java.util.Date

object TransactionRepository {

    private val allTransactions = mutableListOf<Transaction>()
    private val _allTransactionsLiveData = MutableLiveData<List<Transaction>>()

    private val _selectedDate = MutableLiveData<Calendar>()
    val selectedDate: LiveData<Calendar> = _selectedDate

    val transactionsLiveData = MediatorLiveData<List<Transaction>>()
    val totalIncome = MediatorLiveData<Double>()
    val totalExpenses = MediatorLiveData<Double>()
    val balance = MediatorLiveData<Double>()

    private val _monthlyStats = MediatorLiveData<List<MonthlyStat>>()
    val monthlyStats: LiveData<List<MonthlyStat>> = _monthlyStats

    init {
        _selectedDate.value = Calendar.getInstance()

        transactionsLiveData.addSource(_allTransactionsLiveData) { filterAndSortTransactions() }
        transactionsLiveData.addSource(_selectedDate) { filterAndSortTransactions() }

        totalIncome.addSource(transactionsLiveData) { updateTotals(it) }
        totalExpenses.addSource(transactionsLiveData) { updateTotals(it) }
        balance.addSource(transactionsLiveData) { updateTotals(it) }
        _monthlyStats.addSource(_allTransactionsLiveData) { calculateMonthlyStats(it) }
    }

    fun addTransaction(name: String, amount: Double, date: Date, type: TransactionType, category: String) {
        val newTransaction = Transaction(
            id = (allTransactions.size + 1).toLong(),
            name = name,
            amount = amount,
            date = date,
            type = type,
            category = category
        )
        allTransactions.add(newTransaction)
        _allTransactionsLiveData.value = allTransactions.toList()
    }

    fun selectNextMonth() {
        val calendar = _selectedDate.value ?: return
        calendar.add(Calendar.MONTH, 1)
        _selectedDate.value = calendar
    }

    fun selectPreviousMonth() {
        val calendar = _selectedDate.value ?: return
        calendar.add(Calendar.MONTH, -1)
        _selectedDate.value = calendar
    }

    private fun filterAndSortTransactions() {
        val calendar = _selectedDate.value ?: return
        val transactions = _allTransactionsLiveData.value ?: emptyList()

        val filtered = transactions.filter {
            val transactionCalendar = Calendar.getInstance()
            transactionCalendar.time = it.date
            transactionCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            transactionCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
        }
        
        transactionsLiveData.value = filtered.sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.createdAt })
    }

    private fun updateTotals(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        totalIncome.value = income
        totalExpenses.value = expenses
        balance.value = income - expenses
    }

    private fun calculateMonthlyStats(transactions: List<Transaction>) {
        val stats = transactions.groupBy {
            val calendar = Calendar.getInstance()
            calendar.time = it.date
            Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
        }.map { (yearMonth, monthlyTransactions) ->
            val income = monthlyTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expenses = monthlyTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            MonthlyStat(yearMonth.first, yearMonth.second, income, expenses, income - expenses)
        }.sortedWith(compareByDescending<MonthlyStat> { it.year }.thenByDescending { it.month })
        _monthlyStats.value = stats
    }
}