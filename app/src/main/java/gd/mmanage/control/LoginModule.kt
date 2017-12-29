package gd.mmanage.control

import android.content.Context
import android.text.TextUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import gd.mmanage.base.BaseModule
import gd.mmanage.callback.LzyResponse
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.config.url
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EquipmentModel
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Finder丶畅畅 on 2017/11/4 21:25
 * QQ群481606175
 */
class LoginModule(context: Context?) : BaseModule(context) {

    fun user_login(name: String, password: String) {
        var time = SimpleDateFormat("yyyyMMddHHmmssaa").format(Date())
        var map = HashMap<String, String>()
        map.put("userId", name)
        map.put("password", password)
        if (!TextUtils.isEmpty(time) && time.length >= 2) {
            map.put("timeStamp", time.substring(0, time.length - 2))
        }
        map.put("imei", Utils.imei)//862387039569262
        map.put("imei", "867628023577976")//张泽的设备

        //map.put("imei", "867140036608871")//862387039569262  864566039449389 35460207860591

//        val genType = javaClass.genericSuperclass
//        val params = (genType as ParameterizedType).actualTypeArguments
//        val type = params[0] as? ParameterizedType ?: throw IllegalStateException("没有填写泛型参数")
//        val rawType = type.rawType
//        val typeArgument = type.actualTypeArguments[0]
        HttpUtils<String>().get(url.login + "AndroidUserLogin", command.login, map, this)
    }

    /**
     * 检查更新
     * */
    fun check_version() {
        HttpUtils<List<CodeModel>>().get(url.normal + "Other/GetAndroidVersion", command.login + 1, this)
    }

}