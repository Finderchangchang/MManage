package gd.mmanage.control

import android.content.Context
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url

/**
 * Created by Finder丶畅畅 on 2017/11/4 21:25
 * QQ群481606175
 */
class LoginModule(context: Context?) : BaseModule(context) {

    fun user_login(name: String, password: String) {
        var map = HashMap<String, String>()
        map.put("name", name)
        map.put("password", password)
        //HttpUtils<String>().post(url.login, command.login, map, this)
    }

    /**
     * 检查更新
     * */
    fun check_version() {
        //HttpUtils<String>().post(url.check_version, command.login + 1, this)
    }
}