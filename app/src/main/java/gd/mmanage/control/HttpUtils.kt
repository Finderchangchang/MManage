package gd.mmanage.control

import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.internal.LinkedTreeMap
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import gd.mmanage.base.BaseModule
import gd.mmanage.callback.LzyResponse
import gd.mmanage.config.command
import gd.mmanage.model.CodeModel
import gd.mmanage.model.NormalRequest
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import java.lang.Exception

/**
 * Created by Administrator on 2017/11/16.
 */
class HttpUtils<T> {

    fun post(url: String, back_id: Int, control: BaseModule) {
        post(url, back_id, null, control)
    }

    /**
     * @param url 网络访问路径
     * @param model 需要解析的model
     * @param back_id 请求码
     * @param list post过去的参数
     * */
    fun post(url: String, back_id: Int, map: HashMap<String, String>?, control: BaseModule) {
        var go = OkGo.post(url)
        if (map != null) {
            for (model in map) {
                go.params(model.key, model.value)
            }
        }
        go.execute(object : StringCallback() {
            override fun onSuccess(str: String, call: okhttp3.Call?, response: okhttp3.Response?) {
                var t = Gson().fromJson(str, LzyResponse::class.java)
                if (t.Success) {
                    try {
                        var em = JsonParser().parse(str).asJsonObject.get("Data")
                        control.callback(back_id, NormalRequest(0, t.Message, em))
                    } catch (e: Exception) {
                        control.callback(back_id, NormalRequest(1, t.Message, t.Data))
                    }
                } else {
                    control.callback(back_id, NormalRequest(1, t.Message, t.Data))
                }

            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                control.callback(back_id, NormalRequest<T>(2, "未知错误：" + e.toString(), null))
            }
        })
    }

    fun get(url: String, back_id: Int, control: BaseModule) {
        get(url, back_id, null, control)
    }

    /**
     * @param url 网络访问路径
     * @param model 需要解析的model
     * @param back_id 请求码
     * @param list post过去的参数
     * */
    fun get(url: String, back_id: Int, map: HashMap<String, String>?, control: BaseModule) {
        var go = OkGo.get(url)
        if (map != null) {
            for (model in map) {
                go.params(model.key, model.value)
            }
        }
        go.execute(object : StringCallback() {
            override fun onSuccess(str: String, call: okhttp3.Call?, response: okhttp3.Response?) {
                var t = Gson().fromJson(str, LzyResponse::class.java)
                if (t.Success) {
                    try {
                        //t.Data as LinkedTreeMap<String, String>
                        var em = JsonParser().parse(str).asJsonObject.get("Data")
                        control.callback(back_id, NormalRequest(0, t.Message, em))
                    } catch (e: Exception) {
                        control.callback(back_id, NormalRequest(1, t.Message, t.Data))
                    }
                } else {
                    control.callback(back_id, NormalRequest(1, t.Message, t.Data))
                }

            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                control.callback(back_id, NormalRequest<T>(2, "未知错误：" + e.toString(), null))
            }
        })
    }
}