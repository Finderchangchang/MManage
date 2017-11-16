package gd.mmanage.ui.parts

import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.model.PartsModel
import kotlinx.android.synthetic.main.activity_add_parts.*

/**
 * 添加配件
 * @author 杰
 * */
class AddPartsActivity : BaseActivity<ActivityAddPartsBinding>(), AbsModule.OnCallback {
    var control: PratsModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(PratsModule::class.java)
        //保存数据操作
        save_btn.setOnClickListener {
            var model = PartsModel()
            control!!.add_prat(model)
        }
    }

    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_parts
    }
}