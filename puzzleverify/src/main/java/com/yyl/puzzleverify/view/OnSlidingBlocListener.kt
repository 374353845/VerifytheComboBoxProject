package com.yyl.puzzleverify.view

interface OnSlidingBlocListener {



        /**
         * 滑动改变（滑动中）的回调方法
         */
        fun onProgressChanged(seekBar: SlidingBlocSeekBar, progress: Int, fromUser: Boolean)

        /**
         * 滑动结束的回调方法
         */
        fun onStopTrackingTouch(seekBar: SlidingBlocSeekBar)



}