package gd.mmanage.ui.parts

import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.databinding.ActivitySearchPartsBinding

/**
 * 添加从业人员信息
 * */
class SearchPartsActivity : BaseActivity<ActivitySearchPartsBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search_parts
    }
}