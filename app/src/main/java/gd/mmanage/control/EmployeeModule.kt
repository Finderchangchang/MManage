package gd.mmanage.control

import android.content.Context
import gd.mmanage.base.BaseModule
import gd.mmanage.model.EmployeeModel

/**
 * 从业人员管理
 * Created by Finder丶畅畅 on 2017/11/4 23:01
 * QQ群481606175
 */
class EmployeeModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 加载当前用户信息
     * */
    fun get_user_info() {
        var str: List<String>? = null
        callback(77, str)
    }

    /**
     * 添加，修改从业人员信息
     * @param model 从业人员信息
     * */
    fun add_employee(model: EmployeeModel) {

    }

    /**
     * 删除指定的从业人员信息
     * @param id 从业人d
     * */
    fun del_employee(id: String) {

    }

    /**
     * 根据指定的条件获得从业人员信息
     * */
    fun get_employees() {

    }
}