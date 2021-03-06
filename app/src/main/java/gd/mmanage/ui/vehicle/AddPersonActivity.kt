package gd.mmanage.ui.vehicle

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jiangyy.easydialog.LoadingDialog
import com.zkteco.android.IDReader.IDPhotoHelper
import com.zkteco.android.IDReader.WLTService
import com.zkteco.id3xx.IDCardReader
import com.zkteco.id3xx.meta.IDCardInfo
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.callback.LzyResponse
import gd.mmanage.camera.BitmapUtils
import gd.mmanage.camera.Camera2Activity
import gd.mmanage.camera.CameraActivity
import gd.mmanage.camera.CommonUtils
import gd.mmanage.config.LQRPhotoSelectUtils
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddPersonBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.method.uu.compressImage
import gd.mmanage.model.*
import gd.mmanage.ui.CameraPersonActivity
import kotlinx.android.synthetic.main.activity_add_person.*
import net.tsz.afinal.FinalDb
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 人员添加
 * */
class AddPersonActivity : BaseActivity<ActivityAddPersonBinding>(), AbsModule.OnCallback {
    var nation_list: List<CodeModel>? = null
    var mz_array: Array<String?>? = null//民族的集合
    var db: FinalDb? = null
    var xc_img: Bitmap? = null//现场照片
    var user_img: Bitmap? = null//用户头像
    var xc_url = ""
    var dialog: LoadingDialog.Builder? = null
    var alert_builder: AlertDialog.Builder? = null
    var mFile: File? = null

    companion object {
        var context: AddPersonActivity? = null
    }

    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 5 -> {//根据身份证号获得车的记录(解析list然后用dialog的形式展示出来)
                success as NormalRequest<JsonArray>
                if (success.obj != null) {
                    var array = arrayOfNulls<String>(success.obj!!.size())
                    var list = ArrayList<VehicleModel>()
                    for (key in 0 until success.obj!!.size()) {
                        var kk = Gson().fromJson(success.obj!![key], VehicleModel::class.java)
                        array[key] = kk.VehicleNumber
                        list.add(kk)
                    }
                    dialog!!.dismiss()
                    if (list.size > 0) {
                        alert_builder!!.setNegativeButton("取消", null);
                        alert_builder!!.setNeutralButton("修车记录(" + list.size + ")") { a, b ->
                            builder = AlertDialog.Builder(this)
                            builder!!.setTitle("修车记录")
                            builder!!.setItems(array) { _, b ->
                                var now_model = list[b]
                                model.VehicleOwner = now_model.VehicleOwner
                                model.VehicleType = now_model.VehicleType
                                model.VehicleBrand = now_model.VehicleBrand
                                model.VehicleColor = now_model.VehicleColor
                                model.VehicleNumber = now_model.VehicleNumber
                                model.VehicleEngine = now_model.VehicleEngine
                                model.VehicleFrameNumber = now_model.VehicleFrameNumber
                                startActivity(Intent(this@AddPersonActivity, AddCarActivity::class.java)
                                        .putExtra("model", model)
                                        .putExtra("click_position", now_model.VehicleId)
                                        .putExtra("xc_url", xc_url)//FileModel(user_img, "送车人证件照", "C2", "")
                                        .putExtra("user_file", FileModel(ImgUtils().Only_bitmapToBase64(user_img), "送车人证件照", "C2", "")))
                            }
                            builder.setNegativeButton("确定") { a, b -> }
                            builder.show()
                        }

                        alert_builder!!.show();

                    } else {
                        toast("当前送车人名下无车辆")
                    }
                }

            }
            command.car_manage + 8 -> {
                var ss = success as NormalRequest<JsonObject>
                if (ss.obj != null) {
                    var a = ss.obj as JsonObject
                    var key = Gson().fromJson<FaceRecognitionModel>(a, FaceRecognitionModel::class.java)
                    result_btn.visibility = View.VISIBLE
                    alert_builder = AlertDialog.Builder(this);
                    alert_builder!!.setTitle("提示");
                    if ("false" != key.SamePerson) {//比对为同一人
                        result_btn.setImageResource(R.mipmap.yes)
                        model.VehiclePersonCompare = key.FaceScore
                        control!!.get_vehicleByIdCard(binding.model.VehiclePersonCertNumber)
                        bi_tv.visibility = View.VISIBLE
                        alert_builder!!.setMessage("比对通过");
                    } else {
                        result_btn.setImageResource(R.mipmap.no)
                        alert_builder!!.setMessage("比对未通过，相似度为：" + key.FaceScore);
                        model.VehiclePersonCompare = ""
                        bi_tv.visibility = View.GONE
                    }
                    bi_tv.text = "识别率 " + model.VehiclePersonCompare
                }
                dialog!!.dismiss()

            }
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<*>
                if (success.obj != null) {
                    var model = Gson().fromJson<DetailModel>(success.obj.toString(), DetailModel::class.java)
                    if (model != null && model.Vehicle != null && model.Vehicle!!.files != null) {
                        if (model.Vehicle!!.files!!.isNotEmpty()) {
                            for (mo in model.Vehicle!!.files!!) {
                                when (mo.FileType) {
                                    "C2" -> {
                                        card_user_iv.setImageBitmap(ImgUtils().base64ToBitmap(mo.FileContent))
                                    }
                                    "C3" -> {
                                        real_user_iv.setImageBitmap(ImgUtils().base64ToBitmap(mo.FileContent))
                                    }
                                }
                            }
                        }
                    }
                }
                dialog!!.dismiss()
            }
        //显示详情数据
            command.car_manage + 9 -> {
                success as NormalRequest<*>
                if (success.obj != null) {
                    var key = Gson().fromJson<UserCarModel>(success.obj.toString(), UserCarModel::class.java)
                    if (key != null) {
                        user_img = ImgUtils().base64ToBitmap(key.PersonFaceImage)
                        card_user_iv.setImageBitmap(user_img)
                        model.VehicleTakePersonCompare = ""
                        check_two_img()
                        model.VehiclePerson = key.PersonName
                        model.VehiclePersonCertNumber = key.IdentyNumber
                        model.VehiclePersonAddress = key.PersonAddress
                        binding.model = model
                    } else {
                        toast("识别失败")
                    }
                }
                dialog!!.dismiss()
            }
            command.car_manage + 11 -> {
                success as NormalRequest<*>
                if (success.obj != null) {
                    var key = Gson().fromJson<CardUserModel>(success.obj.toString(), CardUserModel::class.java)
                    if (key != null) {
                        user_img = ImgUtils().base64ToBitmap(key.PersonFaceImage)
                        model.VehicleTakePersonCompare = ""
                        card_user_iv.setImageBitmap(user_img)
                        check_two_img()
                        model.VehiclePerson = key.PersonName
                        model.VehiclePersonCertNumber = key.IdentyNumber
                        model.VehiclePersonAddress = key.PersonAddress
                        binding.model = model
                    } else {
                        toast("识别失败")
                        dialog!!.dismiss()
                    }
                }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        dialog!!.dismiss()
    }

    /**
     * 两张图片进行比对操作
     * */
    fun check_two_img() {
        if (xc_img != null && user_img != null) {
            dialog!!.show()
            control!!.check_two_img(xc_img!!, user_img!!)
        } else {
            dialog!!.dismiss()
        }
    }

    var mLqrPhotoSelectUtils: LQRPhotoSelectUtils? = null

    var model: VehicleModel = VehicleModel()
    var control: CarManageModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        context = this
        control = getModule(CarManageModule::class.java, this)
        model = intent.getSerializableExtra("model") as VehicleModel
        if (!TextUtils.isEmpty(model.VehicleId)) {
            dialog!!.show()
            control!!.get_vehicleById(model.VehicleId)
        }
        title_bar.setRightClick { startActivity(Intent(context, SearchVehicleActivity::class.java)) }
        //证件识别操作
        read_card_btn.setOnClickListener {
            if (TextUtils.isEmpty(Utils.getCache(sp.blueToothAddress))) {
                toast("请检查蓝牙读卡设备设置！")
            } else {
                dialog!!.show()
                OnBnRead()
            }
        }
        read_ocr_btn.setOnClickListener {
            if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
                mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                    var bmp = compressImage(uu.rotaingImageView(90, compressImage(uu.getimage(100, outputFile.getAbsolutePath()))))
                    dialog!!.show()
                    getModule(CarManageModule::class.java, this).ocr_sfz(bmp)
                }, false)
                mLqrPhotoSelectUtils!!.takePhoto()
            } else {
                startActivityForResult(Intent(this@AddPersonActivity, CameraPersonActivity::class.java)
                        .putExtra("position", "2"), 1)
                if (Build.VERSION.SDK_INT > 21) {
                    TedPermission.with(this)
                            .setRationaleMessage("我们需要使用您设备上的相机以完成拍照。\n当 Android 系统请求将相机权限授予 HelloCamera2 时，请选择『允许』。")
                            .setDeniedMessage("如果您不对 HelloCamera2 授予相机权限，您将不能完成拍照。")
                            .setRationaleConfirmText("确定")
                            .setDeniedCloseButtonText("关闭")
                            .setGotoSettingButtonText("设定")
                            .setPermissionListener(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    val intent: Intent
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        intent = Intent(this@AddPersonActivity, Camera2Activity::class.java)
                                    } else {
                                        AlertDialog.Builder(this@AddPersonActivity)
                                                .setTitle("不支持的 API Level")
                                                .setMessage("Camera2 API 仅在 API Level 21 以上可用, 当前 API Level : " + Build.VERSION.SDK_INT)
                                                .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
                                                .show()
                                        return
                                    }
                                    mFile = CommonUtils.createImageFile("mFile")
                                    //文件保存的路径和名称
                                    intent.putExtra("file", mFile.toString())
                                    //拍照时的提示文本
                                    intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像")
                                    //是否使用整个画面作为取景区域(全部为亮色区域)
                                    intent.putExtra("hideBounds", false)
                                    //最大允许的拍照尺寸（像素数）
                                    intent.putExtra("maxPicturePixels", 3840 * 2160)
//                                startActivityForResult(intent, 1)
                                }

                                override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                            }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
                } else {
                    TedPermission.with(this)
                            .setRationaleMessage("我们需要使用您设备上的相机以完成拍照。\n当 Android 系统请求将相机权限授予 HelloCamera2 时，请选择『允许』。")
                            .setDeniedMessage("如果您不对 HelloCamera2 授予相机权限，您将不能完成拍照。")
                            .setRationaleConfirmText("确定")
                            .setDeniedCloseButtonText("关闭")
                            .setGotoSettingButtonText("设定")
                            .setPermissionListener(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    val intent: Intent
                                    intent = Intent(this@AddPersonActivity, CameraActivity::class.java)
                                    mFile = CommonUtils.createImageFile("mFile")
                                    //文件保存的路径和名称
                                    intent.putExtra("file", mFile.toString())
                                    //拍照时的提示文本
                                    intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像")
                                    //是否使用整个画面作为取景区域(全部为亮色区域)
                                    intent.putExtra("hideBounds", false)
                                    //最大允许的拍照尺寸（像素数）
                                    intent.putExtra("maxPicturePixels", 3840 * 2160)
//                                startActivityForResult(intent, 1)
                                }

                                override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                            }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
                }
            }
        }
        model.VehiclePersonCertType = "1"
        binding.model = model
        model.VehiclePersonPhone = ""
        //跳转到拍照页面
        real_user_iv.setOnClickListener {
            model.VehiclePersonCertType = "1"
            if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
                mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                    var jd = uu.readPictureDegree(outputFile.getAbsolutePath())//获得旋转角度
                    var bmp = compressImage(uu.rotaingImageView(jd, compressImage(uu.getimage(100, outputFile.getAbsolutePath()))))
                    xc_url = outputFile.getAbsolutePath()
                    xc_img = bmp
                    real_user_iv.setImageBitmap(xc_img)
                    check_two_img()
                }, false)
                mLqrPhotoSelectUtils!!.takePhoto()
            } else {
                startActivityForResult(Intent(this@AddPersonActivity, CameraPersonActivity::class.java)
                        .putExtra("position", "1"), 77)
                if (Build.VERSION.SDK_INT > 21) {
                    TedPermission.with(this)
                            .setRationaleMessage("我们需要使用您设备上的相机以完成拍照。\n当 Android 系统请求将相机权限授予 HelloCamera2 时，请选择『允许』。")
                            .setDeniedMessage("如果您不对 HelloCamera2 授予相机权限，您将不能完成拍照。")
                            .setRationaleConfirmText("确定")
                            .setDeniedCloseButtonText("关闭")
                            .setGotoSettingButtonText("设定")
                            .setPermissionListener(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    val intent: Intent
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        intent = Intent(this@AddPersonActivity, Camera2Activity::class.java)
                                    } else {
                                        AlertDialog.Builder(this@AddPersonActivity)
                                                .setTitle("不支持的 API Level")
                                                .setMessage("Camera2 API 仅在 API Level 21 以上可用, 当前 API Level : " + Build.VERSION.SDK_INT)
                                                .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
                                                .show()
                                        return
                                    }
                                    mFile = CommonUtils.createImageFile("mFile")
                                    //文件保存的路径和名称
                                    intent.putExtra("file", mFile.toString())
                                    //拍照时的提示文本
                                    intent.putExtra("hint", "")
                                    //是否使用整个画面作为取景区域(全部为亮色区域)
                                    intent.putExtra("hideBounds", false)
                                    //最大允许的拍照尺寸（像素数）
                                    intent.putExtra("maxPicturePixels", 3840 * 2160)
//                                startActivityForResult(intent, 77)
                                }

                                override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                            }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
                } else {
                    TedPermission.with(this)
                            .setRationaleMessage("我们需要使用您设备上的相机以完成拍照。\n当 Android 系统请求将相机权限授予 HelloCamera2 时，请选择『允许』。")
                            .setDeniedMessage("如果您不对 HelloCamera2 授予相机权限，您将不能完成拍照。")
                            .setRationaleConfirmText("确定")
                            .setDeniedCloseButtonText("关闭")
                            .setGotoSettingButtonText("设定")
                            .setPermissionListener(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    val intent: Intent
                                    intent = Intent(this@AddPersonActivity, CameraActivity::class.java)
                                    mFile = CommonUtils.createImageFile("mFile")
                                    //文件保存的路径和名称
                                    intent.putExtra("file", mFile.toString())
                                    //拍照时的提示文本
                                    intent.putExtra("hint", "")
                                    //是否使用整个画面作为取景区域(全部为亮色区域)
                                    intent.putExtra("hideBounds", false)
                                    //最大允许的拍照尺寸（像素数）
                                    intent.putExtra("maxPicturePixels", 3840 * 2160)
//                                startActivityForResult(intent, 77)
                                }

                                override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                            }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
                }
            }
        }
        read_driver_btn.setOnClickListener {
            if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
                mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                    var jd = uu.readPictureDegree(outputFile.getAbsolutePath())//获得旋转角度
                    var bmp = compressImage(uu.rotaingImageView(jd, compressImage(uu.getimage(100, outputFile.getAbsolutePath()))))
                    dialog!!.show()
                    control!!.ocr_js(bmp)
                }, false)
                mLqrPhotoSelectUtils!!.takePhoto()
            } else {
                model.VehiclePersonCertType = "2"
                startActivityForResult(Intent(this@AddPersonActivity, CameraPersonActivity::class.java)
                        .putExtra("position", "3"), 2)//驾驶证识别
                if (Build.VERSION.SDK_INT > 21) {
                    TedPermission.with(this)
                            .setRationaleMessage("我们需要使用您设备上的相机以完成拍照。\n当 Android 系统请求将相机权限授予 HelloCamera2 时，请选择『允许』。")
                            .setDeniedMessage("如果您不对 HelloCamera2 授予相机权限，您将不能完成拍照。")
                            .setRationaleConfirmText("确定")
                            .setDeniedCloseButtonText("关闭")
                            .setGotoSettingButtonText("设定")
                            .setPermissionListener(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    val intent: Intent
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        intent = Intent(this@AddPersonActivity, Camera2Activity::class.java)
                                    } else {
                                        AlertDialog.Builder(this@AddPersonActivity)
                                                .setTitle("不支持的 API Level")
                                                .setMessage("Camera2 API 仅在 API Level 21 以上可用, 当前 API Level : " + Build.VERSION.SDK_INT)
                                                .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
                                                .show()
                                        return
                                    }
                                    mFile = CommonUtils.createImageFile("mFile")
                                    //文件保存的路径和名称
                                    intent.putExtra("file", mFile.toString())
                                    //拍照时的提示文本
                                    intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像")
                                    //是否使用整个画面作为取景区域(全部为亮色区域)
                                    intent.putExtra("hideBounds", false)
                                    //最大允许的拍照尺寸（像素数）
                                    intent.putExtra("maxPicturePixels", 3840 * 2160)
//                                startActivityForResult(intent, 2)
                                }

                                override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                            }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
                } else {
                    TedPermission.with(this)
                            .setRationaleMessage("我们需要使用您设备上的相机以完成拍照。\n当 Android 系统请求将相机权限授予 HelloCamera2 时，请选择『允许』。")
                            .setDeniedMessage("如果您不对 HelloCamera2 授予相机权限，您将不能完成拍照。")
                            .setRationaleConfirmText("确定")
                            .setDeniedCloseButtonText("关闭")
                            .setGotoSettingButtonText("设定")
                            .setPermissionListener(object : PermissionListener {
                                override fun onPermissionGranted() {
                                    val intent: Intent
                                    intent = Intent(this@AddPersonActivity, CameraActivity::class.java)
                                    mFile = CommonUtils.createImageFile("mFile")
                                    //文件保存的路径和名称
                                    intent.putExtra("file", mFile.toString())
                                    //拍照时的提示文本
                                    intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像")
                                    //是否使用整个画面作为取景区域(全部为亮色区域)
                                    intent.putExtra("hideBounds", false)
                                    //最大允许的拍照尺寸（像素数）
                                    intent.putExtra("maxPicturePixels", 3840 * 2160)
                                    //startActivityForResult(intent, 2)
                                }

                                override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                            }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
                }
            }
        }
        next_btn.setOnClickListener {
            if (check_null()) {
                var intent = Intent(this@AddPersonActivity, AddCarActivity::class.java)
                if (user_img != null) {
                    intent.putExtra("user_file", FileModel(ImgUtils().Only_bitmapToBase64(user_img), "送车人证件照", "C2", ""))
                } else {
                    intent.putExtra("user_file", FileModel())
                }
                startActivity(intent
                        .putExtra("model", model)
                        .putExtra("xc_url", xc_url)
                        .putExtra("click_position", ""))//FileModel(user_img, "送车人证件照", "C2", "")
            }
        }
        nation_ll.setOnClickListener {
            dialog(mz_array!!, 3)
        }
        init_data()
        //init_blue()
    }

    //检测输入的是不是为空
    fun check_null(): Boolean {
        if (TextUtils.isEmpty(model.VehiclePerson)) {
            toast("送车人姓名不能为空")
            return false
        } else if (TextUtils.isEmpty(model.VehiclePersonCertNumber)) {
            toast("送车人证件号码不能为空")
            return false
        } else if (TextUtils.isEmpty(model.VehiclePersonAddress)) {
            toast("家庭住址不能为空")
            return false
        } else if (TextUtils.isEmpty(model.VehicleId) && TextUtils.isEmpty(model.VehiclePersonCompare)) {
            //添加图片不能为空
            //修改图片可以为空
            toast("请先进行人像比对")
            return false
//        } else if (TextUtils.isEmpty(model.VehiclePersonPhone)) {
//            toast("送车电话不能为空")
//            return false
        }
        return true
    }

    fun init_data() {
        var nation_id = "01"
        nation_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_Nation'")
        mz_array = arrayOfNulls(nation_list!!.size)
        for (id in 0 until nation_list!!.size) {
            var mo = nation_list!![id]
            mz_array!![id] = mo.Name
            if (nation_id == mo.ID) {
                binding.nation = mo.Name
                model.VehiclePersonNation = mo.ID
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
                3 -> {//民族
                    model!!.VehiclePersonNation = nation_list!![which].ID
                    binding.nation = nation_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.setTitle(R.string.please_selected_nation)
        builder.setNegativeButton("确定") { a, b -> }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //拍照成功将图片显示出来
        if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
            mLqrPhotoSelectUtils!!.attachToActivityForResult(requestCode, resultCode, data)
        } else {
            if (resultCode == 66) {
                var url = data!!.getStringExtra("data")
                var jd = uu.readPictureDegree(url)//获得旋转角度
                var bmp = uu.getimage(100, url)
                bmp = uu.rotaingImageView(jd, bmp)

                var card = compressImage(uu.rotaingImageView(0, compressImage(bmp)))
                when (requestCode) {
                    1 -> {//身份证识别
                        dialog!!.show()
                        control!!.ocr_sfz(card)
                    }
                    2 -> {//驾驶证识别
                        dialog!!.show()
                        control!!.ocr_js(card)
                    }
                    else -> {//现场照片
                        xc_url = url
                        xc_img = compressImage(uu.rotaingImageView(90, compressImage(bmp)))
                        real_user_iv.setImageBitmap(xc_img)
                        check_two_img()
                    }
                }
            } else if (resultCode == 200) {
                var bit = BitmapUtils.compressToResolution(mFile, 1920 * 1080)
                when (requestCode) {
                    1 -> {
                        dialog!!.show()
                        control!!.ocr_sfz(bit)
                    }
                    2 -> {
                        dialog!!.show()
                        control!!.ocr_js(bit)
                    }
                    77 -> {
                        xc_url = mFile.toString()
                        xc_img = compressImage(uu.rotaingImageView(90, compressImage(bit)))
                        real_user_iv.setImageBitmap(xc_img)
                        check_two_img()
                    }
                }
            }
        }
    }

    internal var idCardReader: IDCardReader? = null
    private var workThread: WorkThread? = null
    var isRead = false
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    //设备连接
    fun OnBnRead() {
        if (null == idCardReader) {
            val device = mBluetoothAdapter!!.getRemoteDevice(Utils.getCache(sp.blueToothAddress))
            try {
                Thread() { connect(device) }.start()
            } catch (e: Exception) {
                Toast.makeText(this@AddPersonActivity, "连接失败", Toast.LENGTH_SHORT).show()
            }
        } else {
            isRead = true
            workThread = WorkThread()
            workThread!!.start()// 线程启动
            index = 0
        }
    }

    fun init_blue() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!mBluetoothAdapter!!.isEnabled()) {
            mBluetoothAdapter!!.enable()
        }
        var mFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, mFilter)
        // 注册搜索完时的receiver
        mFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mReceiver, mFilter)
        if (mBluetoothAdapter!!.isDiscovering) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
        mBluetoothAdapter!!.startDiscovery()
    }

    //联网操作
    private fun connect(device: BluetoothDevice) {
        try {
            idCardReader = IDCardReader()
            idCardReader!!.setDevice(device.address)
            var ret = idCardReader!!.openDevice()
            runOnUiThread {
                if (IDCardReader.ERROR_SUCC == ret) {
                    //textView.setText(bluetooth)
                    //dialog.dismiss()

                    workThread = WorkThread()
                    workThread!!.start()// 线程启动
                    isRead = true
                } else {
                    idCardReader = null
                }
            }
        } catch (e: Exception) {
            idCardReader = null
        }
    }

    var index = 0

    private inner class WorkThread : Thread() {
        override fun run() {
            super.run()
            while (index < 10) {
                runOnUiThread {
                    if (null == idCardReader) {
                        val device = mBluetoothAdapter!!.getRemoteDevice(Utils.getCache(sp.blueToothAddress))
                        try {
                            connect(device)
                        } catch (e: Exception) {
                            Toast.makeText(this@AddPersonActivity, "连接失败", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (isRead) {
                            if (!ReadCardInfo()) {
                                index++
                                if (index == 9) {
                                    Toast.makeText(this@AddPersonActivity, "读卡失败", Toast.LENGTH_SHORT).show()
                                }
                                //textView.post(Runnable { textView.setText("请放卡...") })

                            } else {
                                //textView.post(Runnable { textView.setText("读卡成功，请放入下一张卡") })
                                index = 11
                            }
                        }
                    }
                }
            }

        }
    }

    //读卡操作
    fun ReadCardInfo(): Boolean {
        if (idCardReader != null) {
            if (!idCardReader!!.sdtFindCard() || !idCardReader!!.sdtSelectCard()) run {
                return false
            } else {
                val idCardInfo = IDCardInfo()
                if (idCardReader!!.sdtReadCard(1, idCardInfo)) {
                    val time = System.currentTimeMillis()
                    val mCalendar = Calendar.getInstance()
                    mCalendar.timeInMillis = time
                    runOnUiThread {
                        model.VehiclePerson = idCardInfo.name
                        model.VehiclePersonCertNumber = idCardInfo.id
                        model.VehiclePersonAddress = idCardInfo.address
                        binding.nation = idCardInfo.nation
                        binding.model = model
                        isRead = false
                    }


                    if (idCardInfo.photo != null) {
                        val buf = ByteArray(WLTService.imgLength)
                        if (1 == WLTService.wlt2Bmp(idCardInfo.photo, buf)) {
                            user_img = IDPhotoHelper.Bgr2Bitmap(buf)
                            if (null != user_img) {
                                card_user_iv.post(Runnable { card_user_iv.setImageBitmap(user_img) })
                                dialog!!.dismiss()
                                check_two_img()

                            }
                        }
                    }
                    dialog!!.dismiss()
                    idCardReader!!.closeDevice()
                    idCardReader = null
                    return true
                } else {
                    return false
                }
            }
        } else {
            return false
        }
    }

    //搜索结果处理
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            val action = arg1.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device = arg1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device.address == Utils.getCache(sp.blueToothAddress)) {//DC:0D:30:04:20:D9
                    //0064 00:13:04:84:00:64
                    try {
                        // connect(device)
                    } catch (e: Exception) {
                        Toast.makeText(this@AddPersonActivity, "连接失败", Toast.LENGTH_SHORT).show()
                    }

                }
            } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                // setProgressBarIndeterminateVisibility(false);
                title = "搜索蓝牙设备"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //checkPermission()
    }

    //页面关闭处理
    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(mReceiver)
//        OnBnDisconn()
    }

    //断开连接
    fun OnBnDisconn() {
        if (null == idCardReader) {
            return
        }
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        idCardReader!!.closeDevice()
        idCardReader = null
        //textView.setText("")
    }

    //权限管理
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
        } else if (requestCode == 1) {

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    //检测权限
    private fun checkPermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            var s = ""
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_person
    }
}
