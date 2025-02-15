package com.mydestiny.hiittimer.screens.timerscreen

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydestiny.hiittimer.R
import com.mydestiny.hiittimer.data.IntervalsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch






  data class TimerStateData(
      val currentTime: Long = 0L,
      val showAlert: Boolean = false,
      val outerProgress: Float = 0f,
      val innerProgress: Float = 0f,
      val isGoingForward: Boolean = true
  )

  @Suppress("DEPRECATION")
  class TimerViewModel(@SuppressLint("StaticFieldLeak") private val context: Context) : ViewModel() {

      private val soundPool = SoundPool.Builder()
          .setMaxStreams(1)
          .setAudioAttributes(
              AudioAttributes.Builder()
                  .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                  .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                  .build()
          )
          .build()

      private var soundId: Int? = null

      private val _workoutDetail = MutableStateFlow (listOf(IntervalsInfo()))
      val workoutDetail: StateFlow<List<IntervalsInfo>> = _workoutDetail.asStateFlow()

      private var timeTotalList: Long = 0L

      private val _timerState = MutableStateFlow(TimerStateData())
      val timerState: StateFlow<TimerStateData> = _timerState.asStateFlow()


      private val _isTimerRunning  = MutableStateFlow(false )
      val isTimerRunning : StateFlow<Boolean>  = _isTimerRunning .asStateFlow()

      private val _index  = MutableStateFlow(0)
      val index : StateFlow<Int>  = _index  .asStateFlow()

      private val  _pausedTime = MutableStateFlow(0L)

     private var exercisesTotalTime: Long = 0L
      private var startTime: Long = 0L
      private val tic = 20L
      private var vibration = true
      private var job: Job? = null
      private var soundPlayed = false

      init {
          Log.d("TBViewMode", "VM Created ")
          loadSound()
      }

      private fun loadSound() {
          soundId = soundPool.load(context, R.raw.treescondscountdown, 1)
      }


      fun removeInterval(index: Int) = viewModelScope.launch(Dispatchers.IO) {
          val b = _workoutDetail.value.toMutableList()
          b.removeAt(index)
          _workoutDetail.value = b.toList()
      }

      fun addInterval() = viewModelScope.launch(Dispatchers.IO) {
          val b = _workoutDetail.value.toMutableList()
          b.add(IntervalsInfo())
          _workoutDetail.value = b.toList()
          Log.d("TBViewMode", "${workoutDetail.value}")
      }


      fun updateInterval(index: Int, intervalsInfo: IntervalsInfo) = viewModelScope.launch(Dispatchers.IO) {
          val b = _workoutDetail.value.toMutableList()
          b[index] = intervalsInfo
          _workoutDetail.value = b.toList()
      }



      fun startTimer() = viewModelScope.launch(Dispatchers.IO) {

          if (job?.isActive == true) return@launch

          // Check if timer was previously paused
          val wasPaused = !isTimerRunning.value && _timerState.value.currentTime > 0

          _isTimerRunning.value = true


          if (wasPaused) {
              // Resume from paused time
               startTime = System.currentTimeMillis() - _pausedTime.value
              _pausedTime.value = 0L
          } else {
              // Start a fresh timer
              startTime = System.currentTimeMillis()


          }


          job = viewModelScope.launch(Dispatchers.IO) {
              try {
                  while (isActive  ) {


                      timeTotalList  = _workoutDetail.value.sumOf { it.intervalDuration }


                      val elapsedTime = System.currentTimeMillis() - startTime



                      val remainingTime = _workoutDetail.value[_index.value].intervalDuration - elapsedTime

//                      if (remainingTime < 0) remainingTime = 0 // prevent negative remain time


                      exercisesTotalTime = _workoutDetail.value.subList(_index.value, _workoutDetail.value.size) .sumOf { it.intervalDuration } - elapsedTime

                      val outerProgress = remainingTime.toFloat() / _workoutDetail.value[_index.value].intervalDuration.toFloat()
                      val innerProgress = exercisesTotalTime.toFloat() / timeTotalList.toFloat()


                      //Play sound between 0-3 seconds from end of time
                      if (remainingTime in 0..3000L && !soundPlayed) {
                          playSound()
                          soundPlayed = true
                      }


                      updateTimerState(
                          currentTime = remainingTime,
                          outerProgress = outerProgress,
                          innerProgress = innerProgress
                      )


                      if (remainingTime <= 0L) {
                          soundPlayed = false
                          if (vibration) {
                              vibrate()
                          }



                          if (_index.value < _workoutDetail.value.size) {

                              _index.value += 1

                              updateTimerState(

                                  currentTime = _workoutDetail.value[_index.value+1].intervalDuration //set current timer to next index duration
                              )
                              startTime = System.currentTimeMillis()
                          } else {
                              _isTimerRunning.value =false


                              updateTimerState(   showAlert = true   )

                          }

                      }

                      delay(tic)

                  }
              } catch (e: IndexOutOfBoundsException) {
                  // Handle out-of-bounds exception more gracefully
                  Log.e("TimerViewModel", "Index out of bounds: ${e.message}")
                  _isTimerRunning.value =false
                      updateTimerState(  showAlert = true)

              }
          }

      }

      private  fun updateTimerState(

          currentTime: Long = _timerState.value.currentTime,

          showAlert: Boolean = _timerState.value.showAlert,
          outerProgress: Float = _timerState.value.outerProgress,
          innerProgress: Float = _timerState.value.innerProgress,
          isGoingForward: Boolean = _timerState.value.isGoingForward
      ) {
          viewModelScope.launch(Dispatchers.Main.immediate) {  // Ensure UI updates on Main
              _timerState.value = _timerState.value.copy(

                  currentTime = currentTime,

                  showAlert = showAlert,
                  outerProgress = outerProgress,
                  innerProgress = innerProgress,
                  isGoingForward = isGoingForward
              )
          }
      }




      fun pauseTimer() = viewModelScope.launch(Dispatchers.IO) {

          _pausedTime.value = System.currentTimeMillis() - startTime // Calculate elapsedTime

          _isTimerRunning.value =  false
          job?.cancel()

          soundPlayed = false // Reset soundPlayed on pause
          Log.d("TBViewMode", "isPause")

      }



      fun nextInterval() = viewModelScope.launch(Dispatchers.IO) {



          if (_index.value < _workoutDetail.value.size - 1) {

              job?.cancel()
              soundPlayed = false

              _index.value += 1
                updateTimerState(

                  currentTime = _workoutDetail.value[_index.value].intervalDuration  ,
                  isGoingForward = true
              )
              startTimer()
          }
          Log.d("TBViewMode", "Next Interval")

      }


      fun previousInterval() = viewModelScope.launch(Dispatchers.IO) {


          if (_index.value > 0) {

              job?.cancel()
              soundPlayed = false


              _index.value -= 1

              updateTimerState(

                  currentTime = _workoutDetail.value[_index.value ].intervalDuration,  // Set remaining time
                  isGoingForward = false
              )
              startTimer()
          }
          Log.d("TBViewMode", "Previous Interval ")

      }



      fun resetTimer() = viewModelScope.launch(Dispatchers.IO) {

          _index.value=0
          updateTimerState(   showAlert = false   )
          soundPool.release() // Release SoundPool resources
          job?.cancel()

          soundPlayed = false
           // Reset to default values

          Log.d("TBViewMode", "reset Viewmodel")
      }


      private fun vibrate() {
          val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
          } else {
              vibrator.vibrate(500) // Deprecated in API 26
          }
      }

      private fun playSound() {
          soundId?.let {
              soundPool.play(it, 1f, 1f, 1, 0, 1f)
          } ?: run {
              Log.e("TimerViewModel", "Sound not loaded yet!")
          }
      }



      override fun onCleared() {
          super.onCleared()
          _index.value=0
          updateTimerState(   showAlert = false   )
          soundPool.release() // Release SoundPool resources
          job?.cancel()
      //Ensure Timer is stopped.
      }


  }

























