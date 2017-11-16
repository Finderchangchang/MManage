package gd.mmanage.control

import android.content.Context
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.model.CodeModel
import gd.mmanage.model.NormalRequest

/**
 * Created by Finder丶畅畅 on 2017/11/4 21:25
 * QQ群481606175
 */
class LoginModule(context: Context?) : BaseModule(context) {

    fun user_login(name: String, password: String) {
        var code = "http://192.168.1.115:3334/Api/Enterprise/GetCode?codeName=Code_Region"
        HttpUtils<String>().post(code, command.login, this)
    }

    /**
     * 检查更新
     * */
    fun check_version() {
        var code = "http://192.168.1.115:3334/Api/Enterprise/GetCode?codeName=Code_Region"
        HttpUtils<String>().post(code, command.login + 1, this)
    }

}