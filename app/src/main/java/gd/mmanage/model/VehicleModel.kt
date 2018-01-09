package gd.mmanage.model

import gd.mmanage.config.sp
import gd.mmanage.method.Utils
import java.io.Serializable

/**
 * 车辆承接
 * Created by Administrator on 2017/11/30.
 */

class VehicleModel : Serializable {

    /**
     * VehicleReceiveTime : 2017-11-29
     * VehicleId : C02130602000120171129006
     * EnterpriseId : C021306020001
     * VehicleOwner : 车主
     * VehicleType : 01
     * VehicleBrand : 大众
     * VehicleColor : 黑色
     * VehicleNumber : 冀F45678
     * VehicleEngine : 465431321654
     * VehicleFrameNumber : 6546453213
     * VehiclePerson : 送车人
     * VehiclePersonCertType : 01
     * VehiclePersonCertNumber : 130624198709183414
     * VehiclePersonAddress : 送车人地址
     * VehiclePersonNation : 01
     * VehiclePersonPhone : 13825256655
     * VehiclePersonCompare : 0.62
     * VehicleReceivePerson : 收车人
     * VehicleReceiveUser : demo
     * VehicleTakeState : 01
     * VehicleTakePerson : null
     * VehicleTakePersonNation : null
     * VehicleTakePersonCertType : null
     * VehicleTakePersonCertNumber : null
     * VehicleTakePersonAddress : null
     * VehicleTakePersonCompare : 0
     * VehicleTakeTime : null
     * VehicleComment : null
     */

    var VehicleReceiveTime = ""
    var VehicleId = ""
    var EnterpriseId = Utils.getCache(sp.company_id)
    var VehicleOwner = ""
    var VehicleType = ""
    var VehicleBrand = ""
    var VehicleColor = ""
    var VehicleNumber = ""
    var VehicleEngine = ""
    var VehicleFrameNumber = ""
    var VehiclePerson = ""
    var VehiclePersonCertType = ""
    var VehiclePersonCertNumber = ""
    var VehiclePersonAddress = ""
    var VehiclePersonNation = ""
    var VehiclePersonPhone = ""
    var VehiclePersonCompare = ""
    var VehicleReceivePerson = ""
    var VehicleReceiveUser = ""
    var VehicleTakeState = ""
    var VehicleTakePerson = ""
    var VehicleTakePersonNation = ""
    var VehicleTakePersonCertType = ""
    var VehicleTakePersonCertNumber = ""
    var VehicleTakePersonAddress = ""
    var VehicleTakePersonCompare = ""
    var VehicleTakeTime = ""
    var VehicleComment = Utils.version
    var CreateTimeBegin = ""
    var CreateTimeEnd = ""
    var files: List<FileModel>? = null
    var RepairCount = ""//维修记录数量
    var SuspiciousCount = ""//可疑记录
}
