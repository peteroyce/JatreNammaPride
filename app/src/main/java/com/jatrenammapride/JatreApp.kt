package com.jatrenammapride

import android.app.Application
import com.jatrenammapride.data.local.AppDatabase

class JatreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the database singleton eagerly
        AppDatabase.getInstance(this)
    }
}
