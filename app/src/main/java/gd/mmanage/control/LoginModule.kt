package gd.mmanage.control

import android.content.Context
import com.arialyy.frame.module.AbsModule
import gd.mmanage.base.BaseModule

/**
 * Created by Finder丶畅畅 on 2017/11/4 21:25
 * QQ群481606175
 */
class LoginModule : BaseModule {
    constructor(context: Context?) : super(context)

    fun user_login(name: String, password: String) {
        callback(1000,"登录成功")
    }
}