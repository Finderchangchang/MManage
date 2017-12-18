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
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddCarBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.method.uu.compressImage
import gd.mmanage.model.*
import gd.mmanage.ui.CameraPersonActivity
import gd.mmanage.ui.DemoActivity
import gd.mmanage.ui.car_manage.AddServiceActivity
import kotlinx.android.synthetic.main.activity_add_car.*
import net.tsz.afinal.FinalDb
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 添加车辆信息
 * */
class AddCarActivity : BaseActivity<ActivityAddCarBinding>(), AbsModule.OnCallback {
    var model: VehicleModel? = null
    var emp_array: Array<String?>? = null//从业人员的集合
    var employees: List<EmployeeModel>? = null//从业人员model
    var db: FinalDb? = null
    var click_position = ""
    var dialog: LoadingDialog.Builder? = null
    var zt_list: List<CodeModel>? = null
    var files: List<FileModel>? = ArrayList<FileModel>()
    var list: ArrayList<VehicleModel> = ArrayList<VehicleModel>()
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 1 -> {
                success as NormalRequest<*>
                if (success.code == 0) {
                    if (TextUtils.isEmpty(model!!.VehicleId)) {
                        toast("添加成功")
                        var v_model = Gson().fromJson<VehicleModel>(success.obj.toString(), VehicleModel::class.java)
                        AddPersonActivity.context!!.finish()
                        if (v_model != null && !TextUtils.isEmpty(v_model.VehicleId)) {
                            startActivity(Intent(this@AddCarActivity, VehicleDetailActivity::class.java)
                                    .putExtra("id", v_model.VehicleId))//跳转到维修详情页
                        }
                        AddPersonActivity.context!!.finish()
                        finish()
                    } else {
                        toast("修改成功")
                        AddPersonActivity.context!!.finish()
                        finish()
                    }
                } else {
                    if (TextUtils.isEmpty(model!!.VehicleId)) {
                        toast("添加失败")
                    } else {
                        toast("修改失败")
                    }
                }
                dialog!!.dismiss()
                save_btn.isEnabled = true
            }
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<*>
                var kk = Gson().fromJson<DetailModel>(success.obj.toString(), DetailModel::class.java)
                if (kk != null) {
                    try {
                        var key = kk.Vehicle
                        model!!.VehiclePerson = key!!.VehiclePerson
                        model!!.VehiclePersonCertNumber = key.VehiclePersonCertNumber
                        model!!.VehiclePersonAddress = key.VehiclePersonAddress
                        model!!.VehiclePersonCertType = key.VehiclePersonCertType
                        model!!.VehiclePersonCompare = key.VehiclePersonCompare
                        binding.model = model
                        if (key.files != null) {
                            for (mo in key.files!!) {
                                //根据前面选择的图片进行显示
                                when (mo.FileType) {
                                    "C1" -> {
                                        left_bm = ImgUtils().base64ToBitmap(mo.FileContent)
                                        car_iv.setImageBitmap(left_bm)
                                    }
                                    "C6" -> {
                                        right_bm = ImgUtils().base64ToBitmap(mo.FileContent)
                                        id_card_iv.setImageBitmap(right_bm)
                                    }
                                }
                            }
                        } else {
                            var s = ""
                        }
                    } catch (e: Exception) {
                        var s = ""
                    }

                }
                dialog!!.dismiss()
            }
            command.car_manage + 5 -> {//根据身份证号获得车的记录(解析list然后用dialog的形式展示出来)
                success as NormalRequest<JsonArray>
                if (success.obj != null) {
                    var array = arrayOfNulls<String>(success.obj!!.size())
                    for (key in 0 until success.obj!!.size()) {
                        var kk = Gson().fromJson(success.obj!![key], VehicleModel::class.java)
                        array[key] = kk.VehicleNumber
//                    if (click_position > -1) {
//                        if (key == click_position) {
//                            try {
//                                if (kk.files!!.isNotEmpty()) {
//                                    for (mo in kk.files!!) {
//                                        //根据前面选择的图片进行显示
//                                        when (mo.FileType) {
//                                            "C1" -> {
//                                                left_bm = ImgUtils().base64ToBitmap(mo.FileContent)
//                                                car_iv.setImageBitmap(left_bm)
//                                            }
//                                            "C6" -> {
//                                                right_bm = ImgUtils().base64ToBitmap(mo.FileContent)
//                                                id_card_iv.setImageBitmap(right_bm)
//                                            }
//                                        }
//                                    }
//                                }
//                            } catch (e: Exception) {
//                                var s = ""
//                            }
//                        }
//                    }
                        list.add(kk)
                    }
                    cars_tv.text = list.size.toString() + "辆"
                    builder = AlertDialog.Builder(this)
                    builder.setTitle("修车记录")
                    builder.setItems(array) { a, b ->
                        var now_model = list[b]//点击名下车辆
                        model!!.VehicleOwner = now_model.VehicleOwner
                        model!!.VehicleType = now_model.VehicleType
                        model!!.VehicleBrand = now_model.VehicleBrand
                        model!!.VehicleColor = now_model.VehicleColor
                        model!!.VehicleNumber = now_model.VehicleNumber
                        model!!.VehicleEngine = now_model.VehicleEngine
                        model!!.VehicleFrameNumber = now_model.VehicleFrameNumber
                        control!!.get_vehicleById(now_model.VehicleId)
                        binding.model = model//刷新一下数据
                        dialog!!.show()
                    }
                }
                dialog!!.dismiss()
            }
            command.car_manage + 10 -> {
                success as NormalRequest<*>
                if (success.obj != null && !TextUtils.isEmpty(success.obj.toString())) {
                    var key = Gson().fromJson<CarModel>(success.obj.toString(), CarModel::class.java)

                    //id_card_iv.setImageBitmap(right_bm)
                    try {
                        model!!.VehicleOwner = key.Owner
                        model!!.VehicleNumber = key.VehicleNumber//车牌
                        model!!.VehicleBrand = key.VehicleBrand//品牌
                        model!!.VehicleFrameNumber = key.FrameNumber//车架
                        model!!.VehicleEngine = key.EngineNumber//发动机
                        var type = key.VehicleType

                        if (model!!.files != null && model!!.files!!.isNotEmpty()) {
                            for (mo in model!!.files!!) {
                                //根据前面选择的图片进行显示
                                when (mo.FileType) {
                                    "C1" -> {
                                        left_bm = ImgUtils().base64ToBitmap(mo.FileContent)
                                        car_iv.setImageBitmap(left_bm)
                                    }
                                    "C6" -> {
                                        right_bm = ImgUtils().base64ToBitmap(mo.FileContent)
                                        id_card_iv.setImageBitmap(right_bm)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        var s = ""
                    }
                    binding.model = model
                }
                dialog!!.dismiss()
            }
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<*>
                var model = Gson().fromJson<DetailModel>(success.obj.toString(), DetailModel::class.java)
                if (model != null) {

                    if (model.Vehicle!!.files!!.isNotEmpty()) {
                        for (mo in model.Vehicle!!.files!!) {
                            when (mo.FileType) {
                                "C1" -> {
                                    left_bm = ImgUtils().base64ToBitmap(mo.FileContent)
                                    car_iv.setImageBitmap(left_bm)
                                }
                                "C6" -> {
                                    right_bm = ImgUtils().base64ToBitmap(mo.FileContent)
                                    id_card_iv.setImageBitmap(right_bm)
                                }
                            }
                        }
                    }
                }
                dialog!!.dismiss()
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        dialog!!.dismiss()
        save_btn.isEnabled = true
    }

    var left_bm: Bitmap? = null
    var right_bm: Bitmap? = null
    var img_list = ArrayList<FileModel>()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //拍照成功将图片显示出来
        if (resultCode == 66) {
            var url = data!!.getStringExtra("data")
            var bmp = uu.getimage(100, url)
            if (img_click_position == 1) {
                right_bm = compressImage(uu.rotaingImageView(0, compressImage(bmp)))
                id_card_iv.setImageBitmap(right_bm)
                dialog!!.show()
                control!!.ocr_xs(right_bm!!)
            } else {
                left_bm = compressImage(uu.rotaingImageView(90, compressImage(bmp)))
                car_iv.setImageBitmap(left_bm)
            }
        }
    }

    var control: CarManageModule? = null
    var xc_url = ""
    var user_img: FileModel? = null
    var img_click_position = 0//0：拍车照片 1:ocr识别
    var ve_array: Array<String?>? = null//车辆状态的集合

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        dialog!!.show()
        control = getModule(CarManageModule::class.java, this)
        model = intent.getSerializableExtra("model") as VehicleModel
        xc_url = intent.getStringExtra("xc_url")
        if (!TextUtils.isEmpty(model!!.VehicleId)) {
            control!!.get_vehicleById(model!!.VehicleId)
        }
        if (!TextUtils.isEmpty(xc_url)) {
            var bmp = uu.getimage(100, xc_url)
            var xc_img = uu.compressImage(uu.rotaingImageView(90, uu.compressImage(bmp)))
            img_list.add(FileModel(ImgUtils().Only_bitmapToBase64(xc_img), "送车人实际照片", "C3", ""))
        }
        user_img = intent.getSerializableExtra("user_file") as FileModel
        click_position = intent.getStringExtra("click_position")
        if (!TextUtils.isEmpty(click_position)) {
            control!!.get_vehicleById(click_position)
        }
        binding.model = model//刷新一下数据
        control!!.get_vehicleByIdCard(model!!.VehiclePersonCertNumber)
        //选择送车人名下车辆
        cars_tv.setOnClickListener {
            builder.setNegativeButton("确定") { a, b -> }
            builder.show()
        }
        car_iv.setOnClickListener {
            img_click_position = 0
            startActivityForResult(Intent(this@AddCarActivity, CameraPersonActivity::class.java)
                    .putExtra("position", "5"), 77)//不显示东西
        }
        ocr_btn.setOnClickListener {
            img_click_position = 1
            startActivityForResult(Intent(this@AddCarActivity, CameraPersonActivity::class.java)
                    .putExtra("position", "4"), 77)//显示行驶证（文字）
        }
        id_card_iv.setOnClickListener {
            img_click_position = 1
            startActivityForResult(Intent(this@AddCarActivity, CameraPersonActivity::class.java)
                    .putExtra("position", "4"), 77)//显示行驶证（文字）
        }
        //选择车辆类型
        ll4.setOnClickListener {

        }
        save_btn.setOnClickListener {
            if (check_null()) {
                save_btn.isEnabled = false
                dialog!!.setTitle("保存中，请稍后...")
                dialog!!.show()
                model = binding.model
                if (TextUtils.isEmpty(model!!.VehicleColor)) {
                    model!!.VehicleColor = "黑色"
                }
                if (user_img!!.FileContent != null) {
                    img_list.add(user_img!!)
                }
                img_list.add(FileModel(ImgUtils().Only_bitmapToBase64(left_bm), "机动车现场照片", "C1", ""))
                img_list.add(FileModel(ImgUtils().Only_bitmapToBase64(right_bm), "行驶证照片", "C6", ""))
                model!!.files = img_list
                model!!.VehicleReceiveTime = Utils.normalTime
                control!!.add_prat(model!!)
            }
        }
        //收车人
        ll9.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("请选择收车人信息")
            builder.setItems(emp_array) { dialog, which ->
                model!!.VehicleReceivePerson = employees!![which].EmployeeName
                binding.model = model//刷新数据
            }
            builder.setNegativeButton("确定") { a, b -> }
            builder.create().show()
        }
        ll4.setOnClickListener {
            dialog(ve_array!!, 1)
        }
        model!!.VehicleReceiveUser = Utils.getCache(sp.user_id)
        employees = db!!.findAll(EmployeeModel::class.java)
        emp_array = arrayOfNulls(employees!!.size)
        for (i in 0 until employees!!.size) {
            emp_array!![i] = employees!![i].EmployeeName
            if (TextUtils.isEmpty(model!!.VehicleReceivePerson)) {
                model!!.VehicleReceivePerson = employees!![0].EmployeeName
                binding.model = model//刷新数据
            }
        }
        zt_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_VehicleType'")
        ve_array = arrayOfNulls(zt_list!!.size)
        var ve_id = "K"
        if (!TextUtils.isEmpty(model!!.VehicleType)) {
            ve_id = model!!.VehicleType
        }
        for (id in 0 until ve_array!!.size) {
            var m = zt_list!![id]
            ve_array!![id] = m.Name
            if (ve_id == m.ID) {
                model!!.VehicleType = m.ID
                binding.type = m.Name
            }
        }
    }


    /**
     * 弹出的列表选择框
     * */
    fun dialog(key: Array<String?>, method: Int) {
        //dialog参数设置
        val builder = AlertDialog.Builder(this)  //先得到构造器
        builder.setItems(key) { dialog, which ->
            when (method) {
                1 -> {//车辆类型
                    model!!.VehicleType = zt_list!![which].ID
                    model!!.VehicleType = zt_list!![which].ID
                    binding.type = zt_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.create().show()
    }

    //检测输入的是不是为空
    fun check_null(): Boolean {
        if (TextUtils.isEmpty(model!!.VehicleOwner)) {
            toast("车辆所有人姓名不能为空")
            return false
        } else if (TextUtils.isEmpty(model!!.VehicleNumber)) {
            toast("车牌号码不能为空")
            return false
        } else if (TextUtils.isEmpty(model!!.VehicleFrameNumber)) {
            toast("车架号不能为空")
            return false
        } else if (TextUtils.isEmpty(model!!.VehicleEngine)) {
            toast("发动机号不能为空")
            return false
        } else if (left_bm == null) {
            toast("车辆照片不能为空")
            return false
        } else if (right_bm == null) {
            toast("行驶证不能为空")
            return false
        } else if (TextUtils.isEmpty(binding.type)) {
            toast("车辆类型不能为空")
            return false
        }
        return true
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_car
    }
}
