@file:Suppress("KotlinConstantConditions")

package com.mydestiny.hiittimer.utilitis

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mydestiny.hiittimer.ui.theme.PurpleGrey40

import com.mydestiny.hiittimer.data.IntervalsInfo

@Composable
//@Preview
fun Arcs(sizeArc: Dp =200.dp, strokeWidth:Int=10,
         valueInner:Float=0.8f, valueOuter :Float=0.4f){
    Canvas(modifier =  Modifier.size( sizeArc ) ) {
        //  val centreOfBox = Offset(size.value.width.toFloat()/2,size.value.height.toFloat()/2,)
        val outerRadius =
            (sizeArc.toPx() / 2) - (strokeWidth.toFloat() * 1f)
        val innerRadius =
            (sizeArc.toPx()  / 2) - (strokeWidth.toFloat() * 2f + 4f)
        // Draw outer arc
        drawArc(
            color = PurpleGrey40,
            startAngle = -360f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size( outerRadius.toDp().toPx() * 2   , outerRadius * 2),
            style = Stroke(strokeWidth.toDp().toPx(), cap = StrokeCap.Round)

        )
        drawArc(
            color = Color.Red,
            startAngle = -360f,
            sweepAngle = 355f * valueInner,
            useCenter = false,
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = Stroke(strokeWidth.toDp().toPx(), cap = StrokeCap.Round)

        )

        // Draw inner arc
        drawArc(
            color = Color.LightGray,
            startAngle = -360f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = Size(innerRadius * 2, innerRadius * 2),
            style = Stroke(strokeWidth.toDp().toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color =  Color.Blue,
            startAngle = -360f,
            sweepAngle = 355f * valueOuter,
            useCenter = false,
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = Size(innerRadius * 2, innerRadius * 2),
            style = Stroke(strokeWidth.toDp().toPx(), cap = StrokeCap.Round)
        )


    }
}

val ExpTA = mutableListOf(
    IntervalsInfo (   intervalDuration =  10000, intervalName = "Jumping jacks"),
    IntervalsInfo (     intervalDuration = 10000,  intervalName ="Push Up"),
    IntervalsInfo (  intervalDuration = 10000, intervalName ="Push Up"),
    IntervalsInfo (     intervalDuration = 10000, intervalName ="Abs"),
    IntervalsInfo(    intervalDuration =40000, intervalName ="Rest BB")
                                  )

@Composable
 @Preview(showBackground = true)
fun ExercisesIndicator (
    modifier: Modifier=Modifier,
    visible:Boolean=true,
    index: Int=0,
    listOfExercise :  List  <IntervalsInfo> = ExpTA,
    isPlus : Boolean= true,

    ){
    val customList = listOfExercise.toMutableList()
    customList.add( 0,
        IntervalsInfo(  intervalDuration = 0, intervalName = "START !!" )
    )
    customList.add(customList.lastIndex+1,
        IntervalsInfo(  intervalDuration = 0, intervalName = "FINISH" )
    )
    customList.add(customList.lastIndex+1,
        IntervalsInfo(  intervalDuration = 0, intervalName = "" )
    )

      Row (modifier = modifier , verticalAlignment = Alignment.CenterVertically
             ){
            if (visible) {
                TriangleL(sizeC = 20.dp)
            }


            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)) {

               if(visible) {
                    AnimatedContent(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                        targetState = customList[index],
                        transitionSpec = { slideInVertically { if (isPlus) it else if (!isPlus) -it else it } togetherWith slideOutVertically { if (isPlus) -it else if (!isPlus) it else -it } },
                        label = "") { interval ->
                        IntervalIndicator(index, interval)
                    }
                }

               if (visible) {
                    Card(
                        modifier = Modifier
                            .height(3.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Green)
                    ) {}
                }

                    AnimatedContent(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(), targetState = customList[ index+1 ],
                        transitionSpec = {
                            slideInVertically { if (isPlus) it else if(!isPlus) -it else it } togetherWith slideOutVertically { if (isPlus) -it else if(!isPlus) it else -it  }

                        }, label = "") {
                            interval  -> IntervalIndicator( index+1 ,interval, visible = visible   )
                    }

                if (visible) {

    Card(modifier= Modifier
        .height(3.dp)
        .fillMaxWidth(), colors=CardDefaults.cardColors(containerColor=Color.Green) ) {}

                }
                if(visible) {
                    AnimatedContent(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(), targetState = customList[index + 2],
                        transitionSpec = {
   slideInVertically { if (isPlus) it else if (!isPlus) -it else it } togetherWith slideOutVertically { if (isPlus) -it else if (!isPlus) it else -it }
                  }, label = ""
                    ) { interval ->
                        IntervalIndicator(index + 2, interval)
                    }
                }

            }

        if (visible) {  TriangleR(sizeC = 20.dp)  }

        }
}

@Composable
 //@Preview
fun IntervalIndicator(
    index : Int = 0,
    interval : IntervalsInfo,
    visible: Boolean=true,
    intervalIndicatorHeight:Dp =30.dp

){
    Card(modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 2.dp)
        .height(intervalIndicatorHeight)

    ) {

   Row(
       Modifier
           .background(Color.Red)
           .fillMaxSize()  ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
   ){

       if (visible) {
           Text(
               modifier = Modifier.weight(0.2f),
               text =
               if (interval.intervalName == "START !!" || interval.intervalName == "FINISH") ""
               else if (  interval.intervalName == "" && interval.intervalDuration == 0L) ""
               else (index).toString(),
               textAlign = TextAlign.Center,
               color = Color.White,
               overflow = TextOverflow.Ellipsis,
               maxLines = 1,
               fontSize = 20.sp
           )
       }

    Text(modifier = Modifier.weight(0.4f),
        text =   interval.intervalName
               , textAlign = TextAlign.Center, color = Color.White , overflow = TextOverflow.Ellipsis, maxLines = 1 , fontSize = 20.sp  )

       if (visible) {
           Text(
               modifier = Modifier.weight(0.4f),
               text = if (interval.intervalDuration == 0L) ""
               else if (  interval.intervalName == "" && interval.intervalDuration == 0L) ""
               else formatToMMSS(interval.intervalDuration),
               textAlign = TextAlign.Center,
               color = Color.White,
               overflow = TextOverflow.Ellipsis,
               maxLines = 1,
               fontSize = 20.sp
           )
       }
    }
}

}
//@Preview
@Composable

fun TriangleL(
    modifier: Modifier = Modifier,
    sizeC: Dp=100.dp,
    color: Color = Color.Green,

) {
    Canvas(
        modifier = modifier.size(sizeC)
    ) {
        val path = Path().apply {
            moveTo(0f   , 0f) // Move to the top-center
            lineTo(size.width, size.height/2) // Draw a line to the bottom-left
            lineTo(0f, size.height) // Draw a line to the bottom-right
            close() // Close the path to complete the triangle
        }

        drawPath(
            path = path,
            color = color
        )
    }
}
//@Preview
@Composable

fun TriangleR(
    modifier: Modifier = Modifier,
    sizeC: Dp=100.dp,
    color: Color = Color.Green,


) {
    Canvas(
        modifier = modifier.size(sizeC)
    ) {
        val path = Path().apply {
            moveTo(size.width    , 0f) // Move to the top-center
            lineTo(0f, size.width/2) // Draw a line to the bottom-left
            lineTo(size.width    , size.height) // Draw a line to the bottom-right
            close() // Close the path to complete the triangle
        }

        drawPath(
            path = path,
            color = color
        )
    }
}

@Composable

fun TimeText(modifier: Modifier=Modifier,

             timeLeft :Long = 20000L,
             textSize :Int=   35 ,
             color: Color = Color.Green

){

    Text(modifier=modifier,text = formatToMMSSMillis(timeLeft), color = color, fontSize = textSize.sp)



}