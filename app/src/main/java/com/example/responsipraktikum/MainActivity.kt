package com.example.responsipraktikum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.responsipraktikum.ui.navigation.AppNavGraph
import com.example.responsipraktikum.ui.theme.ResponsiPraktikumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResponsiPraktikumTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}