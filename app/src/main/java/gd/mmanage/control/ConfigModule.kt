package gd.mmanage.control

import android.content.Context
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.model.CodeModel

/**
 * Created by Administrator on 2017/11/11.
 */

class ConfigModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 修改密码操作
     * @param old_pwd 旧密码
     * @param new_pwd 新密码
     * */
    fun update_pwd(old_pwd: String, new_pwd: String) {
        HttpUtils<String>().post(url.normal + "Other/GetAndroidVersion", command.login + 1, this)
    }

}
