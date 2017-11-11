package gd.mmanage.ui.parts

import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityAddPartsBinding

/**
 * 添加配件
 * @author 杰
 * */
class AddPartsActivity : BaseActivity<ActivityAddPartsBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_parts
    }
}