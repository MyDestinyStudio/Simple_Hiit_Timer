package com.mydestiny.hiittimer.screens.timerscreen

 
 import android.annotation.SuppressLint
 import android.app.Activity
 import android.content.res.Configuration
 import android.os.Build
 import android.util.Log
 import android.view.WindowManager
 import androidx.activity.compose.BackHandler
 import androidx.annotation.RequiresApi
 import androidx.compose.animation.AnimatedContent
 import androidx.compose.animation.slideInVertically
 import androidx.compose.animation.slideOutVertically
 import androidx.compose.animation.togetherWith
 import androidx.compose.foundation.border
 import androidx.compose.foundation.layout.Box
 import androidx.compose.foundation.layout.BoxWithConstraints
 import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.size
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
 import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
 import androidx.compose.material.icons.filled.Lock
 import androidx.compose.material.icons.filled.Pause
 import androidx.compose.material.icons.filled.PlayArrow
 import androidx.compose.material.icons.outlined.LockOpen
 import androidx.compose.material3.AlertDialog
 import androidx.compose.material3.Button
 import androidx.compose.material3.Card
 import androidx.compose.material3.CardDefaults
 import androidx.compose.material3.Icon
 import androidx.compose.material3.IconButton
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Surface
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.DisposableEffect
 import androidx.compose.runtime.LaunchedEffect
 import androidx.compose.runtime.SideEffect
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableIntStateOf
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.runtime.saveable.rememberSaveable
 import androidx.compose.runtime.setValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.platform.LocalConfiguration
 import androidx.compose.ui.platform.LocalContext
 import androidx.compose.ui.text.TextStyle
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
 import androidx.constraintlayout.compose.ConstraintLayout
 import androidx.constraintlayout.compose.Dimension
 import androidx.navigation.NavController
 import androidx.navigation.compose.rememberNavController
 import androidx.navigation.navOptions
 import com.mydestiny.hiittimer.navigation.StartObScreen
 import com.mydestiny.hiittimer.navigation.TimerObScreen
 import com.mydestiny.hiittimer.utilitis.Arcs
 import com.mydestiny.hiittimer.utilitis.ExercisesIndicator
 import com.mydestiny.hiittimer.utilitis.TimeText
 import kotlinx.coroutines.delay


//@PreviewScreenSizes
@SuppressLint("SuspiciousIndentation", "ContextCastToActivity")
@RequiresApi(Build.VERSION_CODES.O)

@Composable
fun TimerScreenUI(
    navController: NavController = rememberNavController(),
    timerViewModel: TimerViewModel,
) {

    val timerState by timerViewModel.timerState.collectAsState() //Collect timer state



    val list by timerViewModel.workoutDetail.collectAsState()






    val configuration = LocalConfiguration.current
    val context = LocalContext.current as Activity
    val sWidth by remember { mutableIntStateOf(30) }
    var  hasRun  by rememberSaveable { mutableStateOf(false) }
    var isLocked by rememberSaveable { mutableStateOf(false) }
    var exitAlert by rememberSaveable { mutableStateOf(false) }

    //Local State only used for the UI.

    //Lifecycle effect for clearing the screen on flag
    DisposableEffect(Unit) {
        onDispose {
            Log.d("D", "OnDispose ")
            timerViewModel.resetTimer()
            context.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        }

    }
    LaunchedEffect(key1 =Unit) {

      //if (!hasRun )  {
           timerViewModel. updateTimerState(isTimerRunning = true)
            timerViewModel.startTimer()

          //  hasRun =true
            Log.d("D","ReLaunched B ")
       // }
    }


    LaunchedEffect(timerState.isTimerRunning) {

        if (timerState.isTimerRunning) {
            context.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        } else {
            context.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }



    if (timerState.showAlert) {

        LaunchedEffect(Unit) {


            delay(3000) // Show alert for 3 seconds
            navController.navigate(  StartObScreen, navOptions { popUpTo(TimerObScreen){inclusive=true} }  )
            timerViewModel.resetTimer()
        }

        AlertDialog(
            onDismissRequest = {
                timerViewModel.resetTimer()
                navController.navigate(  StartObScreen, navOptions { popUpTo(TimerObScreen){inclusive=true} }  )
            },
            title = { Text("Alert Dialog") },
            text = { Text("Timer Is Finished  ") },
            confirmButton = {
                Button(onClick = {
                    timerViewModel.resetTimer()
                    navController.navigate(  StartObScreen,
                        navOptions { popUpTo(TimerObScreen){inclusive=true} }  )
                }) {
                    Text("OK")
                }
            }
        )
    }

    if (exitAlert) {
        timerViewModel.pauseTimer()

        AlertDialog(

            onDismissRequest = {
                exitAlert = false
                timerViewModel.startTimer()
            },
            title = { Text("Are Sure you want to Exit ?") },

            dismissButton = {
                Button(
                    onClick = {

                        exitAlert = false
                        timerViewModel.startTimer()

                    }
                ) {
                    Text("No")
                }
            },
            confirmButton = {

                Button(
                    onClick = {
                        navController.navigate(  StartObScreen,  navOptions { popUpTo(TimerObScreen){inclusive=true} }  )
                        timerViewModel.resetTimer()
                    }
                ) {
                    Text("Yes")
                }
            }
        )
    }

    BackHandler {
        exitAlert = true
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (ppButton, bottomCard, lockButton, counter, noImage,intervalName) = createRefs()

            SlideCounter(
                modifier = Modifier
                    .constrainAs(counter) {
                        top.linkTo(parent.top, margin = 5.dp)
                        start.linkTo(ppButton.end)
                        end.linkTo(lockButton.start)

                    }
                    .height(70.dp),
                count = timerState.index + 1,
                size = list.size,
                fontSize = 38,
                isPlus = timerState.isGoingForward
            )



            IconButton(
                modifier = Modifier
                    .size(70.dp)
                    .constrainAs(ppButton) {
                        top.linkTo(parent.top, margin = 5.dp)
                        start.linkTo(parent.start)

                    },
                onClick = {
                    if (timerState.isTimerRunning) timerViewModel.pauseTimer() else if (!timerState.isTimerRunning) timerViewModel.startTimer()
                },
                enabled = !isLocked
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = if (timerState.isTimerRunning) {
                        Icons.Filled.Pause
                    } else if (!timerState.isTimerRunning) {
                        Icons.Filled.PlayArrow
                    } else {
                        Icons.Filled.Pause
                    },
                    contentDescription = ""
                )
            }
            IconButton(modifier = Modifier
                .size(70.dp)
                .constrainAs(lockButton) {
                    top.linkTo(parent.top, margin = 5.dp)
                    end.linkTo(parent.end)

                }, onClick = { isLocked = !isLocked }) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = if (isLocked) {
                        Icons.Filled.Lock
                    } else if (!isLocked) {
                        Icons.Outlined.LockOpen
                    } else {
                        Icons.Filled.Lock
                    }, contentDescription = ""
                )
            }




            AnimatedContent(modifier = Modifier.constrainAs(intervalName) {

                top.linkTo(counter.bottom)
                bottom.linkTo( noImage .top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                height = Dimension.wrapContent
                width = Dimension.wrapContent
            },
                targetState = timerState.index,
                transitionSpec = {
                    slideInVertically { if (timerState.isGoingForward) it else if (!timerState.isGoingForward) -it else it
                    } togetherWith slideOutVertically { if (timerState.isGoingForward) -it else if (!timerState.isGoingForward) it else -it } },
                label = "") {

                          Text(text =list[it].intervalName, fontSize = 37.sp )

                           }

            BoxWithConstraints(modifier = Modifier
                .constrainAs(noImage) {

                    top.linkTo(intervalName.bottom)
                    bottom.linkTo(bottomCard.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.wrapContent
                    width = Dimension.wrapContent
                }
                .border(2.dp, color = Color.Green), contentAlignment = Alignment.Center) {
                TimeText(
                    timeLeft = timerState.currentTime, textSize = 37
                )

                Arcs(
                    sizeArc = when (configuration.orientation) {

                        Configuration.ORIENTATION_PORTRAIT -> {
                            (this.maxWidth.value - 30).dp
                        }

                        Configuration.ORIENTATION_LANDSCAPE -> {
                            (this.maxHeight.value - 30).dp
                        }

                        else -> 200.dp
                    },

                    strokeWidth = sWidth,
                    valueInner = timerState.innerProgress ,
                    valueOuter = timerState.outerProgress
                )
            }



            Card(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(bottomCard) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.height((configuration.screenHeightDp / 4).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        modifier = Modifier.size(70.dp),
                        onClick = { timerViewModel.previousInterval() },
                        enabled = timerState.index != 0 && !isLocked
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = ""
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ExercisesIndicator(
                            index = timerState.index,
                            listOfExercise = list,
                            isPlus = timerState.isGoingForward
                        )
                    }


                    IconButton(
                        modifier = Modifier.size(70.dp),
                        onClick = {
                            timerViewModel.nextInterval()
                        },
                        enabled = timerState.index != list.lastIndex && !isLocked,

                        ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = ""
                        )

                    }
                }

            }
        }
    }

}









//@Preview
@Composable
fun SlideCounter(
    modifier: Modifier = Modifier,
    count: Int=12,
    size:Int =19,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    fontSize:Int =38,

    isPlus:Boolean = true
) {
    var previousCount by remember {
        mutableIntStateOf(count)
    }
    SideEffect {
        previousCount = count
    }

        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            val cString = count.toString()
            val oldCountString = previousCount.toString()

            for (i in cString.indices) {
                val oldChar = oldCountString.getOrNull(i)
                val newChar = cString[i]
                val char = if (oldChar == newChar) {
                    oldCountString[i]
                } else {
                    cString[i]
                }
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        slideInVertically { if (isPlus) it else if (!isPlus) -it else it } togetherWith slideOutVertically {
                            if (isPlus) -it else if (!isPlus) it else -it
                        }
                    }, label = ""
                ) { c ->
                    Text(text = "$c", style = style, softWrap = false, fontSize = fontSize.sp)


                }
            }
            Text(text = " / $size ", style = style, softWrap = false, fontSize = fontSize.sp)
        }

}
    


