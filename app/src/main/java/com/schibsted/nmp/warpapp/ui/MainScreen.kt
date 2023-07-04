@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.schibsted.nmp.warpapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schibsted.nmp.warpapp.BrandTheme
import com.schibsted.nmp.warpapp.MainViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity)
    val flavor = viewModel.flavor.collectAsState()

    BrandTheme(flavor = flavor.value) {
        NavHost(navController = navController, startDestination = "components") {
            composable("components") {
                ComponentListScreen {
                    navController.navigate(it)
                }
            }
            composable("buttons") {
                ButtonScreen {
                    navController.navigateUp()
                }
            }
            composable("box") {
                BoxScreen {
                    navController.navigateUp()
                }
            }
            composable("typography") {
                TypographyScreen {
                    navController.navigateUp()
                }
            }
        }
    }
}

@Composable
fun ComponentListScreen(onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Warp components")
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(
                    listOf(
                        "buttons" to "WarpButton",
                        "box" to "WarpBox",
                        "typography" to "Typography",
                    ))
                {
                    ElevatedCard(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable {
                                onNavigate(it.first)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(it.second)
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview(
    @PreviewParameter(FlavorPreviewProvider::class) flavor: String
) {
    BrandTheme(flavor = flavor) {
        ComponentListScreen {

        }
    }
}