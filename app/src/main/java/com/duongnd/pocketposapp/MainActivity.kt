package com.duongnd.pocketposapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.duongnd.pocketposapp.core.navigation.AppNavGraph
import com.duongnd.pocketposapp.core.ui.theme.PocketPOSAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketPOSAppTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
