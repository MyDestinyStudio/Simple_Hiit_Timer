package com.mydestiny.hiittimer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.mydestiny.hiittimer.navigation.MainGraph
import com.mydestiny.hiittimer.navigation.StartObScreen
import com.mydestiny.hiittimer.navigation.TimerObScreen
import com.mydestiny.hiittimer.screens.startscreen.StartScreen
import com.mydestiny.hiittimer.screens.timerscreen.TimerScreenUI
import com.mydestiny.hiittimer.screens.timerscreen.TimerViewModel
import com.mydestiny.hiittimer.ui.theme.HiitTimerTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiitTimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {


                    val viewmodel =TimerViewModel(context = this@MainActivity.applicationContext)

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = MainGraph   ,
                        modifier = Modifier.padding(it)
                    ) {

               navigation <MainGraph>( startDestination = StartObScreen)  {

            composable <StartObScreen> {
                    StartScreen(
                             navController = navController,
                             timerViewModel = viewmodel
                             )
                                      }



            composable <TimerObScreen> {
                TimerScreenUI(
                             navController  = navController,
                             timerViewModel = viewmodel,
                            )
                                      }

                        }
                    }



                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HiitTimerTheme {

    }
}