package gd.mmanage.ui.vehicle

import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityVehicleDetailBinding
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.VehicleModel
import gd.mmanage.control.CarManageModule
import gd.mmanage.model.DetailModel
import kotlinx.android.synthetic.main.activity_vehicle_detail.*

/**
 * 车辆承接详细信息
 * @author 杰
 * */
class VehicleDetailActivity : BaseActivity<ActivityVehicleDetailBinding>(), AbsModule.OnCallback {
    var control: CarManageModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(CarManageModule::class.java, this)
        title_bar.setLeftClick { finish() }
        control!!.get_vehicleById(intent.getStringExtra("id"))
    }

    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<JsonElement>
                binding.model = Gson().fromJson<DetailModel>(success.obj, DetailModel::class.java)
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_vehicle_detail
    }
}