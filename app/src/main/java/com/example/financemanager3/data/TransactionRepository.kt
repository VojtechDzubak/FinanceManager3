package com.example.financemanager3.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.financemanager3.model.MonthlyStat
import com.example.financemanager3.model.Transaction
import com.example.financemanager3.model.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar
import java.util.Date

object TransactionRepository {

    private val allTransactions = mutableListOf<Transaction>()
    private val _allTransactionsLiveData = MutableLiveData<List<Transaction>>()

    val allTransactionsLiveData: LiveData<List<Transaction>> = _allTransactionsLiveData
    private val _selectedDate = MutableLiveData<Calendar>()
    val selectedDate: LiveData<Calendar> = _selectedDate

    val transactionsLiveData = MediatorLiveData<List<Transaction>>()
    val totalIncome = MediatorLiveData<Double>()
    val totalExpenses = MediatorLiveData<Double>()
    val balance = MediatorLiveData<Double>()

    private val _monthlyStats = MediatorLiveData<List<MonthlyStat>>()
    val monthlyStats: LiveData<List<MonthlyStat>> = _monthlyStats

    // Proměnné pro ukládání
    private const val FILE_NAME = "transactions.json"
    private lateinit var dataFile: File
    private val gson = Gson()

    // Inicializace repozitáře s Contextem
    fun initialize(context: Context) {
        dataFile = File(context.filesDir, FILE_NAME)
        loadData()
    }

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
            id = System.currentTimeMillis(), // Použijeme čas jako unikátní ID
            name = name,
            amount = amount,
            date = date,
            type = type,
            category = category
        )
        allTransactions.add(newTransaction)
        updateLiveData()
        saveData() // Uložit po přidání
    }

    fun deleteTransaction(transaction: Transaction) {
        // Najdeme a odstraníme transakci ze seznamu
        // (Díky data class funguje porovnání objektů automaticky)
        allTransactions.remove(transaction)
        saveData()
        updateLiveData()
    }

    // UPRAVENÁ METODA: Bezpečné načítání
    private fun loadData() {
        if (dataFile.exists()) {
            try {
                val jsonString = dataFile.readText()
                val type = object : TypeToken<List<Transaction>>() {}.type
                val loadedTransactions: List<Transaction>? = gson.fromJson(jsonString, type)

                allTransactions.clear()
                if (loadedTransactions != null) {
                    allTransactions.addAll(loadedTransactions)
                }
                updateLiveData()

            } catch (e: Exception) {
                // Zde zachytíme chybu (např. poškozený JSON)
                e.printStackTrace()

                // Pokud je soubor poškozený, vytvoříme nový prázdný seznam
                // a přepíšeme tím poškozený soubor, aby aplikace mohla dál fungovat.
                allTransactions.clear()
                saveData() // Vytvoří nový validní (prázdný) JSON
            }
        }
    }

    // Metoda pro uložení dat do souboru
    private fun saveData() {
        try {
            val jsonString = gson.toJson(allTransactions)
            dataFile.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLiveData() {
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