package gd.mmanage.control

import android.content.Context
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import gd.mmanage.base.BaseModule
import gd.mmanage.callback.LzyResponse
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EquipmentModel
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import java.lang.Exception

/**
 * Created by Finder丶畅畅 on 2017/11/4 21:25
 * QQ群481606175
 */
class LoginModule(context: Context?) : BaseModule(context) {

    fun user_login(name: String, password: String) {
        var map = HashMap<String, String>()
        map.put("name", name)
        map.put("password", password)
        HttpUtils<String>().post(url.login + "AndroidUserLogin", command.login, map, this)
    }

    /**
     * 检查更新
     * */
    fun check_version() {
        HttpUtils<List<CodeModel>>().post(url.get_code + "Code_Region", command.login + 1, this)
    }
}