package gd.mmanage.control

import android.content.Context
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.model.EmployeeModel

/**
 * 从业人员管理
 * Created by Finder丶畅畅 on 2017/11/4 23:01
 * QQ群481606175
 */
class EmployeeModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 添加，修改从业人员信息
     * @param map 从业人员信息
     * */
    fun add_employee(map: HashMap<String, String>) {
        HttpUtils<String>().post(url.employee, command.employee + 1, map, this)
    }

    /**
     * 删除指定的从业人员信息
     * @param id 从业人d
     * */
    fun del_employee(id: String) {
        var map = HashMap<String, String>()
        map.put("e_id", id)
        HttpUtils<String>().post(url.employee, command.employee + 1, map, this)
    }

    /**
     * 根据指定的条件获得从业人员信息
     * */
    fun get_employees(map: HashMap<String, String>) {
        HttpUtils<String>().post(url.employee, command.employee + 2, map, this)
    }
}