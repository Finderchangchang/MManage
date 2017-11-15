package gd.mmanage.ui.car_manage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddCarBinding
import gd.mmanage.databinding.ActivityAddPartsBinding

class AddCarActivity : BaseActivity<ActivityAddCarBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        //身份证阅读器读卡
        var list: ArrayList<String> = ArrayList()
        list.add("奔腾-京Q123456")
        list.add("奔腾-京Q123456")
        list.add("奔腾-京Q123456")

    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_car
    }
}
