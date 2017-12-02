package gd.mmanage.ui.car_manage

import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddServiceCarBinding

/**
 *取车登记
 * */
class AddGetCarActivity : BaseActivity<ActivityAddServiceCarBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_service_car
    }
}