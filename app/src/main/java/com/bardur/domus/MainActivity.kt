package com.bardur.domus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bardur.domus.navigation.NavigationStack
import com.bardur.domus.screens.PropertyViewModel
import com.bardur.domus.ui.theme.TestAppTheme

class MainActivity : ComponentActivity() {

    private val propertyViewModel = PropertyViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                val controller = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 28.dp, bottom = 36.dp)
                    ) {
                        NavigationStack(controller = controller, propertyViewModel)
                    }
                }
            }
        }
    }
}