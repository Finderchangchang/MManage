package gd.mmanage.ui.car_manage

import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddDubiousCarBinding

/**
 *可疑车辆登记
 * */
class AddDubiousCarActivity : BaseActivity<ActivityAddDubiousCarBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_dubious_car
    }
}