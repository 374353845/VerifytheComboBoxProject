package com.yyl.puzzleverify.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.yyl.puzzleverify.R
import com.yyl.puzzleverify.utils.dpToPx
import com.yyl.puzzleverify.utils.pxToDp

class PuzzleverifySeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    // 正确的图标位置 （X 坐标）
    var rightX: Int = 883

    /**
     * 动态设置
     */
    val offsetProgress = 10

    /**
     * 拼图滑块的Y坐标 px
     */
    var puzzleSlidingBlocY: Float = 290f

    /**
     * 拼图宽度
     */
    var puzzleW: Float = 1170f


    val TAG_PUZZLE_VERIFICATION = "TAG_PUZZLE_VERIFICATION"


    var imgpuzzleverBg: ImageView? = null
    var seekBarPuzzleVerification: SeekBar? = null

    // 背景图
    var verifyBgDrawable: Drawable? = null
    var onProgressChangeListener: OnCustomProgressChangeListener? = null

    /**
     * 启动双重认证 （默认关闭）
     * 双重即拼图和滑动滑块认证
     */
    var enableDualVerification: Boolean = false

    init {


        verifyBgDrawable = ContextCompat.getDrawable(context, R.drawable.verify_bg_1)


    }


    /**
     *  创建自定义SeekBar（只有验证图片无背景）
     * @param context 上下文（Activity/Fragment/View 均可）
     * @param h 该值为0时，为默认值 200dp
     * @param w 该值为0时，为默认值 LayoutParams.MATCH_PARENT
     * @return 创建好的 SeekBar 实例
     */

    fun createPuzzleVerifySeekBar(w: Int, h: Int): SeekBar {
        var height = h
        var width = w
        if (h == 0) {
            height = 200f.dpToPx(context).toInt()
        }

        if (w == 0) {
            width = LayoutParams.MATCH_PARENT
        }


        apply {

            layoutParams = RelativeLayout.LayoutParams(
                width,
                height // 200dp 转 px
            )
        }
        // 1. 创建 SeekBar 实例
        seekBarPuzzleVerification = SeekBar(context).apply {


            // 2. 先创建 LayoutParams，再设置宽高（核心修复：避免空指针）
            layoutParams = RelativeLayout.LayoutParams(
                width,
                height // 200dp 转 px
            ).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                topMargin = puzzleSlidingBlocY.pxToDp(context).toInt()
            }

            // 1. 移除SeekBar原生背景（轨道+进度条）
            setBackgroundDrawable(null) // 移除整体背景
            progressDrawable = null     // 移除进度条/轨道背景

            // 2. 自定义滑动滑块（圆形图标，可替换为图片/其他形状）
            val thumbDrawable = resources.getDrawable(R.drawable.verify_1)
            thumb = thumbDrawable       // 绑定滑块到SeekBar


//        这个10 不能固定
            progress = verifyBgDrawable!!.intrinsicWidth - (verifyBgDrawable!!.intrinsicWidth - 30)


            // 2. 设置最小值（对应 xml: android:min="0"）
//            min =imgpuzzleverBg!!.width-(imgpuzzleverBg!!.width -10)
            min = 0
            max = puzzleW.dpToPx(context).toInt()
            // 3. 设置内边距（对应 xml: android:padding="0dp"）
            setPadding(0, 0, 0, 0)


        }
        executePuzzleVerification()
        return seekBarPuzzleVerification!!
    }

    /**
     * 创建一个背景图
     * @param context 上下文（Activity/Fragment/View 均可）
     * @return 创建好的 ImageView 实例
     */
    fun createPuzzleVerifyBgImageView(): ImageView {
        // 1. 创建 ImageView 实例
        imgpuzzleverBg = ImageView(context).apply {


            // 2. 先创建 LayoutParams，再设置宽高（核心修复：避免空指针）
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                200f.dpToPx(context).toInt()
            ).apply {
                // 设置水平居中（对应 xml 中的 android:layout_centerHorizontal="true"）
                addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            }


            scaleType = ImageView.ScaleType.FIT_CENTER

            setImageDrawable(verifyBgDrawable)


        }

        return imgpuzzleverBg!!
    }

    fun setMax(max: Int) {
        seekBarPuzzleVerification!!.max = max
    }

    /**
     * 执行拼图验证
     */
    fun executePuzzleVerification() {


        // 监听滑动事件
        seekBarPuzzleVerification!!.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            // 进度改变时触发
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChangeListener!!.onProgressChanged(seekBar!!, progress, fromUser)

            }

            // 开始滑动时触发
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 可选：滑动开始的逻辑

                //在这里讲图标改成 白色
            }

            // 停止滑动时触发
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 可选：滑动结束的逻辑
                if (onProgressChangeListener != null) {
                    onProgressChangeListener!!.onStopTrackingTouch(seekBar!!)
                } else {
                    Log.i(TAG_PUZZLE_VERIFICATION, "未设置OnCustomProgressChangeListener")
                }
                executeVerification(seekBar!!)

            }


        })
    }


    /**
     * 设置PuzzleverifySeekBar的宽高
     * 注：初始化必须调用的api， 选择默认值 w = 0  h = 0
     */
    fun setPuzzleverifySeekBarWidthAndHeight(w: Int, h: Int) = apply {

        apply {
            addView(createPuzzleVerifyBgImageView())
            addView(createPuzzleVerifySeekBar(w, h))

        }


    }

    fun setOnCustomProgressChangeListener(onProgressChangeListener: OnCustomProgressChangeListener) {

        this.onProgressChangeListener = onProgressChangeListener
    }


    /**
     * 是否启动双重验证
     */
    fun isEnableDualVerification(enableDualVerification: Boolean) {

        this.enableDualVerification = enableDualVerification
    }

    /**
     * 执行验证
     */
    fun executeVerification(seekBar: SeekBar?) {

        if (enableDualVerification) {
            Log.i(TAG_PUZZLE_VERIFICATION, "双重认证已开启，不执行拼图验证")
            return
        }
        //                左右边距偏移 4 个progress 点也为正确
        val leftRightX = rightX.toInt() - offsetProgress
        val rightRightX = rightX.toInt() + offsetProgress

        var progress: Int = 0



        progress = seekBar!!.progress


        if (progress >= leftRightX && progress <= rightRightX) {
            // 拼图正确


            Log.i(TAG_PUZZLE_VERIFICATION, "拼图正确")


        } else {
            // 拼图错误


            Log.i(TAG_PUZZLE_VERIFICATION, "拼图错误")


        }
    }

    fun setProgress(progress: Int) {
        seekBarPuzzleVerification!!.setProgress(progress)

    }

}
