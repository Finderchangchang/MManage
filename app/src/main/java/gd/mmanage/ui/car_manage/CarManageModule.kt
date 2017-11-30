package gd.mmanage.ui.car_manage

import android.content.Context
import android.text.TextUtils
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.control.HttpUtils
import gd.mmanage.method.UtilControl
import gd.mmanage.model.VehicleModel

/**
 * Created by Administrator on 2017/11/14.
 */
class CarManageModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 添加配件信息
     * @param model配件信息集合
     * */
    fun add_prat(model: VehicleModel) {
        var u = "AddVehicleParts"
        if (!TextUtils.isEmpty(model.VehicleId)) u = "UpdateVehicleParts"
        HttpUtils<VehicleModel>().post(url.get_vehicle + u, command.car_manage + 1, UtilControl.change(model), this)
    }

    /**
     * 根据id获得配件详情
     * @param id 配件id
     * */
    fun get_vehicleById(id: String) {
        HttpUtils<VehicleModel>().post(url.get_vehicle + "GetVehicle?vehicleId=" + id, command.car_manage + 2, null, this)
    }

    /**
     * 根据条件查询车辆承接信息集合
     * */
    fun get_vehicles(map: HashMap<String, String>) {
        HttpUtils<List<VehicleModel>>().post(url.get_vehicle + "SearchVehicle", command.car_manage + 3, map, this)
    }
}