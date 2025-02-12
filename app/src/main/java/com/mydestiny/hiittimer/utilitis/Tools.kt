package com.mydestiny.hiittimer.utilitis

import android.annotation.SuppressLint


@SuppressLint("DefaultLocale")
fun formatToMMSSMillis(milliseconds: Long): String {
    val minutes = milliseconds / (1000 * 60)
    val seconds = (milliseconds % (1000 * 60)) / 1000
    val millis = milliseconds % 1000
    return String.format("%02d:%02d.%1d", minutes, seconds, millis)
}

@SuppressLint("DefaultLocale")
fun formatToMMSS(milliseconds: Long): String {
    val minutes = milliseconds / (1000 * 60)
    val seconds = (milliseconds % (1000 * 60)) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}


