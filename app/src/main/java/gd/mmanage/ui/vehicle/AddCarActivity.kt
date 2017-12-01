package gd.mmanage.ui.vehicle

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonArray
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddCarBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.model.FileModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.VehicleModel
import kotlinx.android.synthetic.main.activity_add_car.*
import java.io.ByteArrayOutputStream

/**
 * 添加车辆信息
 * */
class AddCarActivity : BaseActivity<ActivityAddCarBinding>(), AbsModule.OnCallback {
    var model: VehicleModel? = null
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
                cars_tv.text = list.size.toString() + "辆"
                builder = AlertDialog.Builder(this)
                builder.setTitle("送车人名下车辆")
                builder.setItems(array) { a, b ->
                    var now_model = list[b]
                    model!!.VehicleOwner = now_model.VehicleOwner
                    model!!.VehicleType = now_model.VehicleType
                    model!!.VehicleBrand = now_model.VehicleBrand
                    model!!.VehicleColor = now_model.VehicleColor
                    model!!.VehicleNumber = now_model.VehicleNumber
                    model!!.VehicleEngine = now_model.VehicleEngine
                    model!!.VehicleFrameNumber = now_model.VehicleFrameNumber
                    binding.model = model//刷新一下数据
                }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    var control: CarManageModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(CarManageModule::class.java, this)
        model = intent.getSerializableExtra("model") as VehicleModel
        control!!.get_vehicleByIdCard("130624198709183414")
        //选择送车人名下车辆
        cars_tv.setOnClickListener {
            builder.show()
        }
        save_btn.setOnClickListener {
            model = binding.model
            var img_list = ArrayList<FileModel>()
//            var bytt = bitmap_to_bytes()
//            var data = arrayOfNulls<Int>(bytt!!.size);
//            for (i in 0 until bytt!!.size) {
//                if (bytt[i] < 0) {
//                    data[i] = bytt[i] + 256
//                } else {
//                    data[i] = bytt[i].toInt()
//                }
//            }
            var i = arrayOfNulls<Int>(0)
            img_list.add(FileModel(i, "111", "01", ""))
            model!!.files = Gson().toJson(img_list).toString()
            control!!.add_prat(binding.model)
        }
    }

    fun bitmap_to_bytes(): ByteArray? {
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        //user_bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray()
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_car
    }
}
