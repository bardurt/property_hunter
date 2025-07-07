package com.example.testapp

import PropertyCard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.testapp.components.ActionBar
import com.example.testapp.model.Property
import com.example.testapp.navigation.NavigationStack
import com.example.testapp.screens.MoreScreen
import com.example.testapp.screens.PropertyViewModel
import com.example.testapp.ui.theme.TestAppTheme

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
                            .padding(top = 28.dp)
                    ) {
                        MoreScreen(navController = controller, propertyViewModel)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyCardPreview() {
    PropertyCard(
        Property(
            address = "Dalabú 1-44",
            city = "Tórshavn",
            url = "https://www.skyn.fo/default.aspx?id=19&ProductID=PROD1772",
            image = "https://www.skyn.fo/admin/public/getimage.ashx?&Image=/Files/Images/Ognir/Sudurstreymoy/Torshavn/Dalabu_Torshavn/01.JPG&Width=360&Height=248&Crop=6",
            buildYear = "2025",
            latestBid = "2.900.000",
            listPrice = "2.795.000"
        ),{}
    )
}