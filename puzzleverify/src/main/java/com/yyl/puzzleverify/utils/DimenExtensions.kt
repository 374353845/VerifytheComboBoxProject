package com.yyl.puzzleverify.utils

import android.content.Context
import android.util.TypedValue

/**
 * 将像素值转换为 dp 值。
 *
 * @receiver Int 要转换的像素值 (px)
 * @param context Context 上下文，用于获取 DisplayMetrics
 * @return Float 转换后的 dp 值
 */
fun Int.pxToDp(context: Context): Int {
    // TypedValue.applyDimension 是 Android 提供的标准转换方法
    // COMPLEX_UNIT_PX: 表示输入的值是像素
    // this.toFloat(): 'this' 指的是调用该函数的 Int 类型的 px 值
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Float.dpToPx(context: Context): Float = this * context.resources.displayMetrics.density



fun Float.pxToDp(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this,
        context.resources.displayMetrics
    )
}