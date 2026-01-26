package com.yyl.puzzleverify.utils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Base64
object ImageUtils {



    /**
     * 图片文件转 Base64
     * @param file 图片文件
     * @param withPrefix 是否包含前缀（如 data:image/jpeg;base64,）
     * @return Base64 字符串，转换失败返回 null
     */
    fun fileToBase64(file: File, withPrefix: Boolean = false): String? {
        if (!file.exists() || !file.isFile) {
            println("文件不存在或不是有效文件：${file.absolutePath}")
            return null
        }

        return try {
            FileInputStream(file).use { inputStream ->
                val bytes = inputStream.readBytes()
                bytesToBase64(bytes, getImageMimeType(file), withPrefix)
            }
        } catch (e: Exception) {
            println("文件转 Base64 失败：${e.message}")
            null
        }
    }

    /**
     * Bitmap 转 Base64
     * @param bitmap 位图对象
     * @param quality 压缩质量 (0-100)
     * @param format 图片格式（JPEG/PNG/WBMP）
     * @param withPrefix 是否包含前缀
     * @return Base64 字符串，转换失败返回 null
     */
    fun bitmapToBase64(
        bitmap: Bitmap,
        quality: Int = 100,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        withPrefix: Boolean = false
    ): String? {
        return try {
            ByteArrayOutputStream().use { outputStream ->
                bitmap.compress(format, quality, outputStream)
                val bytes = outputStream.toByteArray()
                val mimeType = when (format) {
                    Bitmap.CompressFormat.JPEG -> "image/jpeg"
                    Bitmap.CompressFormat.PNG -> "image/png"
                    Bitmap.CompressFormat.WEBP -> "image/webp"
                    else -> "image/jpeg"
                }
                bytesToBase64(bytes, mimeType, withPrefix)
            }
        } catch (e: Exception) {
            println("Bitmap 转 Base64 失败：${e.message}")
            null
        }
    }

    /**
     * 输入流转 Base64
     * @param inputStream 图片输入流
     * @param mimeType 图片类型（如 image/jpeg）
     * @param withPrefix 是否包含前缀
     * @return Base64 字符串，转换失败返回 null
     */
    fun inputStreamToBase64(
        inputStream: InputStream,
        mimeType: String = "image/jpeg",
        withPrefix: Boolean = false
    ): String? {
        return try {
            val bytes = inputStream.readBytes()
            bytesToBase64(bytes, mimeType, withPrefix)
        } catch (e: Exception) {
            println("输入流转 Base64 失败：${e.message}")
            null
        }
    }

    /**
     * 字节数组转 Base64
     * @param bytes 图片字节数组
     * @param mimeType 图片类型
     * @param withPrefix 是否包含前缀
     * @return Base64 字符串，转换失败返回 null
     */
    fun bytesToBase64(
        bytes: ByteArray,
        mimeType: String = "image/jpeg",
        withPrefix: Boolean = false
    ): String? {
        return if (bytes.isEmpty()) {
            println("图片字节数组为空")
            null
        } else {
            val base64Str = Base64.getEncoder().encodeToString(bytes)
            if (withPrefix) "data:$mimeType;base64,$base64Str" else base64Str
        }
    }

    /**
     * 获取文件的 MIME 类型
     * @param file 图片文件
     * @return 对应的 MIME 类型，默认 image/jpeg
     */
    private fun getImageMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            else -> "image/jpeg"
        }
    }

    // 扩展方法：简化调用（可选）
    fun File.toBase64(withPrefix: Boolean = false): String? = fileToBase64(this, withPrefix)
    fun Bitmap.toBase64(
        quality: Int = 100,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        withPrefix: Boolean = false
    ): String? = bitmapToBase64(this, quality, format, withPrefix)
}




