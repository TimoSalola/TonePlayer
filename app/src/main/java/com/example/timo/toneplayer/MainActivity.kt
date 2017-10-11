package com.example.timo.toneplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast


/**
 * Created by Timo Salola on 17.9.2017.
 *
 * Based on morse player in "Radio Tool"
 *
 * Copy and use as you wish, no need to mention me in licenses or anything.
 *
 *      TonePlayer.kt is the main thing you should be copying from here if anything.
 *
 *      This class just serves as an example on how to use it.
 *
 *
 *
 *
 */

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()

        //TonePlayer.setCustomSampleRate(48000)
        //TonePlayer.disablePopRemoval()
    }

    var customToneDuration = 200
    var customToneTone = 200

    var createHashToneDuration = 200
    var createHashToneTone = 200




    private fun setListeners(){

        btnPlayLong.setOnClickListener( {
            playLong()
        })

        btnPlayShort.setOnClickListener( {
            playShort()
        })




        //Advanced listeners


        //PLAY CUSTOM TONE
        customToneDurationInput.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                customToneDuration = progress*50 +200
                customToneDurationTitle.text = "Duration ${customToneDuration} ms"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        customToneToneInput.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                customToneTone = progress*50 +200
                customToneToneTitle.text = "Tone ${customToneTone} Hz"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        btnPlayCustomTone.setOnClickListener( {
            playCustomTone()
        })




        //CREATE HASH TONE

        createHashToneToneInput.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                createHashToneTone = progress*50 +200
                createHashToneTitle.text = "Tone ${createHashToneTone} Hz"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        createHashToneDurationInput.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                createHashToneDuration = progress*50+200
                createHashToneDurationTitle.text = "Duration ${createHashToneDuration} ms"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        btnCreateHashToneSave.setOnClickListener( {
            saveCustomHashTone()

            //New hash tone exists
            checkIfHashExistst()
        })

        //PLAY HASH TONE

        btnPlayHashTone.setOnClickListener({
            playHashTone()
        })

        PlayHashToneNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                checkIfHashExistst()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })





    }


    private fun saveCustomHashTone(){
        if(editTextNewHashToneName.text.toString().isEmpty()){
            this.toast("Can't save tone with empty name") //Actually this can be done, just don't see it
            return
        }

        TonePlayer.constructHashArray(editTextNewHashToneName.text.toString(), createHashToneTone, createHashToneDuration)
    }


    private fun checkIfHashExistst(){
        val existance = TonePlayer.doesHashExist(PlayHashToneNameInput.text.toString())

        if (existance) {
            playHashToneConfirm.text = "Hash exists"
        } else {
            playHashToneConfirm.text = "Hash doesn't exist"
        }

    }

    private fun playHashTone(){

        if (TonePlayer.doesHashExist(PlayHashToneNameInput.text.toString())){
            var hashName = PlayHashToneNameInput.text.toString()

            TonePlayer.playHashArray(hashName)

        }

    }


    private fun playCustomTone(){
        TonePlayer.playHzForMs(customToneTone,customToneDuration)

    }


    private fun playLong(){

        //Play 400Hz for 1.5 seconds
        TonePlayer.playHzForMs(400, 1500)


    }

    private fun playShort(){

        //Play 400Hz for 500ms
        TonePlayer.playHzForMs(400, 500)




    }


}
