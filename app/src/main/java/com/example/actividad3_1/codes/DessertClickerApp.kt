package com.example.actividad3_1.codes

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.content.ContextCompat
import com.example.actividad3_1.R
import com.example.actividad3_1.model.Dessert

@Composable
fun DessertClickerApp(
    desserts: List<Dessert>
) {
    var revenue by rememberSaveable { mutableStateOf(0) }
    var dessertsSold by rememberSaveable { mutableStateOf(0) }
    val currentDessertIndex by rememberSaveable { mutableStateOf(0) }
    var currentDessertPrice by rememberSaveable { mutableStateOf(desserts[currentDessertIndex].price) }
    var currentDessertImageId by rememberSaveable { mutableStateOf(desserts[currentDessertIndex].imageId) }

    Scaffold(
        topBar = {
            val intentContext = LocalContext.current
            val layoutDirection = LocalLayoutDirection.current
            DessertClickerAppBar(
                onShareButtonClicked = {
                    shareSoldDessertsInformation(
                        intentContext = intentContext,
                        dessertsSold = dessertsSold,
                        revenue = revenue
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = WindowInsets
                            .safeDrawing
                            .asPaddingValues()
                            .calculateStartPadding(layoutDirection),
                        end = WindowInsets
                            .safeDrawing
                            .asPaddingValues()
                            .calculateEndPadding(layoutDirection)
                    )
            )
        }
    ) {contentPadding ->
        DessertClickerScreen(
            revenue = revenue,
            dessertsSold = dessertsSold,
            dessertImageId = currentDessertImageId,
            onDessertClicked = {
                revenue += currentDessertPrice
                dessertsSold++
                val dessertToShow = determineDessertToShow(desserts, dessertsSold)
                currentDessertImageId = dessertToShow.imageId
                currentDessertPrice = dessertToShow.price
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

fun shareSoldDessertsInformation(intentContext: Context, dessertsSold: Int, revenue: Int) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            intentContext.getString(R.string.share_text, dessertsSold, revenue)
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    try {
        ContextCompat.startActivity(intentContext, shareIntent, null)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            intentContext,
            intentContext.getString(R.string.sharing_not_available),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun determineDessertToShow(
    desserts: List<Dessert>,
    dessertsSold: Int
): Dessert {
    var dessertToShow = desserts.first()
    for (dessert in desserts) {
        if (dessertsSold >= dessert.startProductionAmount) {
            dessertToShow = dessert
        } else {
            break
        }
    }
    return dessertToShow
}
