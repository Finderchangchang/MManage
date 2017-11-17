package gd.mmanage.control

import com.arialyy.frame.module.AbsModule
import com.lzy.okgo.OkGo
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
        for (model in map!!) {
            go.params(model.key, model.value)
        }
        go.execute(object : JsonCallback<LzyResponse<T>>() {
            override fun onSuccess(t: LzyResponse<T>, call: okhttp3.Call?, response: okhttp3.Response?) {
                if (t.Success) {
                    control.callback(back_id, NormalRequest<T>(0, t.Message, t.Data))
                } else {
                    control.callback(back_id, NormalRequest<T>(1, t.Message, t.Data))
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
        for (model in map!!) {
            go.params(model.key, model.value)
        }
        go.execute(object : JsonCallback<LzyResponse<T>>() {
            override fun onSuccess(t: LzyResponse<T>, call: okhttp3.Call?, response: okhttp3.Response?) {
                if (t.Success) {
                    control.callback(back_id, NormalRequest<T>(0, t.Message, t.Data))
                } else {
                    control.callback(back_id, NormalRequest<T>(1, t.Message, t.Data))
                }
            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                control.callback(back_id, NormalRequest<T>(2, "未知错误：" + e.toString(), null))
            }
        })
    }
}