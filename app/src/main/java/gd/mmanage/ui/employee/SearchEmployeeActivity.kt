package gd.mmanage.ui.employee

import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding

/**
 * 添加从业人员信息
 * */
class SearchEmployeeActivity : BaseActivity<ActivityAddEmployeeBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_employee
    }
}