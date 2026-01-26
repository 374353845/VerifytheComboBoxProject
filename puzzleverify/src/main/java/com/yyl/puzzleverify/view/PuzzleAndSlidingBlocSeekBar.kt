package com.yyl.puzzleverify.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView

import com.yyl.puzzleverify.R
import com.yyl.puzzleverify.utils.dpToPx
import com.yyl.puzzleverify.utils.pxToDp

/**
 * 滑块和滑动条组合seekbar
 */
class PuzzleAndSlidingBlocSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // 正确的图标位置 （X 坐标）
    var rightX: Int = 883

    /**
     * 动态设置
     */
    val offsetProgress = 10

    /**
     * 拼图滑块的Y坐标 px
     */
    var puzzleSlidingBlocY: Int = 290

    /**
     * 拼图宽度
     */
    var puzzleW: Int = 1170


    val TAG_PUZZLE_VERIFICATION = "TAG_PUZZLE_VERIFICATION"


    var progressText: TextView? = null
    var imgpuzzleverBg: ImageView? = null

    var puzzleverifySeekBar: PuzzleverifySeekBar? = null
    var slidingBlocSeekBar: SlidingBlocSeekBar? = null
    var attrs: AttributeSet? = null
    var defStyleAttr: Int = 0

    init {

        this.attrs = attrs
        this.defStyleAttr = defStyleAttr
        orientation = LinearLayout.VERTICAL
        // 1. 加载自定义布局到当前 View 中
//        LayoutInflater.from(context).inflate(R.layout.puzzle_and_sliding_bloc_seekbar, this, true)
//
//        findId()

//        puzzleverifySeekBar!!.setOnCustomProgressChangeListener(object :
//            OnCustomProgressChangeListener {
//            override fun onProgressChanged(
//                seekBar: SeekBar,
//                progress: Int,
//                fromUser: Boolean
//            ) {
//
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//
//            }
//
//
//        })


//        executePuzzleVerification()
//        executeSlidingBloc()
    }
    // addview 之前给出宽高定义再加载   ，如果不定义宽高就默认宽高

    /**
     * 初始化view
     * 注意：必须调用 选择默认值 puzzleverifySeekBarW = 0  puzzleverifySeekBarH = 0
     */
    fun initViews(puzzleverifySeekBarW: Int, puzzleverifySeekBarH: Int) {
        puzzleverifySeekBar = PuzzleverifySeekBar(context, attrs, defStyleAttr)

        puzzleverifySeekBar!!.setPuzzleverifySeekBarWidthAndHeight(
            puzzleverifySeekBarW,
            puzzleverifySeekBarH
        )



        addView(puzzleverifySeekBar)
        slidingBlocSeekBar = SlidingBlocSeekBar(context, attrs, defStyleAttr)


        addView(slidingBlocSeekBar)


        executeSlidingBloc()
        executePuzzleVerification()
    }

    fun setMax(max : Int){

        slidingBlocSeekBar!!.setMax(max)
        puzzleverifySeekBar!!.setMax(max)
    }


    /**
     * 执行滑块
     */
    fun executeSlidingBloc() {


// 设置监听器
        slidingBlocSeekBar!!.setOnSlidingBlocListener(object : OnSlidingBlocListener {
            override fun onProgressChanged(
                seekBar: SlidingBlocSeekBar,
                progress: Int,
                fromUser: Boolean
            ) {


                puzzleverifySeekBar!!.setProgress(progress)
            }

            override fun onStopTrackingTouch(seekBar: SlidingBlocSeekBar) {
                // 用户停止拖动时的操作
                executeVerification(null )
            }
        })

        slidingBlocSeekBar!!.setVerificationResultCallback(object : VerificationResultCallback {
            override fun succeed() {

            }

            override fun failure() {

            }


        })
    }

    /**
     * 是否启动双重验证
     */
    fun isEnableDualVerification(enableDualVerification: Boolean) {

        puzzleverifySeekBar!!.isEnableDualVerification(enableDualVerification)
        slidingBlocSeekBar!!.isEnableDualVerification(enableDualVerification)
    }


    /**
     * 执行拼图验证
     */
    fun executePuzzleVerification() {


//        // 1. 移除SeekBar原生背景（轨道+进度条）
//        seekBarPuzzleVerification!!.setBackgroundDrawable(null) // 移除整体背景
//        seekBarPuzzleVerification!!.progressDrawable = null     // 移除进度条/轨道背景
//
//        // 2. 自定义滑动滑块（圆形图标，可替换为图片/其他形状）
//        val thumbDrawable = resources.getDrawable(R.drawable.verify_1)
//        seekBarPuzzleVerification!!.thumb = thumbDrawable       // 绑定滑块到SeekBar
//
//
//        val params = seekBarPuzzleVerification!!.layoutParams as RelativeLayout.LayoutParams
//        params.topMargin = puzzleSlidingBlocY.pxToDp(context)
//        seekBarPuzzleVerification!!.layoutParams = params
//
//
//        seekBarPuzzleVerification!!.max = puzzleW.pxToDp(context)
//        这个10 不能固定
//        seekBarPuzzleVerification!!.progress =imgpuzzleverBg!!.width-(imgpuzzleverBg!!.width -10)


        // 监听滑动事件
        puzzleverifySeekBar!!.setOnCustomProgressChangeListener(object :
            OnCustomProgressChangeListener {


            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                slidingBlocSeekBar!!.setProgress(progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                executeVerification(seekBar!!)
            }


        })
    }

    /**
     * 执行验证
     */
    fun executeVerification(seekBar: SeekBar? ) {
        //                左右边距偏移 4 个progress 点也为正确
        val leftRightX = rightX.toInt() - offsetProgress
        val rightRightX = rightX.toInt() + offsetProgress

        var progress: Int = 0


        if (seekBar == null) {
            progress = slidingBlocSeekBar!!.getProgress()

        } else {
            progress = seekBar!!.progress
        }


        if (progress >= leftRightX && progress <= rightRightX) {
            // 拼图正确

            // 处理联动的滑块
            Log.i(TAG_PUZZLE_VERIFICATION, "拼图正确")
            if (slidingBlocSeekBar != null) {
                slidingBlocSeekBar!!.setThumbBitmap(R.drawable.sliding_bloc_right)
                    .setBgColorActive("#D2F4EF").setBorderColorActive("#92E0D4").notifyView()
            }


        } else {
            // 拼图错误

            // 处理联动的滑块

            Log.i(TAG_PUZZLE_VERIFICATION, "拼图错误")
            if (slidingBlocSeekBar != null) {
                slidingBlocSeekBar!!.setThumbBitmap(R.drawable.sliding_bloc_failure)
                    .setBgColorActive("#FCE1E1").setBorderColorActive("#F9AEAE").notifyView()

            }
        }
    }

}