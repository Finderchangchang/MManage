package gd.mmanage.ui.vehicle

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddCarBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.FileModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.VehicleModel
import kotlinx.android.synthetic.main.activity_add_car.*
import net.tsz.afinal.FinalDb
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 添加车辆信息
 * */
class AddCarActivity : BaseActivity<ActivityAddCarBinding>(), AbsModule.OnCallback {
    var model: VehicleModel? = null
    var emp_array: Array<String?>? = null//从业人员的集合
    var employees: List<EmployeeModel>? = null//从业人员model
    var db: FinalDb? = null
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 1 -> {
                success as NormalRequest<JsonObject>
                if (success.code == 0) {
                    if (TextUtils.isEmpty(model!!.VehicleId)) {
                        toast("添加成功")
                    } else {
                        toast("修改成功")
                    }
                } else {
                    if (TextUtils.isEmpty(model!!.VehicleId)) {
                        toast("添加失败")
                    } else {
                        toast("修改失败")
                    }
                }
            }
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
    var xc_url = ""
    var user_img: FileModel? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        control = getModule(CarManageModule::class.java, this)
        model = intent.getSerializableExtra("model") as VehicleModel
        xc_url = intent.getStringExtra("xc_url")
        user_img = intent.getSerializableExtra("user_file") as FileModel
        var img_list = ArrayList<FileModel>()
        var bmp = uu.getimage(100, xc_url)
        var xc_img = uu.compressImage(uu.rotaingImageView(90, uu.compressImage(bmp)))
        img_list.add(FileModel(bitmap_to_bytes(xc_img), "送车人实际照片", "C3", ""))
        img_list.add(user_img!!)
        model!!.files = Gson().toJson(img_list).toString()
        binding.model = model//刷新一下数据
        control!!.get_vehicleByIdCard("130624198709183414")
        //选择送车人名下车辆
        cars_tv.setOnClickListener {
            builder.show()
        }
        save_btn.setOnClickListener {
            model = binding.model
            model!!.VehicleReceiveTime = Utils.normalTime
            control!!.add_prat(model!!)
        }
        //收车人
        ll9.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("请选择收车人信息")
            builder.setItems(emp_array) { dialog, which ->
                model!!.VehicleReceivePerson = employees!![0].EmployeeName
                binding.model = model//刷新数据
            }
            builder.create().show()
        }
        employees = db!!.findAll(EmployeeModel::class.java)
        emp_array = arrayOfNulls(employees!!.size)
        for (i in 0 until employees!!.size) {
            emp_array!![i] = employees!![i].EmployeeName
        }
        if (employees!!.isNotEmpty()) {
            model!!.VehicleReceivePerson = employees!![0].EmployeeName
            binding.model = model//刷新数据
        }
    }

    fun bitmap_to_bytes(bitmap: Bitmap): Array<Int?> {
        var baos: ByteArrayOutputStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        var bytt = baos.toByteArray()
        var data = arrayOfNulls<Int>(bytt!!.size);
        for (i in 0 until bytt!!.size) {
            if (bytt[i] < 0) {
                data[i] = bytt[i] + 256
            } else {
                data[i] = bytt[i].toInt()
            }
        }
        return data
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_car
    }
}
