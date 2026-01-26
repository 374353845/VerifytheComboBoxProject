package com.yyl.puzzleverify.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.yyl.puzzleverify.R
import com.yyl.puzzleverify.utils.dpToPx


/**
 * 滑块seekbar
 * ps: 为什么不直接用seekbar,因为seekbar 定制的划中部分背景线不能覆盖图标，
 * 看起来很丑陋，所以用画布来开发  可查看drawabe下的图片 test1_deletable.png 效果，该图片可删除
 */
class SlidingBlocSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 1. 定义属性和变量
    private val TAG = "SlidingBlocSeekBar"
    private var progress: Int = 0
    private var max: Int = 1000
    private var min: Int = 0

    // 颜色定义
    private var borderColorActive = Color.parseColor("#75BDFC")   // 边框-激活
    private var borderColorInactive = Color.parseColor("#E4E7EB") // 边框-未激活
    private var bgColorActive = Color.parseColor("#D1E9FE")      // 背景-激活
    private var bgColorInactive = Color.parseColor("#F7F9FA")    // 背景-未激活


    // 尺寸定义 (单位: dp)
    private val trackHeight = 50f.dpToPx(context) // 轨道和滑块的高度
    private val trackBorderWidth = 1f.dpToPx(context)
    private val cornerRadius = 2f.dpToPx(context) // 增大圆角以匹配高度

    // 滑块图标和尺寸
    private var thumbBitmap: Bitmap? = null
    private var thumbHalfWidth: Float = 0f
    private val thumbHalfHeight = 49f.dpToPx(context) // 滑块的高度

    // 绘制相关的 Paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 触摸事件相关
    private var isDragging = false
    private var progressPositionX: Float = 0f

    // 监听器
    private var onSlidingBlocListener: OnSlidingBlocListener? = null
    private var verificationResultCallback: VerificationResultCallback? = null

    // 正确的图标位置 （X 坐标）
    var rightX: Int = 883

    /**
     * 动态设置
     */
    val offsetProgress = 10

    /**
     * 是否使用默认的样式
     * 注:如果不使用,必须设置为 false
     */
    var isUseDefaultStyle: Boolean = true

    /**
     * 启动双重认证 （默认关闭）
     * 双重即拼图和滑动滑块认证
     */
    var enableDualVerification: Boolean = false

    // 2. 初始化
    init {
        // 加载并缩放滑块图标
        try {


            val drawable = ContextCompat.getDrawable(context, R.drawable.move_right)
            if (drawable != null) {
                // 缩放图标以匹配轨道高度
                thumbBitmap = getScaledBitmap(drawable, thumbHalfHeight.toInt())
                thumbHalfWidth = thumbBitmap!!.width / 2f

                //设置最小进度，使图标不会移出左边半个宽度
                min = (thumbHalfWidth + cornerRadius + 2f.dpToPx(context)).toInt()
                progress = min
                setProgress(progress)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果图标加载失败，创建一个默认的色块
            thumbHalfWidth = trackHeight / 2f
        }
    }

    // 3. 测量尺寸 (onMeasure)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        // 高度由轨道高度和内边距决定
        val height = (trackHeight + paddingTop + paddingBottom).toInt()
        setMeasuredDimension(width, height)
    }

    // 4. 绘制内容 (onDraw) - 核心部分
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (thumbBitmap == null) return

        // 计算轨道的有效绘制区域
        val trackLeft = paddingLeft + thumbHalfWidth
        val trackTop = paddingTop.toFloat()
        val trackRight = width - paddingRight - thumbHalfWidth

        val trackBottom = trackTop + trackHeight

        // 计算进度的位置
        val progressRatio = progress.toFloat() / max.toFloat()
        progressPositionX = trackLeft + (trackRight - trackLeft) * progressRatio

        // --- 绘制轨道 ---

        // 1. 绘制未划过的背景和边框
        paint.color = bgColorInactive
        canvas.drawRoundRect(
            progressPositionX,
            trackTop,
            trackRight,
            trackBottom,
            cornerRadius,
            cornerRadius,
            paint
        )

        paint.color = borderColorInactive
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = trackBorderWidth
        val inactiveBorderRect = RectF(
            progressPositionX + trackBorderWidth / 2,
            trackTop + trackBorderWidth / 2,
            trackRight - trackBorderWidth / 2,
            trackBottom - trackBorderWidth / 2
        )
        canvas.drawRoundRect(inactiveBorderRect, cornerRadius, cornerRadius, paint)

        // 2. 绘制已划过的背景和边框
        paint.color = bgColorActive
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(
            trackLeft,
            trackTop,
            progressPositionX,
            trackBottom,
            cornerRadius,
            cornerRadius,
            paint
        )

        paint.color = borderColorActive
        paint.style = Paint.Style.STROKE
        val activeBorderRect = RectF(
            trackLeft + trackBorderWidth / 2,
            trackTop + trackBorderWidth / 2,
            progressPositionX - trackBorderWidth / 2 + thumbHalfWidth,
            trackBottom - trackBorderWidth / 2
        )
        canvas.drawRoundRect(activeBorderRect, cornerRadius, cornerRadius, paint)

        // --- 绘制滑块图标 ---
        val thumbCenterY = trackTop + trackHeight / 2f
        canvas.drawBitmap(
            thumbBitmap!!,
            progressPositionX - thumbHalfWidth,
            thumbCenterY - thumbBitmap!!.height / 2f,
            paint
        )
    }

    // 5. 处理触摸事件 (onTouchEvent)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (thumbBitmap == null) return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val trackTop = paddingTop.toFloat()
                val trackBottom = trackTop + trackHeight
                // 判断触摸点是否在轨道的矩形区域内
                if (event.x >= progressPositionX - thumbHalfWidth &&
                    event.x <= progressPositionX + thumbHalfWidth &&
                    event.y >= trackTop &&
                    event.y <= trackBottom
                ) {
                    isDragging = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val trackLeft = paddingLeft + thumbHalfWidth
                    val trackRight = width - paddingRight - thumbHalfWidth

                    var newProgressX = event.x

                    newProgressX = newProgressX.coerceIn(trackLeft, trackRight)
                    val newProgress =
                        (((newProgressX - trackLeft) / (trackRight - trackLeft)) * max).toInt()

                    if (newProgress != progress) {
                        setProgress(newProgress)
                        if (onSlidingBlocListener != null) {
                            onSlidingBlocListener?.onProgressChanged(this, newProgress, true)
                        } else {
                            Log.i(TAG, "未设置OnSlidingBlocListener")
                        }
                    }
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    parent.requestDisallowInterceptTouchEvent(false)
                    if (onSlidingBlocListener != null) {
                        onSlidingBlocListener?.onStopTrackingTouch(this)
                    } else {
                        Log.i(TAG, "未设置OnSlidingBlocListener")
                    }
                    executeVerification()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }


    fun getProgress(): Int = progress

    /**
     * 是否启动双重验证
     */
    fun isEnableDualVerification(enableDualVerification : Boolean){

        this.enableDualVerification =enableDualVerification
    }

    /**
     * 执行验证
     */
    fun executeVerification() {

        if (enableDualVerification) {
            Log.i(TAG, "双重认证已开启，不执行滑块验证")
            return
        }
        //                左右边距偏移 4 个progress 点也为正确
        val leftRightX = rightX.toInt() - offsetProgress
        val rightRightX = rightX.toInt() + offsetProgress

        var progress: Int = 0



        if (progress >= leftRightX && progress <= rightRightX) {
            // 拼图正确
            Log.i(TAG, "拼图正确")
            if (verificationResultCallback != null) {
                verificationResultCallback!!.succeed()
            }

            if (isUseDefaultStyle) {
                setThumbBitmap(R.drawable.sliding_bloc_right).setBgColorActive("#D2F4EF")
                    .setBorderColorActive("#92E0D4").notifyView()
            }

        } else {
            // 拼图错误
            Log.i(TAG, "拼图错误")
            if (verificationResultCallback != null) {
                verificationResultCallback!!.failure()
            }
            if (isUseDefaultStyle) {
                setThumbBitmap(R.drawable.sliding_bloc_failure).setBgColorActive("#FCE1E1")
                    .setBorderColorActive("#F9AEAE").notifyView()
            }

        }
    }

    /**
     * 使用该方法进行滑动事件的监听
     */
    fun setOnSlidingBlocListener(listener: OnSlidingBlocListener) {
        this.onSlidingBlocListener = listener
    }


    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) return drawable.bitmap
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getScaledBitmap(drawable: Drawable, targetHeight: Int): Bitmap {
        val originalBitmap = drawableToBitmap(drawable)
        val scale = targetHeight.toFloat() / originalBitmap.height
        val targetWidth = (originalBitmap.width * scale).toInt()
        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
    }


    fun setMax(maxVal: Int) = apply {
        require(max > 0) { "Max value must be greater than 0" }
        this.max = maxVal - min

        invalidate()
    }

    /**
     * 设置拖动的图标
     */
    fun setThumbBitmap(thumbBitmapId: Int) = apply {

        val drawable = ContextCompat.getDrawable(context, thumbBitmapId)
        if (drawable != null) {
            // 缩放图标以匹配轨道高度
            this.thumbBitmap = getScaledBitmap(drawable, thumbHalfHeight.toInt())
        }
        return this;
    }

    fun isUseDefaultStyle(isUseDefaultStyle: Boolean) = apply {
        this.isUseDefaultStyle = isUseDefaultStyle

    }

    /**
     * 设置边框-激活颜色
     */
    fun setBorderColorActive(colorStr: String) = apply {
        borderColorActive = Color.parseColor(colorStr)
        return this;
    }

    /**
     * 设置边框-未激活颜色
     */
    fun setBorderColorInactive(colorStr: String) = apply {
        borderColorInactive = Color.parseColor(colorStr)
        return this;
    }

    /**
     * 设置背景-激活颜色
     */
    fun setBgColorActive(colorStr: String) = apply {
        bgColorActive = Color.parseColor(colorStr)
        return this;
    }

    /**
     * 设置背景-未激活颜色
     */
    fun setBgColorInactive(colorStr: String) = apply {
        bgColorInactive = Color.parseColor(colorStr)
        return this;
    }

    // 6. 公共方法和监听器 (与之前版本相同)
    fun setProgress(progress: Int) = apply {

        if (max >= min) {
            var newProgress = progress.coerceIn(min, max - min)
            if (this.progress != newProgress) {
                this.progress = newProgress
                invalidate()
            }
        } else {
            Log.i(TAG, "max 不能小于 min ")
        }

    }

    /**
     * 刷新view
     */
    fun notifyView() = apply {
        invalidate()

    }

    fun setVerificationResultCallback(verificationResultCallback: VerificationResultCallback) =
        apply { this.verificationResultCallback = verificationResultCallback }


}