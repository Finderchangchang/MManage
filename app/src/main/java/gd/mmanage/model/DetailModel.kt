package gd.mmanage.model

/**
 * Created by Administrator on 2017/12/7.
 */
class DetailModel {
    var Vehicle: VehicleModel? = null
    var Repair: RepairModel? = null//修理记录
    var Parts: List<PartsModel>? = null//使用配件记录
    var Suspicious: SuspiciousModel? = null//报警记录
}