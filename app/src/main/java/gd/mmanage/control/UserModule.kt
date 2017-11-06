package gd.mmanage.control

import android.content.Context
import gd.mmanage.base.BaseModule

/**
 * Created by Finder丶畅畅 on 2017/11/4 23:01
 * QQ群481606175
 */
class UserModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 加载当前用户信息
     * */
    fun get_user_info() {
        var str: List<String>? = null
        callback(77, str)
    }
}