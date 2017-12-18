package gd.mmanage.control

import android.content.Context
import android.text.TextUtils
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.config.url
import gd.mmanage.method.Utils
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
        var cmd = command.employee
        //空是添加，不空为更新
        var normal_url = if (TextUtils.isEmpty(map["employeeid"])) {
            url.get_employee + "AddEmployee"
        } else {
            cmd = command.employee + 1
            url.get_employee + "UpdateEmployee"
        }
        HttpUtils<String>().post(normal_url, cmd, map, this)
    }

    /**
     * 删除指定的从业人员信息
     * @param id 从业人d
     * */
    fun get_employee(id: String) {
        HttpUtils<String>().get(url.get_employee + "GetEmployee?employeeId=" + id, command.employee + 2, HashMap(), this)
    }

    /**
     * 根据指定的条件获得从业人员信息
     * */
    fun get_employees(map: HashMap<String, String>) {
        HttpUtils<List<EmployeeModel>>().post(url.get_employee + "SearchEmployee", command.employee + 3, map, this)
    }

    /**
     * 获得通知通告
     * */
    fun get_notice(map: HashMap<String, String>) {
        HttpUtils<List<EmployeeModel>>().post(url.normal + "News/SearchNews", command.employee + 4, map, this)
    }

    /**
     * 获得首页数量
     * */
    fun get_num() {
        HttpUtils<List<EmployeeModel>>().get(url.normal + "Vehicle/GetVehicleStatistics?enterpriseId="+Utils.getCache(sp.company_id), command.employee + 5, this)
    }
}