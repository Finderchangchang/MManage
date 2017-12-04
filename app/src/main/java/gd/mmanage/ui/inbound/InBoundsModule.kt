package gd.mmanage.ui.inbound

import android.content.Context
import android.text.TextUtils
import gd.mmanage.base.BaseModule
import gd.mmanage.config.command
import gd.mmanage.config.url
import gd.mmanage.control.HttpUtils
import gd.mmanage.method.UtilControl
import gd.mmanage.model.InBoundModel
import gd.mmanage.model.PartsModel
import gd.mmanage.model.StorageModel

/**
 * 配件管理
 * Created by Administrator on 2017/11/14.
 */
class InBoundsModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 添加入库信息
     * @param model入库信息集合
     * */
    fun add_prat(model: StorageModel) {
        HttpUtils<StorageModel>().post(url.get_storage + "AddStorage", command.parts, UtilControl.change(model), this)
    }

    /**
     * 根据条件查询入库信息集合
     * */
    fun get_in_bounds(map: HashMap<String, String>) {
        HttpUtils<List<StorageModel>>().post(url.get_storage + "SearchStorage", command.parts + 1, map, this)
    }
}