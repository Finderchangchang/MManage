package gd.mmanage.method

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created by Administrator on 2017/12/6.
 */
class ImgUtils {
    fun base64ToBitmap(base64Data: String): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun bitmapToBase64(bitmap: Bitmap?): String {
        return bitmapToBase64(bitmap, 90)
    }

    fun Only_bitmapToBase64(bitmap: Bitmap?): String {
        return bitmapToBase64(bitmap, 0)
    }

    /***
     * bitmap转成Base64
     * @param jd 旋转的角度
     */
    fun bitmapToBase64(bitmap: Bitmap?, jd: Int): String {
        var bitmap = bitmap
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                bitmap = rotaingImageView(jd, bitmap)
                baos = ByteArrayOutputStream()
                var options = 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
                while (baos.toByteArray().size / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    Log.i("lgg", baos.toByteArray().size.toString())
                    baos.reset()//重置baos即清空baos
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
                    options -= 10//每次都减少10
                }
                baos.flush()
                baos.close()

                val bitmapBytes = baos.toByteArray()
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return result!!
    }

    /***
     * 如果图片不正，进行旋转
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        println("angle2=" + angle)
        // 创建新的图片
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true)
        return resizedBitmap
    }


}