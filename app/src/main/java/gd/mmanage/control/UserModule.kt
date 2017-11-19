package gd.mmanage.control

import android.content.Context
import com.google.gson.JsonElement
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url

/**
 * Created by Finder丶畅畅 on 2017/11/4 23:01
 * QQ群481606175
 */
class UserModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 加载当前用户信息
     * */
    fun get_user_info(user_id: String) {
        HttpUtils<JsonElement>().post(url.get_enterprise + "GetEnterprise?enterpriseId=" + user_id, command.user, this)
    }
}