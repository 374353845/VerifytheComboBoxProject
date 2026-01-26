package com.verifythecomboboxproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.verifythecomboboxproject.databinding.ActivityPuzzleverBinding
import com.yyl.puzzleverify.utils.dpToPx

class PuzzleverifyActivity : ComponentActivity() {

    private lateinit var activityPuzzleverBinding :  ActivityPuzzleverBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        activityPuzzleverBinding = ActivityPuzzleverBinding.inflate(layoutInflater)
        setContentView(activityPuzzleverBinding.root)

        activityPuzzleverBinding.puzzleAndSlidingBlocSeekBar.initViews(0, 200f.dpToPx(this).toInt())
        activityPuzzleverBinding.puzzleAndSlidingBlocSeekBar.isEnableDualVerification(true)
        activityPuzzleverBinding.puzzleAndSlidingBlocSeekBar.setMax(1170)

    }








}