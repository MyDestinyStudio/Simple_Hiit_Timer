package com.mydestiny.hiittimer.data

import java.util.UUID


data class  IntervalsInfo(
             val id :String= UUID.randomUUID().toString(),
             val  intervalDuration:Long=10000L  ,
            val  intervalName:String=""

)

