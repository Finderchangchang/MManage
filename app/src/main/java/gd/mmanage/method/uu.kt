package gd.mmanage.method

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.ListView
import gd.mmanage.base.BaseApplication
import java.io.*
import java.lang.Exception
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Administrator on 2017/12/6.
 */
object uu {


    /**
     * 设置TotalListView(自定义)的高度

     * @param listView
     */
    fun setListViewHeight(listView: ListView) {
        // 获取ListView对应的Adapter
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0..listAdapter.count - 1) { // listAdapter.getCount()返回数据项的数目
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0) // 计算子项View 的宽高
            totalHeight += listItem.measuredHeight // 统计所有子项的总高度
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.layoutParams = params
    }

    interface setSure {
        fun click(view: View?)
    }

    interface setCancle {
        fun click(view: View?)
    }

    fun isMobileNo(mobiles: String): Boolean {
        val p = Pattern.compile("[1][34578]\\d{9}")
        val m = p.matcher(mobiles)
        return m.matches()
    }


    fun putCache(key: String, `val`: String?) {
        val sp = BaseApplication.context!!.getSharedPreferences("grclass", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, `val`)
        editor.commit()
    }

    fun putBooleanCache(key: String, `val`: Boolean) {
        val sp = BaseApplication.context!!.getSharedPreferences("grclass", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(key, `val`)
        editor.commit()
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态

     * @return true 表示网络可用
     */
    // 当前网络是连接的
    // 当前所连接的网络可用
    val isNetworkAvailable: Boolean
        get() {
            val connectivity = BaseApplication.context!!
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.activeNetworkInfo
                if (info != null && info.isConnected) {
                    if (info.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
            return false
        }

    val isWifiEnabled: Boolean
        get() {
            val mgrConn = BaseApplication.context!!
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mgrTel = BaseApplication.context!!
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return mgrConn.activeNetworkInfo != null && mgrConn
                    .activeNetworkInfo.state == NetworkInfo.State.CONNECTED || mgrTel
                    .networkType == TelephonyManager.NETWORK_TYPE_UMTS
        }

    fun getCache(key: String): String {
        val sharedPreferences = BaseApplication.context!!.getSharedPreferences("grclass", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

    fun getBooleanCache(key: String): Boolean {
        val sharedPreferences = BaseApplication.context!!.getSharedPreferences("grclass", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }


    /*
    * 获取当前程序的版本号
    */
    //获取packagemanager的实例
    //getPackageName()是你当前类的包名，0代表是获取版本信息
    val version: String
        get() {
            val packageManager = BaseApplication.context!!.packageManager
            var packInfo: PackageInfo? = null
            try {
                packInfo = packageManager.getPackageInfo(BaseApplication.context!!.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {

            }

            return packInfo!!.versionName
        }

    /**
     * 加载当前时间。
     * 1.同一年的显示格式 05-11  07:45
     * 2.前一年或者更多格式 2015-11-12

     * @param old
     * *
     * @return 需要显示的处理结果
     */
    fun loadTime(old: String): String {
        val old_year = old.substring(0, 4)//获得old里面的年
        val now_year = SimpleDateFormat("yyyy").format(Date()).substring(0, 4)//获得当前的年
        if (old_year == now_year) {//两者为同一年
            return old.substring(5, 16)
        } else {
            return old.substring(0, 10)
        }
    }

    /**
     * 检查网络连接状态

     * @return 连接成功
     */
    val isNetworkConnected: Boolean
        get() {
            val mConnectivityManager = BaseApplication.context!!
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
            return false
        }

    fun compressImage(image: Bitmap): Bitmap {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90
        while (baos.toByteArray().size / 1024 > 1500) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()//重置baos即清空baos
            if (options < 0) {
                options = 1
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10//每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())//把压缩后的数据baos存放到ByteArrayInputStream中
        val bitmap = BitmapFactory.decodeStream(isBm, null, null)//把ByteArrayInputStream数据生成图片
        return bitmap
    }

    fun getimage(contxt: Int, srcPath: String): Bitmap {
        val newOpts = BitmapFactory.Options()
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)//此时返回bm为空
        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        //Bitmap header = BitmapFactory.decodeResource(contxt.getResources(), R.mipmap.person_header);

        val hh = 1440f//这里设置高度为800f
        val ww = 900f//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)

        return compressImagexin(bitmap, contxt)//压缩好比例大小后再进行质量压缩
    }

    fun compressImagexin(image: Bitmap, context: Int): Bitmap {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 50
        while (baos.toByteArray().size / 1024 > context) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10//每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())//把压缩后的数据baos存放到ByteArrayInputStream中
        val bitmap = BitmapFactory.decodeStream(isBm, null, null)//把ByteArrayInputStream数据生成图片
        return bitmap
    }

    //得到旋转的角度
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    //旋转图片
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

    /**
     *                                                                       
     *    * @param bitmap      原图
     *    * @param edgeLength  希望得到的正方形部分的边长
     *    * @return  缩放截取正中部分后的位图。
     *    
     */

    fun centerSquareScaleBitmap(bitmap: Bitmap?, edgeLength: Int): Bitmap? {
        if (null == bitmap || edgeLength <= 0) {
            return null
        }
        var result: Bitmap = bitmap
        val widthOrg = bitmap.width
        val heightOrg = bitmap.height
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            val longerEdge = (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg)).toInt()
            val scaledWidth = if (widthOrg > heightOrg) longerEdge else edgeLength
            val scaledHeight = if (widthOrg > heightOrg) edgeLength else longerEdge
            val scaledBitmap: Bitmap
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
            } catch (e: Exception) {
                return null
            }

            //从图中截取正中间的正方形部分。
            val xTopLeft = (scaledWidth - edgeLength) / 2
            val yTopLeft = (scaledHeight - edgeLength) / 2
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength)
                scaledBitmap.recycle()
            } catch (e: Exception) {
                return null
            }

        }
        return result
    }


    /**
     * 获取当前应用的版本号：
     */
    // 获取packagemanager的实例
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val versionName: String
        get() {
            val Version = "[Version:num]-[Registe:Mobile]"
            val packageManager = BaseApplication.context!!.packageManager
            val packInfo: PackageInfo
            try {
                packInfo = packageManager.getPackageInfo(BaseApplication.context!!.packageName, 0)
                val version = packInfo.versionName
                return Version.replace("num", version)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return Version.replace("num", "1.0")
        }

    /**
     * 获得当前系统时间

     * @return String类型的当前时间
     */
    //设置日期格式
    val normalTime: String
        get() {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return df.format(Date())
        }

    /**
     * 获得当前时间 yyyy/MM/dd HH:mm:ss

     * @return String类型的当前时间
     */
    //设置日期格式
    val now: String
        get() {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return df.format(Date())
        }

    //将20160302210101转换为yyyy-MM-dd HH:mm:ss
    fun DataTimeTO(time: String): String {
        val df = SimpleDateFormat("yyyyMMddHHmmss")
        val dfstr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//设置日期格式

        var date: Date? = null
        try {
            date = df.parse(time)
            return dfstr.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 读取xml文件

     * @param FileName 文件名
     * *
     * @return 文件内容
     */
    fun getAssetsFileData(FileName: String): String {
        var str = ""
        try {
            val `is` = BaseApplication.context!!.assets.open(FileName)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            str = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return str
    }

    //base64 string转换为bitmap

    /**
     * 获得屏幕高度宽度

     * @return Point对象 point.x宽度。point.y高度
     */
    val scannerPoint: Point
        get() {
            val windowManager = BaseApplication.context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            return point
        }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getViewPoint(view: View): Point {
        val point = Point()
        view.display.getSize(point)
        return point
    }

    //获取当前时间的hhmmssfff
    //yyyymmddhhmmssfff
    val qingqiuma: String
        get() {
            val ts = Timestamp(System.currentTimeMillis())
            println(ts.toString())
            var str = ts.toString().replace(":", "").replace(".", "").replace("-", "").replace(" ", "")
            if (str.length < 16) {
                str = str.substring(0)
            } else if (str.length < 17) {
                str = str.substring(1)
            } else {
                str = str.substring(2)
            }
            return str
        }

    /**
     * 比较时间的大小str1小返回true

     * @param str1   起始时间
     * *
     * @param str2   结束时间
     * *
     * @param islong true,长时间串
     * *
     * @return
     */
    fun DateCompare(str1: String, str2: String, islong: Boolean): Boolean {
        val df: java.text.DateFormat
        if (islong) {
            df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        } else {
            df = SimpleDateFormat("yyyy-MM-dd")
        }
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        try {
            c1.time = df.parse(str1)
            c2.time = df.parse(str2)
        } catch (e: ParseException) {
            System.err.println("格式不正确")
            return false
        }

        val result = c1.compareTo(c2)
        if (result == 0) {
            //System.out.println("c1相等c2");
            return true
        } else if (result >= 0) {
            return false
            //System.out.println("c1小于c2");
        } else {
            // System.out.println("c1大于c2");
            return true
        }
    }


    fun getAssetsFileData(context: Context, FileName: String): String {
        var str = ""
        try {
            val `is` = context.assets.open(FileName)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            str = String(buffer)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return str
    }


    /**
     * 判断字符串是否为空,为空返回空串
     * http://bbs.3gstdy.com

     * @param text
     * *
     * @return
     */
    fun URLEncode(text: String): String {
        if (isEmptyString(text))
            return ""
        if (text == "null")
            return ""
        return text
    }

    /**
     * 判断字符串是否为空
     * http://bbs.3gstdy.com

     * @param str
     * *
     * @return
     */
    fun isEmptyString(str: String?): Boolean {
        return str == null || str.length == 0
    }

    /**
     * 将图片bitmap转换为base64字符串
     * http://bbs.3gstdy.com

     * @param bitmap
     * *
     * @return 根据url读取出的图片的Bitmap信息
     */
    fun encodeBitmap(bitmap: Bitmap): String {
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
                    .trim { it <= ' ' }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    val allChar = "0123456789"


    /**
     * 获取系统的当前日期，格式为YYYYMMDD
     */
    val systemNowDate: String
        get() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val monthOfYear = calendar.get(Calendar.MONTH) + 1
            var monthStr = monthOfYear.toString()
            if (monthStr.length < 2) {
                monthStr = "0" + monthStr
            }
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            var dayStr = dayOfMonth.toString()
            if (dayStr.length < 2) {
                dayStr = "0" + dayStr
            }
            return year.toString() + monthStr + dayStr
        }

    /**
     * 带参数的跳页

     * @param cla      需要跳转到的页面
     * *
     * @param listener 传参的接口
     */
    @JvmOverloads
    fun IntentPost(cla: Class<*>, listener: putListener? = null) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        intent.setClass(BaseApplication.context, cla)
        listener?.put(intent)
        BaseApplication.context!!.startActivity(intent)
    }


    /**
     * 获得key在val中存在的个数

     * @param val 字符串
     * *
     * @param key 包含在key中的某字符
     * *
     * @return 存在的个数
     */
    fun getContainSize(`val`: String, key: String): Int {
        if (`val`.contains(key)) {
            val length = `val`.length - `val`.replace(key, "").length
            if (length > 0) {
                return length
            }
        }
        return 0
    }

    /**
     * 加载本地图片
     * http://bbs.3gstdy.com

     * @param url
     * *
     * @return 根据url读取出的图片的Bitmap信息
     */
    fun getBitmapByFile(url: String?): Bitmap? {
        if (url !== "" && url != null) {
            try {
                val fis = FileInputStream(url)
                return BitmapFactory.decodeStream(fis)
            } catch (e: FileNotFoundException) {
                return null
            }

        } else {
            return null
        }
    }

    /**
     * @param bitmap     原图
     * *
     * @param edgeLength 希望得到的正方形部分的边长
     * *
     * @return 缩放截取正中部分后的位图。
     */
    fun centerImageBitmap(bitmap: Bitmap?, edgeLength: Int): Bitmap? {
        if (null == bitmap || edgeLength <= 0) {
            return null
        }
        var result: Bitmap = bitmap
        val widthOrg = bitmap.width
        val heightOrg = bitmap.height
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            val longerEdge = (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg)).toInt()
            val scaledWidth = if (widthOrg > heightOrg) longerEdge else edgeLength
            val scaledHeight = if (widthOrg > heightOrg) edgeLength else longerEdge
            val scaledBitmap: Bitmap
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
            } catch (e: Exception) {
                return null
            }

            //从图中截取正中间的正方形部分。
            val xTopLeft = (scaledWidth - edgeLength) / 2
            val yTopLeft = (scaledHeight - edgeLength) / 2
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength)
                scaledBitmap.recycle()
            } catch (e: Exception) {
                return null
            }

        }
        return result
    }

    /**
     * 截取指定字符串并添加并在后面添加...

     * @param val    截取前的字符串
     * *
     * @param length 截取字符长度
     * *
     * @return 处理之后的结果
     */
    fun cutStringToDian(`val`: String, length: Int): String {
        if (`val`.length >= length) {
            return `val`.substring(0, length) + "..."
        } else {
            return `val`
        }
    }


    /**
     * 跳页传参的接口
     */
    interface putListener {
        fun put(intent: Intent)
    }
}