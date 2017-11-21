package gd.mmanage.ui.employee

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arialyy.frame.module.AbsModule
import com.google.gson.JsonElement
import com.zkteco.android.IDReader.IDPhotoHelper
import com.zkteco.android.IDReader.WLTService
import com.zkteco.id3xx.IDCardReader
import com.zkteco.id3xx.meta.IDCardInfo
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.EmployeeModule
import gd.mmanage.control.LoginModule
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityLoginBinding
import gd.mmanage.method.UtilControl
import gd.mmanage.method.Utils
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest

import kotlinx.android.synthetic.main.activity_add_employee.*
import net.tsz.afinal.FinalDb
import java.util.*

/**
 * 添加从业人员信息
 * */
class AddEmployeeActivity : BaseActivity<ActivityAddEmployeeBinding>(), AbsModule.OnCallback {
    private var control: EmployeeModule? = null
    var is_add = true//true:添加。false：删除
    var employee: EmployeeModel? = null//传递过来的从业人员信息
    override fun onSuccess(result: Int, success: Any?) {
        //添加
        success as NormalRequest<*>
        when (success.code) {
            0 -> {
                when (result) {
                    command.employee -> toast("添加成功")
                    command.employee + 1 -> toast("修改成功")
                }
                finish()
            }
            else -> toast(success.message)
        }
    }

    override fun onError(result: Int, error: Any?) {
        toast(error as String)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }


    var db: FinalDb? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)//创建数据库查询实例
        employee = intent.getSerializableExtra("model") as EmployeeModel
        is_add = employee != null
        if (is_add) ll1.visibility = View.GONE//添加隐藏编号
        employee!!.EmployeeCertType = "1"
        employee!!.EmployeePhone = "17093215800"
        employee!!.EmployeeState = "1"
        employee!!.EmployeeEntryDate = "2017-11-12"
        binding.model = employee//数据绑定操作
        control = getModule(EmployeeModule::class.java, this)
        title_bar.setLeftClick { finish() }
        //读卡操作
        read_card_btn.setOnClickListener {
            if (Utils.getCache("BlueToothAddress") == "") {
                toast("请检查蓝牙读卡设备设置！")
            } else {
                var progressDialog = ProgressDialog.show(this, "", "正在读取身份证信息...", true, false)
                progressDialog!!.setCancelable(true)
                progressDialog.setCanceledOnTouchOutside(true)
                //progressDialog.show()
                OnBnRead()
            }
        }
        //性别选择
        ll3.setOnClickListener {
            builder.setItems(arrayOf("男", "女")) { _, position ->
                employee!!.EmployeeSex = position.toString()
                when (position) {
                    1 -> tv3.text = "女"
                    else -> tv3.text = "男"
                }
            }
            builder.show()
        }
        //户籍选择
        ll5.setOnClickListener {

        }
        //文化程度选择
        ll6.setOnClickListener {

        }
        //入职时间选择
        ll8.setOnClickListener {

        }
        //添加从业人员
        save_btn.setOnClickListener {
            if (check_null()) {
                control!!.add_employee(UtilControl.change(binding.model))
            }
        }
    }

    internal var idCardReader: IDCardReader? = null
    private var workThread: WorkThread? = null
    var isRead = false
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    //设备连接
    fun OnBnRead() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = mBluetoothAdapter!!.getRemoteDevice(Utils.getCache("BlueToothAddress"))
        try {
            connect(device)

        } catch (e: Exception) {
            Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show()
        }


    }

    private fun connect(device: BluetoothDevice) {
        try {
            idCardReader = IDCardReader()
            idCardReader!!.setDevice(device.address)
            var ret = idCardReader!!.openDevice()
            if (IDCardReader.ERROR_SUCC == ret) {
//                textView.setText(bluetooth)
//                dialog.dismiss()
                Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show()
                if (null == idCardReader) {
                    Toast.makeText(this@AddEmployeeActivity, "请先连接设备", Toast.LENGTH_SHORT).show()
                    return
                }
                workThread = WorkThread()
                workThread!!.start()// 线程启动
                isRead = true
            } else {
                idCardReader = null
                Toast.makeText(this, "连接失败，错误码:" + ret, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // TODO: handle exception
            idCardReader = null
            Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show()
        }

    }

    private inner class WorkThread : Thread() {
        override fun run() {
            super.run()
            if (isRead) {
                if (!ReadCardInfo()) {
                    //textView.post(Runnable { textView.setText("请放卡...") })
                    var s = ""
                } else {
                    //textView.post(Runnable { textView.setText("读卡成功，请放入下一张卡") })
                    var s = ""

                }
            }
        }
    }

    //读卡操作
    private fun ReadCardInfo(): Boolean {
        if (!idCardReader!!.sdtFindCard()) {
            return false
        } else {
            if (!idCardReader!!.sdtSelectCard()) {
                return false
            }
        }
        runOnUiThread {

        }
        val idCardInfo = IDCardInfo()
        if (idCardReader!!.sdtReadCard(1, idCardInfo)) {
            val time = System.currentTimeMillis()
            val mCalendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            runOnUiThread {
                //                infoName.setText(idCardInfo.name)
//                infoSex.setText(idCardInfo.sex)
//                infoNation.setText(idCardInfo.nation)
//                infoBirth.setText(idCardInfo.birth)
//                infoAddress.setText(idCardInfo.address)
//                infoIdcard.setText(idCardInfo.id)
//                infoCertifying.setText(idCardInfo.depart)
//                infoData.setText(idCardInfo.validityTime)
                isRead = false
            }


            if (idCardInfo.photo != null) {
                val buf = ByteArray(WLTService.imgLength)
                if (1 == WLTService.wlt2Bmp(idCardInfo.photo, buf)) {
                    val bitmap = IDPhotoHelper.Bgr2Bitmap(buf)
                    if (null != bitmap) {
                        //image.post(Runnable { image.setImageBitmap(bitmap) })

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
            Utils.etIsNull(et4) -> {
                toast("身份证号不能为空")
                false
            }
            Utils.etIsNull(et7) -> {
                toast("详细地址不能为空")
                false
            }
            else -> true
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_employee
    }
}
