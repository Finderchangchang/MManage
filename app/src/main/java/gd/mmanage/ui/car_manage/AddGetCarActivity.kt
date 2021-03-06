package gd.mmanage.ui.car_manage

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
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
import gd.mmanage.camera.BitmapUtils
import gd.mmanage.camera.Camera2Activity
import gd.mmanage.camera.CameraActivity
import gd.mmanage.camera.CommonUtils
import gd.mmanage.config.LQRPhotoSelectUtils
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddGetCarBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.model.*
import gd.mmanage.ui.CameraPersonActivity
import gd.mmanage.ui.vehicle.AddCarActivity
import kotlinx.android.synthetic.main.activity_add_get_car.*
import net.tsz.afinal.FinalDb
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *取车登记
 * */
class AddGetCarActivity : BaseActivity<ActivityAddGetCarBinding>(), AbsModule.OnCallback {

    var nation_list: List<CodeModel>? = null
    var mz_array: Array<String?>? = null//民族的集合
    var db: FinalDb? = null
    var xc_img: Bitmap? = null//现场照片
    var user_img: Bitmap? = null//用户头像
    var vehicleId = ""
    var dialog: LoadingDialog.Builder? = null//转圈的dialog

    override fun onSuccess(result: Int, success: Any?) {
        next_btn.isEnabled = true
        when (result) {
            command.car_manage + 8 -> {
                var ss = success as NormalRequest<JsonObject>
                var a = ss.obj as JsonObject
                var key = Gson().fromJson<FaceRecognitionModel>(a, FaceRecognitionModel::class.java)
                if ("false" != key.SamePerson) {//比对为同一人
                    result_btn.setImageResource(R.mipmap.yes)
                    model.VehicleTakePersonCompare = key.FaceScore
                    bi_tv.visibility = View.VISIBLE
                } else {
                    result_btn.setImageResource(R.mipmap.no)
                    model.VehicleTakePersonCompare = ""
                    bi_tv.visibility = View.GONE
                }
                toast("通过率:" + key.FaceScore)
                result_btn.visibility = View.VISIBLE
                bi_tv.text = "识别率 " + model.VehicleTakePersonCompare
                dialog!!.dismiss()
            }
            command.car_manage + 1 -> {
                success as NormalRequest<JsonElement>
                if (success.code == 0) {
                    dialog!!.dismiss()
                    setResult(1)
                    finish()
                    toast("取车成功")
                } else {
                    toast(success.message)
                }
                dialog!!.dismiss()
            }
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<*>
                var kk = Gson().fromJson<DetailModel>(success.obj.toString(), DetailModel::class.java)
                if (kk != null) {
                    try {
                        var key = kk.Vehicle
                        model.VehicleTakePerson = key!!.VehiclePerson
                        model.VehicleTakePersonCertNumber = key.VehiclePersonCertNumber
                        model.VehicleTakePersonAddress = key.VehiclePersonAddress
                        model.VehicleTakePersonCertType = key.VehiclePersonCertType
                        model.VehicleTakePersonCompare = key.VehiclePersonCompare
                        binding.model = model
                        if (kk.Vehicle!!.files!!.isNotEmpty()) {
                            for (mo in kk.Vehicle!!.files!!) {
                                when (mo.FileType) {
                                    "C2" -> {
                                        xc_img = ImgUtils().base64ToBitmap(mo.FileContent)
                                        card_user_iv.setImageBitmap(xc_img)
                                    }
                                    "C3" -> {
                                        user_img = ImgUtils().base64ToBitmap(mo.FileContent)
                                        real_user_iv.setImageBitmap(user_img)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        var s = ""
                    }

                }
                dialog!!.dismiss()
            }
            command.car_manage + 11 -> {
                success as NormalRequest<*>
                var key = Gson().fromJson<CardUserModel>(success.obj.toString(), CardUserModel::class.java)
                if (key != null) {
                    user_img = ImgUtils().base64ToBitmap(key.PersonFaceImage)
                    model.VehicleTakePersonCompare = ""
                    card_user_iv.setImageBitmap(user_img)
                    check_two_img()
                    model.VehicleTakePerson = key.PersonName
                    model.VehicleTakePersonCertNumber = key.IdentyNumber
                    model.VehicleTakePersonAddress = key.PersonAddress
                    binding.model = model
                } else {
                    toast("识别失败")
                    dialog!!.dismiss()
                }
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
                        model.VehicleTakePerson = key.PersonName
                        model.VehicleTakePersonCertNumber = key.IdentyNumber
                        model.VehicleTakePersonAddress = key.PersonAddress
                        binding.model = model
                    } else {
                        toast("识别失败")
                    }
                }
                dialog!!.dismiss()
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        next_btn.isEnabled = true
        dialog!!.dismiss()
    }

    /**
     * 两张图片进行比对操作
     * */
    fun check_two_img() {
        if (xc_img != null && user_img != null) {
            dialog!!.show()
            var bit1 = ImgUtils().Only_bitmapToBase64(xc_img)
            var bit2 = ImgUtils().Only_bitmapToBase64(user_img)
            control!!.check_two_imgs(bit1, bit2)
            var bi1 = ImgUtils().base64ToBitmap(bit1)
            var bi2 = ImgUtils().base64ToBitmap(bit2)
            real_user_iv.setImageBitmap(bi1)
            card_user_iv.setImageBitmap(bi2)
        } else {
            dialog!!.dismiss()
        }
    }

    var mFile: File? = null
    fun getMediaFileUri(): Uri? {
        var mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "相册名字");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        var mediaFile: File = File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return Uri.fromFile(mediaFile);
    }

    var model: VehicleModel = VehicleModel()
    var control: CarManageModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        vehicleId = intent.getStringExtra("vehicleId")
        model.VehicleId = vehicleId
        model.VehicleTakeState = "02"
        control = getModule(CarManageModule::class.java, this)
        //证件识别操作
        read_card_btn.setOnClickListener {
            //身份证读取
//            if (id_card_rb.isChecked) {
//
//            } else {//orc读取驾驶证
//            }
        }
        //蓝牙识别
        read_card_btn.setOnClickListener {
            model.VehicleTakePersonCertType = "1"
            if (TextUtils.isEmpty(Utils.getCache(sp.blueToothAddress))) {
                toast("请检查蓝牙读卡设备设置！")
            } else {
                OnBnRead()
            }
        }
        //同送车人
        same_people_tv.setOnClickListener {
            dialog!!.setTitle("加载中，请稍后...")
            dialog!!.show()
            control!!.get_vehicleById(model.VehicleId)
        }
//        read_ocr_btn.setOnClickListener {
//            model.VehicleTakePersonCertType = "1"
//            startActivityForResult(Intent(this@AddGetCarActivity, CameraPersonActivity::class.java)
//                    .putExtra("position", "2"), 1)
//        }
//        read_driver_btn.setOnClickListener {
//            model.VehicleTakePersonCertType = "2"
//            startActivityForResult(Intent(this@AddGetCarActivity, CameraPersonActivity::class.java)
//                    .putExtra("position", "3"), 2)//驾驶证识别
//        }
        read_ocr_btn.setOnClickListener {
            if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
                mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                    var jd = uu.readPictureDegree(outputFile.getAbsolutePath())//获得旋转角度
                    var bmp = uu.compressImage(uu.rotaingImageView(jd, uu.compressImage(uu.getimage(100, outputFile.absolutePath))))
                    dialog!!.show()
                    control!!.ocr_sfz(bmp)
                }, false)
                mLqrPhotoSelectUtils!!.takePhoto()
            } else {
                startActivityForResult(Intent(this@AddGetCarActivity, CameraPersonActivity::class.java)
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
                                        intent = Intent(this@AddGetCarActivity, Camera2Activity::class.java)
                                    } else {
                                        AlertDialog.Builder(this@AddGetCarActivity)
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
                                    intent = Intent(this@AddGetCarActivity, CameraActivity::class.java)
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
        binding.model = model
        //跳转到拍照页面
        real_user_iv.setOnClickListener {
            if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
                mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                    var jd = uu.readPictureDegree(outputFile.getAbsolutePath())//获得旋转角度
                    var bmp = uu.compressImage(uu.rotaingImageView(jd, uu.compressImage(uu.getimage(100, outputFile.absolutePath))))
                    xc_img = bmp
                    real_user_iv.setImageBitmap(xc_img)
                    model.VehicleTakePersonCompare = ""
                    check_two_img()
                }, false)
                mLqrPhotoSelectUtils!!.takePhoto()
            } else {
                model.VehiclePersonCertType = "1"
                startActivityForResult(Intent(this@AddGetCarActivity, CameraPersonActivity::class.java)
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
                                        intent = Intent(this@AddGetCarActivity, Camera2Activity::class.java)
                                    } else {
                                        AlertDialog.Builder(this@AddGetCarActivity)
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
                                    intent = Intent(this@AddGetCarActivity, CameraActivity::class.java)
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
            model.VehiclePersonCertType = "2"
            if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
                mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                    var jd = uu.readPictureDegree(outputFile.getAbsolutePath())//获得旋转角度
                    var bmp = uu.compressImage(uu.rotaingImageView(jd, uu.compressImage(uu.getimage(100, outputFile.absolutePath))))
                    dialog!!.show()
                    control!!.ocr_js(bmp)
                }, false)
                mLqrPhotoSelectUtils!!.takePhoto()
            } else {
                startActivityForResult(Intent(this@AddGetCarActivity, CameraPersonActivity::class.java)
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
                                        intent = Intent(this@AddGetCarActivity, Camera2Activity::class.java)
                                    } else {
                                        AlertDialog.Builder(this@AddGetCarActivity)
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
                                    intent = Intent(this@AddGetCarActivity, CameraActivity::class.java)
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
                }
            }
        }
        //跳转到拍照页面
//        real_user_iv.setOnClickListener {
//            //control!!.get_vehicleByIdCard("130624198709183414")
//            startActivityForResult(Intent(this@AddGetCarActivity, CameraPersonActivity::class.java)
//                    .putExtra("position", "1"), 77)
//        }
        //执行保存操作
        next_btn.setOnClickListener {
            if (check_null()) {
                next_btn.isEnabled = false
                var img_list = ArrayList<FileModel>()
                img_list.add(FileModel(ImgUtils().Only_bitmapToBase64(xc_img), "取车人实际照片", "C5", ""))
                img_list.add(FileModel(ImgUtils().Only_bitmapToBase64(user_img), "取车人证件照片", "C4", ""))
                model.files = img_list
                model.VehicleTakeTime = Utils.normalTime
                dialog!!.show()
                control!!.add_prat(model)
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
        if (TextUtils.isEmpty(model.VehicleTakePerson)) {
            toast("送车人姓名不能为空")
            return false
        } else if (TextUtils.isEmpty(model.VehicleTakePersonCertNumber)) {
            toast("送车人证件号码不能为空")
            return false
        } else if (TextUtils.isEmpty(model.VehicleTakePersonAddress)) {
            toast("家庭住址不能为空")
            return false
        } else if (TextUtils.isEmpty(model.VehicleTakePersonCompare)) {
            toast("请先进行人像比对")
            return false
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
                model.VehicleTakePersonNation = mo.ID
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
                    model!!.VehicleTakePersonNation = nation_list!![which].ID
                    binding.nation = nation_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.setTitle(R.string.please_selected_nation)
        builder.setNegativeButton("确定") { a, b -> }
        builder.create().show()
    }

    var mLqrPhotoSelectUtils: LQRPhotoSelectUtils? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Utils.getCache(sp.sys_camera_show) == "√" && android.os.Build.VERSION.SDK_INT <= 23) {
            mLqrPhotoSelectUtils!!.attachToActivityForResult(requestCode, resultCode, data)
        } else {
            //拍照成功将图片显示出来
            if (resultCode == 66) {
                when (requestCode) {
                    1 -> {//身份证识别
                        var url = data!!.getStringExtra("data")
                        var bmp = uu.compressImage(uu.rotaingImageView(90, uu.compressImage(uu.getimage(100, url))))
                        dialog!!.show()
                        control!!.ocr_sfz(bmp)
                    }
                    2 -> {//驾驶证识别
                        var url = data!!.getStringExtra("data")
                        var bmp = uu.compressImage(uu.rotaingImageView(90, uu.compressImage(uu.getimage(100, url))))
                        dialog!!.show()
                        control!!.ocr_js(bmp)
                    }
                    77 -> {
                        var url = data!!.getStringExtra("data")
                        var bmp = uu.compressImage(uu.rotaingImageView(90, uu.compressImage(uu.getimage(100, url))))
                        xc_img = bmp
                        real_user_iv.setImageBitmap(xc_img)
                        model.VehicleTakePersonCompare = ""
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
                        xc_img = uu.rotaingImageView(90, bit)
                        real_user_iv.setImageBitmap(xc_img)
                        model.VehicleTakePersonCompare = ""
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
        dialog!!.show()
        //builder.show()
        if (null == idCardReader) {
            val device = mBluetoothAdapter!!.getRemoteDevice(Utils.getCache(sp.blueToothAddress))
            try {
                Thread() { connect(device) }.start()
            } catch (e: Exception) {
                Toast.makeText(this@AddGetCarActivity, "连接失败", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
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
                    Toast.makeText(this@AddGetCarActivity, "连接成功", Toast.LENGTH_SHORT).show()
                } else {
                    idCardReader = null
                    dialog!!.dismiss()
                    Toast.makeText(this@AddGetCarActivity, "连接失败，错误码:" + ret, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
            idCardReader = null
            dialog!!.dismiss()
            Toast.makeText(this@AddGetCarActivity, "连接失败", Toast.LENGTH_SHORT).show()
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
                            dialog!!.dismiss()
                            Toast.makeText(this@AddGetCarActivity, "连接失败", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (isRead) {
                            if (!ReadCardInfo()) {
                                index++
                                if (index == 9) {
                                    dialog!!.dismiss()
                                    Toast.makeText(this@AddGetCarActivity, "读卡失败", Toast.LENGTH_SHORT).show()
                                }
                                //textView.post(Runnable { textView.setText("请放卡...") })

                            } else {
                                //textView.post(Runnable { textView.setText("读卡成功，请放入下一张卡") })
                                index = 11
                                dialog!!.dismiss()
                            }
                        }
                    }
                }
            }

        }
    }

    //读卡操作
    fun ReadCardInfo(): Boolean {

        if (idCardReader != null && !idCardReader!!.sdtFindCard()) {
            dialog!!.dismiss()
            return false
        } else {
            if (!idCardReader!!.sdtSelectCard()) {
                dialog!!.dismiss()
                return false
            }
        }
        runOnUiThread {
            //textView.setText("正在读卡...")
            //resetContent()
        }
        val idCardInfo = IDCardInfo()
        if (idCardReader!!.sdtReadCard(1, idCardInfo)) {
            val time = System.currentTimeMillis()
            val mCalendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            runOnUiThread {
                model.VehicleTakePerson = idCardInfo.name
                model.VehicleTakePersonCertNumber = idCardInfo.id
                model.VehicleTakePersonAddress = idCardInfo.address
                binding.nation = idCardInfo.nation
                binding.model = model
                //                employee!!.EmployeeName = idCardInfo.name
//                employee!!.EmployeeCertNumber = idCardInfo.id
//                when (idCardInfo.sex) {
//                    "男" -> {
//                        employee!!.EmployeeSex = "1"
//                    }
//                    else -> {
//                        employee!!.EmployeeSex = "2"
//                    }
//                }
//                employee!!.EmployeeAddress = idCardInfo.address
//                binding.model = employee
                isRead = false
            }


            if (idCardInfo.photo != null) {
                val buf = ByteArray(WLTService.imgLength)
                if (1 == WLTService.wlt2Bmp(idCardInfo.photo, buf)) {
                    user_img = IDPhotoHelper.Bgr2Bitmap(buf)
                    if (null != user_img) {
                        model.VehicleTakePersonCompare = ""
                        card_user_iv.post(Runnable { card_user_iv.setImageBitmap(user_img) })
                        check_two_img()
                    }
                }
            }
            return true
        } else {
            //playSound(9, 0);
        }
        //textView.post(Runnable { textView.setText("读卡失败...") })
        dialog!!.dismiss()
        return false
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
                        Toast.makeText(this@AddGetCarActivity, "连接失败", Toast.LENGTH_SHORT).show()
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
//        checkPermission()
    }

    //页面关闭处理
    override fun onDestroy() {
        super.onDestroy()
        dialog!!.dismiss()
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
        //Toast.makeText(this@AddGetCarActivity, "断开设备成功", Toast.LENGTH_SHORT).show()
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
        return R.layout.activity_add_get_car
    }
}