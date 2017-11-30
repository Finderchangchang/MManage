package gd.mmanage.config

/**
 * Created by Administrator on 2017/11/17.
 */
object url {
    var normal = "http://192.168.1.115:3334/Api/"
    var login = normal + "Enterprise/GetCode?codeName=Code_Region"
    var get_code = normal + "Code/GetCode?codeName="
    var get_enterprise = normal + "Enterprise/"
    var get_employee = normal + "Employee/"
    var get_parts = normal + "VehicleParts/"//配件Storage
    var get_storage = normal + "Storage/"//配件Storage
    var get_vehicle = normal + "Vehicle/"//车辆承接
    var check_version = normal + "Enterprise/GetCode?codeName=Code_Region"
}