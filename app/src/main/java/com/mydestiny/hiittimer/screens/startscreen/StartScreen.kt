package com.mydestiny.hiittimer.screens.startscreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mydestiny.hiittimer.data.IntervalsInfo
import com.mydestiny.hiittimer.navigation.TimerObScreen
import com.mydestiny.hiittimer.screens.timerscreen.TimerViewModel


@Composable

fun StartScreen(
    modifier: Modifier  = Modifier,
    navController: NavController = NavController(context = LocalContext.current),
    timerViewModel: TimerViewModel,

    ){

    val listOfIntervals   by   timerViewModel.workoutDetail.collectAsState()


    val context = LocalContext.current

    Surface(modifier = modifier.fillMaxSize()) {


        Column {
            LazyColumn(
                modifier.weight(1f )
                    .fillMaxSize()
                    .padding(top = 10.dp)) {

                itemsIndexed(listOfIntervals) { index, interval ->

                    key(interval.id) {


                        IntervalCard(
                            number = index + 1,
                            deleteInterval = {
                                 timerViewModel.removeInterval(index)

                                             },
                            sendValue = {

                                timerViewModel.updateInterval(
                                    index,
                                    IntervalsInfo(
                                        id = interval.id,
                                        intervalName = it.intervalName,
                                        intervalDuration = it.intervalDuration
                                    )
                                )

                            }
                        )


                        }
                    }





                item {

                    CustomCard(
                                   modifier =Modifier.fillMaxWidth()
                                                     .padding(horizontal = 30.dp, vertical = 7.dp) ,
                                   sendValue = { timerViewModel.addInterval() }
                          )
                }



            }
            CustomCard(
                modifier =Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp) ,

                sendValue = {

                    if(listOfIntervals.isEmpty() ){

                        Toast.makeText(context , "No Interval is Added", Toast.LENGTH_LONG).show()
                    }
                   else {navController.navigate(TimerObScreen)}


                },
                icon = Icons.Filled.PlayArrow,
                text = "Start Timer",

            )


        }




    }
}

//@Preview
@Composable

fun IntervalCard(
    modifier: Modifier=Modifier,
       number:Int=0,
       sendValue : (IntervalsInfo) -> Unit={},

       deleteInterval :( )->  Unit={}
  ){

    var duration  by remember { mutableLongStateOf(10000) }
    var intervalName  by remember { mutableStateOf( "") }

   Card  (modifier = modifier
       .fillMaxWidth()
       .padding(horizontal = 15.dp, vertical = 3.dp)) {


       Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically

        ){
           Text(modifier = Modifier.padding(start = 7.dp),text ="$number . " , fontSize = 20.sp)

           Column(modifier = Modifier.weight(1f)) {
               TimePickerCard(
                   modifier = Modifier
                       .align(Alignment.CenterHorizontally)
                       .padding(bottom = 7.dp),

               ) {
                   duration = it
                   sendValue(IntervalsInfo(intervalDuration = duration, intervalName = intervalName))

               }

           TextField(
                      modifier = Modifier
                          .align(Alignment.CenterHorizontally)
                          .padding(bottom = 7.dp),
                      value = intervalName,
                      onValueChange = {
                                     intervalName  = it
                                    sendValue(IntervalsInfo(intervalDuration = duration, intervalName = intervalName))
                                      },
                      label = { Text(" Enter Name ") }
           )
           }

           IconButton(onClick = {deleteInterval()}) {

              Icon(imageVector =  Icons.Default.Delete,contentDescription = null)
           }
       }





    }
}

//@Preview
@Composable

fun CustomCard(


    modifier: Modifier=Modifier,
    sendValue : ( ) -> Unit={},
    icon:ImageVector = Icons.Default.Add,
    text : String=  " Add Interval ",
    containerColor :Color =  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f ),
    iconColor : Color =  MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f ),


    ){



    Card(
        modifier = modifier
            ,
        //   .clickable { sendValue() },
        colors = CardDefaults.cardColors(containerColor =  containerColor)

    ) {


        IconButton(

            modifier = Modifier
                .size(70.dp)
                .align(Alignment.CenterHorizontally)
                .padding(7.dp),
            onClick = {sendValue()},
            colors =   IconButtonDefaults.iconButtonColors( containerColor = MaterialTheme.colorScheme.primaryContainer.copy( 0.7f  ) )

        ) {

            Icon(imageVector = icon,contentDescription = null, tint = iconColor )

        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 7.dp),
            text =text
        )

    }
}




//@Preview(backgroundColor = 0xFFCCC2DC, showBackground = true )
@Composable
fun TimePickerCard(modifier : Modifier = Modifier,



                   durationValue : (Long) -> Unit ={},
)   {

    val secondsRange = 0..60
    val minutesRange = 0..180

    var minutes by remember { mutableIntStateOf(0) }
    var seconds by remember { mutableIntStateOf(10) }
    var intervalDuration =seconds  + minutes *60
    val duration = intervalDuration*1000L


    Card(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        Row(  modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {

            IconButton(onClick = {
                intervalDuration -= 5
                seconds=intervalDuration%60
                minutes = intervalDuration/60
                if (seconds  >secondsRange.last)seconds =60
                if (seconds  < secondsRange.first)seconds =0
                durationValue(duration)
            }) {
                Icon(imageVector = Icons.Filled.Remove, contentDescription = "" )

            }


            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .height(70.dp)
                    .width(70.dp) ,

                value = minutes.toString(),
                onValueChange = { value ->
                    minutes  = value.toIntOrNull() ?: 0

                    if (minutes  >minutesRange.last)minutes =90
                    if (minutes < minutesRange.first)minutes =0
                    if(minutes <10) minutes = minutes.toString().format("%002d", minutes ).toInt()

                },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,

                ),
                singleLine = true,

                maxLines = 1,

                label={ Text(text = "Min" ) },

                colors = OutlinedTextFieldDefaults.  colors(

                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer  ),

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                )

            Spacer(modifier = Modifier.width(2.dp))
            Text(text = ":", fontSize = 25.sp  )
            Spacer(modifier = Modifier.width(2.dp))


            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .height(70.dp)
                    .width(70.dp)

                ,
                value = seconds.toString(),
                onValueChange = { value ->
                    seconds  = value.toIntOrNull() ?: 0
                    if (seconds  >secondsRange.last)seconds =60
                    if (seconds  < secondsRange.first)seconds =0
                    if(seconds <10) seconds = seconds.toString().format("%002d", seconds ).toInt()
                    durationValue(duration)
                },
                singleLine = true,
                maxLines = 1,
                label={ Text(text = "Sec"  ) },
                colors = OutlinedTextFieldDefaults.
                colors(

                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer


                    ),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize =20.sp ,
                    ),

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )



            IconButton(onClick = {
                intervalDuration += 5
                seconds =intervalDuration%60
                minutes = intervalDuration/60
                if (seconds  >secondsRange.last)seconds =60
                if (seconds  < secondsRange.first)seconds =0

                durationValue(duration)
            }) {
                Icon(imageVector = Icons.Default.Add , contentDescription = "" )

            }




        }
    }
}


