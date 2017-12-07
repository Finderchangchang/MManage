package gd.mmanage.control

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.control.HttpUtils
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.UtilControl
import gd.mmanage.model.RepairModel
import gd.mmanage.model.StorageModel
import gd.mmanage.model.SuspiciousModel
import gd.mmanage.model.VehicleModel

/**
 * Created by Administrator on 2017/11/14.
 */
class CarManageModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 车辆承接信息
     * @param model承接信息集合
     * */
    fun add_prat(model: VehicleModel) {
        var u = "AddVehicle"
        if (!TextUtils.isEmpty(model.VehicleId)) u = "UpdateVehicle"
        HttpUtils<VehicleModel>().post(url.get_vehicle + u, command.car_manage + 1, UtilControl.change(model), this)
    }

    /**
     * 根据id获得配件详情
     * @param id 配件id
     * */
    fun get_vehicleById(id: String) {
        HttpUtils<VehicleModel>().post(url.get_vehicle + "GetAllVehicle?vehicleId=" + id, command.car_manage + 2, null, this)
    }

    /**
     * 根据条件查询车辆承接信息集合
     * */
    fun get_vehicles(map: HashMap<String, String>) {
        HttpUtils<List<VehicleModel>>().post(url.get_vehicle + "SearchVehicle", command.car_manage + 3, map, this)
    }

    /**
     * 根据车牌号获得车辆历史信息
     * @param id 车牌号
     * */
    fun get_vehicleByCarId(id: String) {
        HttpUtils<VehicleModel>().get(url.get_vehicle + "GetVehicleByVehicleNumber?vehicleNumber=" + id, command.car_manage + 4, null, this)
    }

    /**
     * 根据身份证号码获得车辆历史信息
     * @param id 身份证号
     * */
    fun get_vehicleByIdCard(id: String) {
        HttpUtils<VehicleModel>().get(url.get_vehicle + "GetVehicleByCertNumber?certNumber=" + id, command.car_manage + 5, null, this)
    }

    /**
     * 添加报警信息
     * @param model 报警数据
     * */
    fun save_warn(model: SuspiciousModel) {
        HttpUtils<VehicleModel>().post(url.get_suspicious + "AddSuspicious", command.car_manage + 6, UtilControl.change(model), this)
    }

    /**
     * 添加维修业务
     * @param model 维修数据
     * */
    fun save_repair(model: RepairModel) {
        HttpUtils<VehicleModel>().post(url.get_repair + "AddRepair", command.car_manage + 7, UtilControl.change(model), this)
    }

    /**
     * 添加维修业务
     * @param model 维修数据
     * */
    fun check_two_img(bitmap1: Bitmap, bitmap2: Bitmap) {
        var map = HashMap<String, String>()
        map.put("imageA", ImgUtils().bitmapToBase64(bitmap1))
        map.put("imageB", ImgUtils().bitmapToBase64(bitmap2))
        HttpUtils<VehicleModel>().post(url.normal + "Other/FaceRecognition", command.car_manage + 8, map, this)
    }
}