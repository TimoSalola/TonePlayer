# TonePlayer
An example project built around TonePlayer.kt, a kotlin based buzzer.

CC0 -like license. Use as you wish, no need to mention the author nor do you owe me anything for using the code.

How to use
  MainActivity.kt <- An example of how to do stuff with TonePlayer
  
  TonePlayer.kt <- Cool stuff happens here
  
  Quote from TonePlayer.kt
 * Example usage:
 *      Play 400Hz for 3.2 seconds:
 *          TonePlayer.playHzForS(400, 3.2)
 *
 *      Play 300Hz for 600ms:
 *          TonePlayer.playHzForMs(300, 600)
 *
 *      Set custom bitrate
 *          TonePlayer.setCustomSampleRate()
 *
 *          Note, default 44100 should work for most devices.
 *
 *      Build hash tone with name"long", freq 400 Hz and duration 600 ms
 *          TonePlayer.constructHashArray(long, 400, 600)
 *
 *      Play hash with name "long"
 *          TonePlayer.playHashArray(long)
 *
 *          Note, hash tones exist so the app doesn't have to create the array multiple times.
 *
 *
 *      popRemoval
 *          Smoothens the last x values from an array. smoothSteps inside the pop removal function
 *          is responsible for smooth sample count.
