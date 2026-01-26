package com.yyl.puzzleverify.view

import android.widget.SeekBar

interface OnCustomProgressChangeListener {

    /**
     * 滑动改变（滑动中）的回调方法
     */
    fun onProgressChanged(seekBar:SeekBar, progress: Int, fromUser: Boolean)

    /**
     * 滑动结束的回调方法
     */
    fun onStopTrackingTouch(seekBar: SeekBar)
}