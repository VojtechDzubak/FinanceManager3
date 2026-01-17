package com.example.financemanager3

import android.app.Application
import com.example.financemanager3.data.TransactionRepository

class FinanceManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializujeme repozitář při startu aplikace
        TransactionRepository.initialize(this)
    }
}