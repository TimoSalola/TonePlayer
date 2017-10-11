package com.example.timo.toneplayer

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import org.jetbrains.anko.async

/**
 * Created by Timo Salola on 17.9.2017.
 * Last updated 11.10.2017
 *
 * Based on morse player in "Radio Tool"
 *
 * Copy and use as you wish, no need to mention me in licenses or anything.
 *
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
 *
 *
 *
 */
object TonePlayer {


    //Default values
    var bitrate = 44100
    var doPopRemoval = true

    //Contains ShortArrays just so they don't have to be generated multiple times
    var hashArrays = HashMap<String, ShortArray>()

    var hashArrayTitles = ArrayList<String>()



    //Generate and play on the fly
    fun playHzForS(givenFreq: Int, givenS: Double){

        val durationInMs = (givenS*1000).toInt()

        playHzForMs(givenFreq, durationInMs)

    }


    //For adding new arrays to the hashmap just in case same array is used multiple times.
    fun constructHashArray(givenHash :String, givenFreq : Int, givenDura : Int){

        //What if an array with said has already exists?
        if(hashArrays.containsKey(givenHash)){
            hashArrays.remove(givenHash)

        }

        hashArrayTitles.add(givenHash)

        val samplesPerWave = (bitrate/(givenFreq*1.0))

        val deltaRad = ((Math.PI*2.0) / samplesPerWave)

        val sampleCount = ((bitrate*givenDura)/1000.0).toInt()

        var generatedArray = buildSineArray(sampleCount, deltaRad)

        if (doPopRemoval){

            generatedArray = popRemoval(buildSineArray(sampleCount, deltaRad))
        }

        hashArrays.put(givenHash, generatedArray)

    }


    //
    fun clearHashArrays(){
        hashArrays.clear()
        hashArrayTitles.clear()

    }

    fun doesHashExist(toSearch : String): Boolean{
        //Returns true if hash exists
        return hashArrayTitles.contains(toSearch)

    }



    fun disablePopRemoval(){
        doPopRemoval = false

    }

    fun enablePopRemoval(){
        doPopRemoval = true

    }

    fun setCustomBitrate(givenBitrate : Int){
        bitrate = givenBitrate

        clearHashArrays() // Not about to handle the conversions of existing hashes
        //Could be done with figure out delta rad of first sample, current bitrate

    }

    //Playing has array with key x
    fun playHashArray(givenHash: String) {

        val arrayToPlay = hashArrays[givenHash] as ShortArray

        playPCMArray(arrayToPlay)
    }

    fun playHzForMs(givenFreq: Int, givenDura: Int){

        //Main function, builds an array full of sine values and passes it onto playPCMArray function

        async {

            val samplesPerWave = (bitrate/(givenFreq*1.0))

            val deltaRad = ((Math.PI*2.0) / samplesPerWave)

            val sampleCount = ((bitrate*givenDura)/1000.0).toInt()

            Log.d("TonePlayer", "Sample count for array at $sampleCount, delta rad $deltaRad, samples per wave $samplesPerWave") //Values are right

            //Building sine array

            val sineArray = buildSineArray(sampleCount, deltaRad)


            //Playing sine array

            playPCMArray(sineArray)


        }
    }



    private fun buildSineArray(sampleSize : Int, deltaRad : Double): ShortArray{

        val sineArray = ShortArray(sampleSize)

        var atDegrees = 0.0

        for (i in 0 until sampleSize){ //Or 0..sampleSize-1
            sineArray[i] = (Math.sin(atDegrees)*Short.MAX_VALUE).toShort()
            atDegrees += deltaRad
        }


        if (doPopRemoval){
            return popRemoval(sineArray)
        }

        return sineArray

    }

    fun popRemoval(givenArray :ShortArray): ShortArray{

        //Pop removal is done with magic
        //  Tried 3 methods
        //  1.  Remove samples until distance to 0 is as low as possible, doesn't work on high frequencies
        //  2.  Remove samples until at peak and build bridge down from there
        //  3.  Divide the last x values of the array so they near zero, dampening the volume smoothly

        //Using method 3


        var atPos = givenArray.size-1

        val smoothSteps = 30

        if (givenArray.size <= smoothSteps) {return givenArray} //TOO SHORT ARRAY FOR POP REMOVAL

        for(i in 0 until smoothSteps){
            givenArray[atPos] = (givenArray[atPos] * (i*1.0/smoothSteps)) .toShort()
            atPos--
        }

        //sineWave should now have a smooth end

        return givenArray

    }



    fun playPCMArray(PCMArray: ShortArray){
        //Sound type
        //Samples per second, /2 since pcm16bit means 2 bits are parsed together
        //Output type, mono is safe
        //8 bit for -127 to 127, 16 for -32767 to 32767

        Log.d("TonePlayer","Samplerate $bitrate")


        val PCMTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                bitrate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                PCMArray.size*2,
                AudioTrack.MODE_STATIC
        )

        PCMTrack.write(PCMArray, 0, PCMArray.size)

        PCMTrack.play()

        val sleepTime = (1000*PCMArray.size/bitrate).toLong()

        Thread.sleep(sleepTime*3)

        PCMTrack.release()

    }


}