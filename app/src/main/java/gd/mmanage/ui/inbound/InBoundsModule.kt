package gd.mmanage.ui.inbound

import android.content.Context
import android.text.TextUtils
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.control.HttpUtils
import gd.mmanage.method.UtilControl
import gd.mmanage.model.PartsModel

/**
 * 配件管理
 * Created by Administrator on 2017/11/14.
 */
class InBoundsModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 添加配件信息
     * @param model配件信息集合
     * */
    fun add_prat(model: PartsModel) {
        var u = "AddVehicleParts"
        if (!TextUtils.isEmpty(model.PartsId)) u = "UpdateVehicleParts"
        HttpUtils<PartsModel>().post(url.get_storage + u, command.parts + 1, UtilControl.change(model), this)
    }

    /**
     * 根据id获得配件详情
     * @param id 配件id
     * */
    fun get_partById(id: String) {
        HttpUtils<PartsModel>().post(url.get_storage + "GetVehicleParts?vehiclePartsId=" + id, command.parts + 2, null, this)
    }

    /**
     * 根据条件查询入库信息集合
     * */
    fun get_in_bounds(map: HashMap<String, String>) {
        HttpUtils<List<PartsModel>>().post(url.get_storage + "SearchStorage", command.parts + 3, map, this)
    }
}