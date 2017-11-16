package gd.mmanage.ui.parts

import android.content.Context
import gd.mmanage.base.BaseModule
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PartsModel

/**
 * 配件管理
 * Created by Administrator on 2017/11/14.
 */
class PratsModule : BaseModule {
    constructor(context: Context?) : super(context)

    /**
     * 添加配件信息
     * @param model配件信息集合
     * */
    fun add_prat(model: PartsModel) {
        callback(11000, NormalRequest<String>(true, "添加成功"))
    }

    /**
     * 根据条件查询配件信息集合
     * */
    fun search_parts(page_index: Int, choice: String) {
        callback(11001, NormalRequest<String>(true, "查询成功"))
    }
}