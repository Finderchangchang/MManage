package gd.mmanage.control

import android.content.Context
import com.google.gson.JsonElement
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.config.url
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel

/**
 * Created by Finder丶畅畅 on 2017/11/4 23:01
 * QQ群481606175
 */
class UserModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 加载当前企业信息
     * */
    fun get_company_info(user_id: String) {
        HttpUtils<JsonElement>().post(url.get_enterprise + "GetEnterprise?enterpriseId=" + user_id, command.user, this)
    }

    /**
     * 加载当前用户信息
     * */
    fun get_user_info() {
        HttpUtils<JsonElement>().post(url.login + "GetUser?userId=" + Utils.getCache(sp.user_id), command.user + 1, this)
    }


    /**
     * 获得当前设备是否可用
     * @param id 设备
     * */
    fun get_equipments(id: String) {
        HttpUtils<List<CodeModel>>().post(url.normal + "/Equipment/GetEquipments?enterpriseId=" + id, command.user + 2, this)
    }
}