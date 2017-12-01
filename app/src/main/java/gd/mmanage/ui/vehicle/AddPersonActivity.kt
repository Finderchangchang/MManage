package gd.mmanage.ui.vehicle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.callback.LzyResponse
import gd.mmanage.config.command
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddPersonBinding
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.VehicleModel
import kotlinx.android.synthetic.main.activity_add_person.*

/**
 * 人员添加
 * */
class AddPersonActivity : BaseActivity<ActivityAddPersonBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 5 -> {//根据身份证号获得车的记录(解析list然后用dialog的形式展示出来)
                success as NormalRequest<JsonArray>
                var array = arrayOfNulls<String>(success.obj!!.size())
                var list = ArrayList<VehicleModel>()
                for (key in 0 until success.obj!!.size()) {
                    var kk = Gson().fromJson(success.obj!![key], VehicleModel::class.java)
                    array[key] = kk.VehicleNumber
                    list.add(kk)
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("送车人名下车辆")
                builder.setItems(array) { a, b ->
                    var now_model = list[b]
                    model.VehicleOwner = now_model.VehicleOwner
                    model.VehicleType = now_model.VehicleType
                    model.VehicleBrand = now_model.VehicleBrand
                    model.VehicleColor = now_model.VehicleColor
                    model.VehicleNumber = now_model.VehicleNumber
                    model.VehicleEngine = now_model.VehicleEngine
                    model.VehicleFrameNumber = now_model.VehicleFrameNumber
                    startActivity(Intent(this@AddPersonActivity, AddCarActivity::class.java)
                            .putExtra("model", model))
                    finish()
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
            }
        }
        model.VehiclePerson = "柳伟杰"
        model.VehiclePersonNation = "01"
        model.VehiclePersonCertType = "01"
        model.VehiclePersonCompare = "0.81"
        binding.model = model
        binding.sex = "男"
        binding.nation = "汉族"
        //跳转到拍照页面
        real_user_iv.setOnClickListener {
            control!!.get_vehicleByIdCard("130624198709183414")
        }
        next_btn.setOnClickListener {
            startActivity(Intent(this@AddPersonActivity, AddCarActivity::class.java)
                    .putExtra("model", model))
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //拍照成功将图片显示出来
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_person
    }
}
