package gd.mmanage.ui.employee

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
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
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.control.CarManageModule
import gd.mmanage.control.EmployeeModule
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.UtilControl
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.method.uu.compressImage
import gd.mmanage.model.*
import gd.mmanage.ui.CameraPersonActivity
import gd.mmanage.ui.DemoActivity

import kotlinx.android.synthetic.main.activity_add_employee.*
import net.tsz.afinal.FinalDb
import net.tsz.afinal.view.DatePickerDialog
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 添加从业人员信息
 * */
class AddEmployeeActivity : BaseActivity<ActivityAddEmployeeBinding>(), AbsModule.OnCallback {
    private var control: EmployeeModule? = null
    var is_add = true//true:添加。false：删除
    var employee: EmployeeModel? = null//传递过来的从业人员信息
    var dialog: LoadingDialog.Builder? = null

    override fun onSuccess(result: Int, success: Any?) {
        save_btn.isEnabled = true
        if (result == command.employee + 2) {
            success as NormalRequest<*>
            if (success.obj != null) {
                var model: EmployeeModel = Gson().fromJson(success.obj.toString(), EmployeeModel::class.java)
                if (model.file != null && !TextUtils.isEmpty(model.file!!.FileContent)) {
                    user_iv.setImageBitmap(ImgUtils().base64ToBitmap(model.file!!.FileContent))
                }
            }
        } else if (result == command.car_manage + 11) {
            success as NormalRequest<*>
            if (success.obj != null) {

                var key = Gson().fromJson<CardUserModel>(success.obj.toString(), CardUserModel::class.java)
                if (key != null) {
                    user_bitmap = ImgUtils().base64ToBitmap(key.PersonFaceImage)
                    user_iv.setImageBitmap(user_bitmap)
                    employee!!.EmployeeName = key.PersonName
                    employee!!.EmployeeCertNumber = key.IdentyNumber
                    if (!TextUtils.isEmpty(key.IdentyNumber)) {
                        if (key.IdentyNumber.length == 18) {
                            var nu = key.IdentyNumber.substring(16, 17)
                            employee!!.EmployeeSex = nu
                        }
                    }
                    employee!!.EmployeeAddress = key.PersonAddress
                    binding.model = employee
                } else {
                    toast("识别失败")
                }
            }
            dialog!!.dismiss()
        } else {
            //添加
            success as NormalRequest<*>
            when (success.code) {
                0 -> {
                    when (result) {
                        command.employee -> toast("添加成功")
                        command.employee + 1 -> toast("修改成功")
                    }
                    setResult(2)
                    finish()
                }
                else -> toast(success.message)
            }
            dialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            66 -> {
                var url = data!!.getStringExtra("data")
                var bmp = compressImage(uu.rotaingImageView(90, compressImage(uu.getimage(100, url))))
                dialog!!.show()
                getModule(CarManageModule::class.java, this).ocr_sfz(bmp)
            }
            200 -> {
                try {
                    var bit = BitmapUtils.compressToResolution(mFile, 1920 * 1080)
                    getModule(CarManageModule::class.java, this).ocr_sfz(bit)
                    dialog!!.show()
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        toast(error as String)
        dialog!!.dismiss()
        save_btn.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    var mFile: File? = null

    var is_cc = 0
    var db: FinalDb? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)//创建数据库查询实例
        employee = intent.getSerializableExtra("model") as EmployeeModel
        is_add = employee != null
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        is_cc = intent.getIntExtra("is_cc", 0)
        employee!!.EnterpriseId = Utils.getCache(sp.company_id)
        binding.model = employee//数据绑定操作
        control = getModule(EmployeeModule::class.java, this)
        //读卡操作
        read_card_btn.setOnClickListener {
            if (TextUtils.isEmpty(Utils.getCache(sp.blueToothAddress))) {
                toast("请检查蓝牙读卡设备设置！")
            } else {
                dialog!!.show()
                OnBnRead()
            }
        }
        read_ocr_btn.setOnClickListener {
            //            startActivityForResult(Intent(this@AddEmployeeActivity, CameraPersonActivity::class.java)
//                    .putExtra("position", "2"), 1)
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
                                    intent = Intent(this@AddEmployeeActivity, Camera2Activity::class.java)
                                } else {
                                    AlertDialog.Builder(this@AddEmployeeActivity)
                                            .setTitle("不支持的 API Level")
                                            .setMessage("Camera2 API 仅在 API Level 21 以上可用, 当前 API Level : " + Build.VERSION.SDK_INT)
                                            .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
                                            .show()
                                    return
                                }
                                mFile = CommonUtils.createImageFile(System.currentTimeMillis().toString() + ".jpg")
                                //文件保存的路径和名称
                                intent.putExtra("file", mFile.toString())
                                //拍照时的提示文本
                                intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像")
                                //是否使用整个画面作为取景区域(全部为亮色区域)
                                intent.putExtra("hideBounds", false)
                                //最大允许的拍照尺寸（像素数）
                                intent.putExtra("maxPicturePixels", 3840 * 2160)
                                startActivityForResult(intent, 1)
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
                                intent = Intent(this@AddEmployeeActivity, CameraActivity::class.java)
                                mFile = CommonUtils.createImageFile("mFile")
                                //文件保存的路径和名称
                                intent.putExtra("file", mFile.toString())
                                //拍照时的提示文本
                                intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像")
                                //是否使用整个画面作为取景区域(全部为亮色区域)
                                intent.putExtra("hideBounds", false)
                                //最大允许的拍照尺寸（像素数）
                                intent.putExtra("maxPicturePixels", 3840 * 2160)
                                startActivityForResult(intent, 1)
                            }

                            override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
                        }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
            }
        }
        when (intent.getIntExtra("is_cc", 0)) {
            1 -> {
                title_btn_ll.visibility = View.GONE
                save_btn.visibility = View.GONE
                read_card_btn.visibility = View.GONE
                title_bar.center_Tv.text = "从业人员信息"
                control!!.get_employee(employee!!.EmployeeId)
            }
            0 -> {
                //ll1.visibility = View.GONE//添加隐藏编号
                title_bar.center_Tv.text = "从业人员添加"
            }
            2 -> {
                title_bar.center_Tv.text = "从业人员修改"
                control!!.get_employee(employee!!.EmployeeId)
            }
        }
        //性别选择
        ll3.setOnClickListener { if (is_cc != 1) dialog(arrayOf("男", "女"), 1) }
        //人员状态
        ll8.setOnClickListener { if (is_cc != 1) dialog(zt_array!!, 2) }
        //添加从业人员
        save_btn.setOnClickListener {
            if (check_null()) {
                save_btn.isEnabled = false
                dialog!!.show()
                var map = HashMap<String, String>()
                var model = binding.model
                model.EmployeeEntryDate = Utils.normalTime
                map = UtilControl.change(model)
                if (user_bitmap != null) {
                    var img_list = ArrayList<FileModel>()
                    img_list.add(FileModel(ImgUtils().Only_bitmapToBase64(user_bitmap), "从业人员证件照", "1", model.EmployeeId))
                    map.put("files", Gson().toJson(img_list))
                }
                control!!.add_employee(map)
            }
        }
        ll4.setOnClickListener { if (is_cc != 1) dialog(mz_array!!, 3) }
        //init_blue()
        initNation()
    }

    var mz_array: Array<String?>? = null//民族的集合
    var zt_array: Array<String?>? = null//人员状态的集合

    /**
     * 加载民族的字典
     * */
    fun initNation() {
        var nation_id = "01"
        if (!TextUtils.isEmpty(employee!!.EmployeeNation)) {
            nation_id = employee!!.EmployeeNation
        }
        var zt_id = "01"
        if (!TextUtils.isEmpty(employee!!.EmployeeState)) {
            zt_id = employee!!.EmployeeState
        }
        nation_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_Nation'")
        mz_array = arrayOfNulls(nation_list!!.size)
        for (id in 0 until nation_list!!.size) {
            var model = nation_list!![id]
            mz_array!![id] = model.Name
            if (nation_id == model.ID) {
                binding.nation = model.Name
            }
        }
        zt_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_EmployeeState'")
        zt_array = arrayOfNulls(zt_list!!.size)
        for (id in 0 until zt_array!!.size) {
            var model = zt_list!![id]
            zt_array!![id] = model.Name
            if (zt_id == model.ID) {
                binding.state = model.Name
                binding.model.EmployeeState = model.ID
            }
        }

    }

    var nation_list: List<CodeModel>? = null
    var zt_list: List<CodeModel>? = null

    /**
     * 弹出的列表选择框
     * */
    fun dialog(key: Array<String?>, method: Int) {
        //dialog参数设置
        val builder = AlertDialog.Builder(this)  //先得到构造器
        builder.setItems(key) { dialog, which ->
            when (method) {
                1 -> {//性别
                    when (which) {
                        1 -> tv3.text = "女"
                        else -> tv3.text = "男"
                    }
                    employee!!.EmployeeSex = (which + 1).toString()
                }
                2 -> {//人员状态
                    employee!!.EmployeeState = zt_list!![which].ID
                    tv8.text = zt_list!![which].Name
                }
                3 -> {//民族
                    employee!!.EmployeeNation = nation_list!![which].ID
                    binding.nation = nation_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.create().show()
    }

    internal var idCardReader: IDCardReader? = null
    private var workThread: WorkThread? = null
    public var isRead = false
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    //设备连接
    fun OnBnRead() {
        dialog!!.show()
        val connectThread = ConnectThread()
        connectThread.start()
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

    //搜索结果处理
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            val action = arg1.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device = arg1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device.address == Utils.getCache(sp.blueToothAddress)) {//DC:0D:30:04:20:D9
                    //0064 00:13:04:84:00:64
                    try {
                        //connect(device)
                    } catch (e: Exception) {
                        Toast.makeText(this@AddEmployeeActivity, "连接失败", Toast.LENGTH_SHORT).show()
                    }

                }
            } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                // setProgressBarIndeterminateVisibility(false);
                title = "搜索蓝牙设备"
            }
        }
    }

    private inner class ConnectThread : Thread() {
        override fun run() {
            super.run()
            Looper.prepare()
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (!mBluetoothAdapter!!.isEnabled()) {
                mBluetoothAdapter!!.enable()
            }
            try {
                val ad = Utils.getCache(sp.blueToothAddress)
                val device = mBluetoothAdapter!!.getRemoteDevice(ad)

                connect(device)
            } catch (e: Exception) {
                // TODO: handle exception
                mHandler.sendMessage(mHandler.obtainMessage(1, "连接失败"))
                dialog!!.dismiss()
            }

        }
    }

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    Toast.makeText(this@AddEmployeeActivity, msg.obj as String, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun connect(device: BluetoothDevice) {
        try {
            idCardReader = IDCardReader()
            idCardReader!!.setDevice(device.address)
            var ret = idCardReader!!.openDevice()
            if (IDCardReader.ERROR_SUCC === ret) {
                //progressDialog.dismiss();
                //Toast.makeText(AddEmployeeActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                // ReadCardInfo();
                index = 0
                workThread = WorkThread()
                workThread!!.start()// 线程启动
            } else {
                idCardReader = null
                dialog!!.dismiss()
                mHandler.sendMessage(mHandler.obtainMessage(1, "连接失败，请重启蓝牙身份证阅读器"))
            }
        } catch (e: Exception) {
            // TODO: handle exception
            idCardReader = null
            dialog!!.dismiss()
            mHandler.sendMessage(mHandler.obtainMessage(1, "连接失败"))
        }

    }

    var index = 0

    private inner class WorkThread : Thread() {
        override fun run() {
            super.run()
            while (index < 10) {
                runOnUiThread {
                    if (!ReadCardInfo()) {
                        index++
                        if (index == 9) {
                            dialog!!.dismiss()
                            idCardReader!!.closeDevice()
                            idCardReader = null
                            mHandler.sendMessage(mHandler.obtainMessage(1, "读卡失败"))
                        } else {
                            //Toast.makeText(AddEmployeeActivity.this,"请放卡...",Toast.LENGTH_SHORT);
                        }
                    } else {
                        index = 11
                        mHandler.sendMessage(mHandler.obtainMessage(1, "读卡成功"))
                    }
                }

                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()//1141112223
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
                        employee!!.EmployeeName = idCardInfo.name
                        employee!!.EmployeeCertNumber = idCardInfo.id
                        when (idCardInfo.sex) {
                            "男" -> {
                                employee!!.EmployeeSex = "1"
                            }
                            else -> {
                                employee!!.EmployeeSex = "2"
                            }
                        }
                        employee!!.EmployeeAddress = idCardInfo.address
                        binding.model = employee
                        isRead = false
                    }


                    if (idCardInfo.photo != null) {
                        val buf = ByteArray(WLTService.imgLength)
                        if (1 == WLTService.wlt2Bmp(idCardInfo.photo, buf)) {
                            user_bitmap = IDPhotoHelper.Bgr2Bitmap(buf)
                            if (null != user_bitmap) {
                                user_iv.post(Runnable { user_iv.setImageBitmap(user_bitmap) })
                                dialog!!.dismiss()
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

    var user_bitmap: Bitmap? = null

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

    //检测权限
    private fun checkPermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            var s = ""
        }
    }

    //权限管理
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
        } else if (requestCode == 1) {

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * 检测必填字段
     * @return true 全部通过
     * */
    private fun check_null(): Boolean {
        return when {
            Utils.etIsNull(et2) -> {
                toast("姓名不能为空")
                false
            }
            Utils.etIsNull(et5) -> {
                toast("身份证号不能为空")
                false
            }
            Utils.etIsNull(et6) -> {
                toast("详细地址不能为空")
                false
            }
            Utils.etIsNull(et7) -> {
                toast("联系电话不能为空")
                false
            }
            Utils.isChinaPhoneLegal(et7.text.toString().trim()) -> {
                toast("请输入正确的手机号")
                false
            }
            else -> true
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_employee
    }

    //页面关闭处理
    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(mReceiver)
//        OnBnDisconn()
    }
}
