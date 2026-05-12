package com.jatrenammapride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.jatrenammapride.navigation.NavGraph
import com.jatrenammapride.ui.theme.JatreNammaPrideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JatreNammaPrideTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
