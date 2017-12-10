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
import com.zkteco.android.IDReader.IDPhotoHelper
import com.zkteco.android.IDReader.WLTService
import com.zkteco.id3xx.IDCardReader
import com.zkteco.id3xx.meta.IDCardInfo
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.callback.LzyResponse
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddPersonBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.method.uu.compressImage
import gd.mmanage.model.*
import gd.mmanage.ui.DemoActivity
import kotlinx.android.synthetic.main.activity_add_person.*
import net.tsz.afinal.FinalDb
import java.io.ByteArrayOutputStream
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
    var user_url = ""

    companion object {
        var context: AddPersonActivity? = null
    }

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
                            .putExtra("model", model)
                            .putExtra("xc_url", xc_url)//FileModel(user_img, "送车人证件照", "C2", "")
                            .putExtra("user_file", FileModel(bitmap_to_bytes(user_img!!).toString(), "送车人证件照", "C2", "")))
                }
                builder.show()
            }
            command.car_manage + 8 -> {
                var ss = success as NormalRequest<JsonObject>
                var a = ss.obj as JsonObject
                var key = Gson().fromJson<FaceRecognitionModel>(a, FaceRecognitionModel::class.java)
                result_btn.visibility = View.VISIBLE
                var builder = AlertDialog.Builder(this);
                builder.setTitle("提示");
                if ("false" != key.SamePerson) {//比对为同一人
                    result_btn.text = "成功"
                    model.VehiclePersonCompare = key.FaceScore
                    bi_tv.visibility = View.VISIBLE
                    builder.setNeutralButton("查看名下车辆") { a, b ->
                        control!!.get_vehicleByIdCard(binding.model.VehiclePersonCertNumber)
                    }
                    builder.setMessage("比对通过");
                } else {
                    result_btn.text = "失败"
                    builder.setMessage("比对未通过，相似度为：" + key.FaceScore);
                    model.VehiclePersonCompare = ""
                    bi_tv.visibility = View.GONE
                }
                builder.setNegativeButton("取消", null);

                builder.show();
                bi_tv.text = "识别率 " + model.VehiclePersonCompare
            }
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<*>
                var model = Gson().fromJson<DetailModel>(success.obj.toString(), DetailModel::class.java)
                if (model != null) {

                    if (model.Vehicle!!.files!!.isNotEmpty()) {
                        if (model.Vehicle!!.files!!.size > 2) {
                            card_user_iv.setImageBitmap(ImgUtils().base64ToBitmap(model.Vehicle!!.files!![2].FileContent))
                        }
                        if (model.Vehicle!!.files!!.size > 3) {
                            real_user_iv.setImageBitmap(ImgUtils().base64ToBitmap(model.Vehicle!!.files!![3].FileContent))
                        }
                    }
                }
            }
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

    override fun onError(result: Int, error: Any?) {

    }

    /**
     * 两张图片进行比对操作
     * */
    fun check_two_img() {
        if (xc_img != null && user_img != null) {
            control!!.check_two_img(xc_img!!, user_img!!)
        }
    }

    var model: VehicleModel = VehicleModel()
    var control: CarManageModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        context = this
        control = getModule(CarManageModule::class.java, this)
        model = intent.getSerializableExtra("model") as VehicleModel
        if (!TextUtils.isEmpty(model.VehicleId)) {
            control!!.get_vehicleById(model.VehicleId)
        }
        //证件识别操作
        read_card_btn.setOnClickListener {
            //身份证读取
            if (id_card_rb.isChecked) {
                if (TextUtils.isEmpty(Utils.getCache(sp.blueToothAddress))) {
                    toast("请检查蓝牙读卡设备设置！")
                } else {
                    OnBnRead()
                }
            } else {//orc读取驾驶证
            }
        }
        model.VehiclePersonCertType = "01"
        binding.model = model
        binding.nation = "汉族"
        //跳转到拍照页面
        real_user_iv.setOnClickListener {
            //control!!.get_vehicleByIdCard("130624198709183414")
            startActivityForResult(Intent(this@AddPersonActivity, DemoActivity::class.java)
                    .putExtra("position", "1"), 77)
        }
        next_btn.setOnClickListener {
            if (check_null()) {
                var intent = Intent(this@AddPersonActivity, AddCarActivity::class.java)
                if (user_img != null) {
                    intent.putExtra("user_file", FileModel(Gson().toJson(bitmap_to_bytes(user_img!!)), "送车人证件照", "C2", ""))
                } else {
                    intent.putExtra("user_file", FileModel())
                }
                startActivity(intent
                        .putExtra("model", model)
                        .putExtra("xc_url", xc_url))//FileModel(user_img, "送车人证件照", "C2", "")
            }
        }
        nation_ll.setOnClickListener {
            dialog(mz_array!!, 3)
        }
        init_data()
        init_blue()
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
        }
        return true
    }

    fun init_data() {
        var nation_id = "01"
        nation_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_Nation'")
        mz_array = arrayOfNulls(nation_list!!.size)
        for (id in 0 until nation_list!!.size) {
            var model = nation_list!![id]
            mz_array!![id] = model.Name
            if (nation_id == model.ID) {
                binding.nation = model.Name
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
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //拍照成功将图片显示出来
        if (resultCode == 66) {
            xc_url = data!!.getStringExtra("data")
            var bmp = uu.getimage(100, xc_url)
            xc_img = compressImage(uu.rotaingImageView(90, compressImage(bmp)))
            real_user_iv.setImageBitmap(xc_img)
            check_two_img()
        }
    }

    internal var idCardReader: IDCardReader? = null
    private var workThread: WorkThread? = null
    public var isRead = false
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    //设备连接
    fun OnBnRead() {
        builder.setMessage("加载中...")
        //builder.show()
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
                    Toast.makeText(this@AddPersonActivity, "连接成功", Toast.LENGTH_SHORT).show()
                } else {
                    idCardReader = null
                    Toast.makeText(this@AddPersonActivity, "连接失败，错误码:" + ret, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
            idCardReader = null
            Toast.makeText(this@AddPersonActivity, "连接失败", Toast.LENGTH_SHORT).show()
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

        if (idCardReader != null && !idCardReader!!.sdtFindCard()) {
            return false
        } else {
            if (!idCardReader!!.sdtSelectCard()) {
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
                model.VehiclePerson = idCardInfo.name
                model.VehiclePersonCertNumber = idCardInfo.id
                model.VehiclePersonAddress = idCardInfo.address
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
        checkPermission()
    }

    //页面关闭处理
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        OnBnDisconn()
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
