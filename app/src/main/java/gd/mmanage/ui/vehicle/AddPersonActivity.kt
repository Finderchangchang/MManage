package gd.mmanage.ui.vehicle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddPersonBinding
import gd.mmanage.model.VehicleModel
import kotlinx.android.synthetic.main.activity_add_person.*

/**
 * 人员添加
 * */
class AddPersonActivity : BaseActivity<ActivityAddPersonBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 5 -> {//根据身份证号获得车的记录
                val builder = AlertDialog.Builder(this)
                builder.setTitle("送车人名下车辆")
                builder.setItems(arrayOf("冀T12345", "冀T45678")) { a, b ->
                    startActivity(Intent(this@AddPersonActivity, AddCarActivity::class.java)
                            .putExtra("model", model))
                }
                builder.show()
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    var model: VehicleModel = VehicleModel()
    var control: CarManageModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(CarManageModule::class.java, this)
        //证件识别操作
        read_card_btn.setOnClickListener {
            //身份证读取
            if (id_card_rb.isChecked) {

            } else {//orc读取驾驶证
                control!!.get_vehicleByIdCard("130624198709183414")
            }
        }
        model.VehiclePerson = "柳伟杰"
        model.VehiclePersonNation = "01"
        model.VehiclePersonCertType = "01"
        model.VehiclePersonCompare = "0.81"
        binding.model = model
        binding.sex = "男"
        binding.nation = "汉族"
    }

    /**
     * 加载模拟数据
     * */
    fun initData() {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_person
    }
}
