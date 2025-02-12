package com.mydestiny.hiittimer.screens.timerscreen

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
      val index: Int = 0,
      val currentTime: Long = 0L,
      val isTimerRunning: Boolean = false,
      val showAlert: Boolean = false,
      val outerProgress: Float = 0f,
      val innerProgress: Float = 0f,
      val isGoingForward: Boolean = true
  )

  class TimerViewModel(private val context: Context) : ViewModel() {

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

      private val _workoutDetail = MutableStateFlow (listOf(IntervalsInfo())) // Start with one interval
      val workoutDetail: StateFlow<List<IntervalsInfo>> = _workoutDetail.asStateFlow()

      private var timeTotalList: Long = 0L


      private val _timerState = MutableStateFlow(TimerStateData())
      val timerState: StateFlow<TimerStateData> = _timerState.asStateFlow()

      private var exercisesTotalTime: Long = 0L
      private var startTime: Long = 0L
      private val tic = 20L
      private var vibration = true
      private var job: Job? = null
      private var soundPlayed = false

      init {
          Log.d("TBViewMode", "VM Created ")
          loadSound()  // Load sound in init
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

          updateTimerState(isTimerRunning = true) // Use

          startTime = System.currentTimeMillis()

          timeTotalList = _workoutDetail.value.sumOf { it.intervalDuration }

          job = viewModelScope.launch(Dispatchers.IO) { // Keep timer logic on IO

              try {
                  while (isActive && _timerState.value.isTimerRunning) {

                      val elapsedTime = System.currentTimeMillis() - startTime

                      val currentIndex = _timerState.value.index
                      val currentIntervalDuration = _workoutDetail.value[currentIndex].intervalDuration
                      val remainingTime = currentIntervalDuration - elapsedTime


                      exercisesTotalTime = _workoutDetail.value.subList(currentIndex, _workoutDetail.value.size)
                          .sumOf { it.intervalDuration } - elapsedTime

                      val outerProgress = remainingTime.toFloat() / currentIntervalDuration.toFloat()
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

                          val nextIndex = currentIndex + 1

                          if (nextIndex < _workoutDetail.value.size) {

                              updateTimerState(
                                  index = nextIndex
                              )
                              startTime = System.currentTimeMillis()


                          } else {
                              //Timer Completed
                              updateTimerState(
                                  isTimerRunning = false,
                                  showAlert = true
                              )

                          }

                      }

                      delay(tic)

                  }
              } catch (e: IndexOutOfBoundsException) {

                  Log.e("TimerViewModel", "Index out of bounds: ${e.message}")
                  updateTimerState(isTimerRunning = false, showAlert = true)

              } finally {
                  Log.d("TBViewMode", "Timer Job Finished (Normally or Cancelled)")

              }
          }

      }

        fun updateTimerState(
          index: Int = _timerState.value.index,
          currentTime: Long = _timerState.value.currentTime,
          isTimerRunning: Boolean = _timerState.value.isTimerRunning,
          showAlert: Boolean = _timerState.value.showAlert,
          outerProgress: Float = _timerState.value.outerProgress,
          innerProgress: Float = _timerState.value.innerProgress,
          isGoingForward: Boolean = _timerState.value.isGoingForward
      ) {
          viewModelScope.launch(Dispatchers.Main.immediate) {  // Ensure UI updates on Main
              _timerState.value = _timerState.value.copy(
                  index = index,
                  currentTime = currentTime,
                  isTimerRunning = isTimerRunning,
                  showAlert = showAlert,
                  outerProgress = outerProgress,
                  innerProgress = innerProgress,
                  isGoingForward = isGoingForward
              )
          }
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

      fun pauseTimer() = viewModelScope.launch(Dispatchers.IO) {

          updateTimerState(isTimerRunning = false)
          job?.cancel()
          Log.d("TBViewMode", "isPause")

      }

      fun resetTimer() = viewModelScope.launch(Dispatchers.IO) {
          job?.cancel()
          soundPlayed = false
          _timerState.value =TimerStateData()  // Reset to default values

          Log.d("TBViewMode", "reset Viewmodel")
      }


      fun nextInterval() = viewModelScope.launch(Dispatchers.IO) {

          val currentIndex = _timerState.value.index

          if (currentIndex < _workoutDetail.value.size - 1) {

              job?.cancel()
              soundPlayed = false

              updateTimerState(
                  index = currentIndex + 1,
                  isGoingForward = true
              )
              startTimer()
          }
          Log.d("TBViewMode", "Next Interval")

      }


      fun previousInterval() = viewModelScope.launch(Dispatchers.IO) {
          val currentIndex = _timerState.value.index

          if (currentIndex > 0) {

              job?.cancel()
              soundPlayed = false

              updateTimerState(
                  index = currentIndex - 1,
                  isGoingForward = false
              )
              startTimer()
          }
          Log.d("TBViewMode", "Previous Interval ")

      }

      override fun onCleared() {
          super.onCleared()
          soundPool.release() // Release SoundPool resources
          job?.cancel()   //Ensure Timer is stopped.
      }


  }

























