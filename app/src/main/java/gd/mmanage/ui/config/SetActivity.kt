package gd.mmanage.ui.config

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.sp
import gd.mmanage.databinding.ActivitySetBinding
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel
import gd.mmanage.service.DownConfigService
import kotlinx.android.synthetic.main.activity_set.*
import java.util.ArrayList

/**
 * 查询页面功能
 * */
class SetActivity : BaseActivity<ActivitySetBinding>() {
    var listblue: MutableList<CodeModel>? = null
    var blue_array: Array<String?>? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        title_bar.setLeftClick { finish() }
        //蓝牙选择
        blue_set_mv.setOnClickListener {
            getBluetooth()
        }
        blue_set_mv.setRight_tv(Utils.getCache(sp.blueToothName))
        update_pwd_mv.setOnClickListener {
            startActivity(Intent(this, UpdatePwdActivity::class.java))
        }
        update_code_mv.setOnClickListener {
            startService(Intent(this, DownConfigService::class.java))
        }
        version_mv.setRight_tv(Utils.version)
        open_sys_camera_mv.setRight_tv(Utils.getCache(sp.sys_camera_show))
        open_sys_camera_mv.setOnClickListener {
            var show_result = Utils.getCache(sp.sys_camera_show)
            when (show_result) {
                "×" -> {//打开系统相机
                    var builder = AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("系统拍照功能将打开，请确保拍照方向");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定") { a, b ->
                        open_sys_camera_mv.setRight_tv("√")
                        Utils.putCache(sp.sys_camera_show, "√")
                    }
                    builder.show();
                }
                "√" -> {//关闭系统相机
                    var builder = AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("您将关闭系统拍照功能将打开，请确保关闭以后，可以拍照");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定") { a, b ->
                        open_sys_camera_mv.setRight_tv("×")
                        Utils.putCache(sp.sys_camera_show, "×")
                    }
                    builder.show();
                }
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_set
    }

    /**
     * 获得蓝牙的相关信息
     */
    private fun getBluetooth() {
        val mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBtAdapter == null) {
            toast("不支持蓝牙设备！")
        } else {
            val pairedDevices = mBtAdapter.bondedDevices
            if (pairedDevices.size == 0) {
                toast("没有配对蓝牙设备！")
                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }
            listblue = ArrayList<CodeModel>()
            blue_array = arrayOfNulls(pairedDevices.size)
            for ((i, device) in pairedDevices.withIndex()) {
                listblue!!.add(CodeModel(device.address, device.name))
                blue_array!![i] = device.name
            }
            if (listblue!!.size > 0) {
                builder.setTitle("选择蓝牙")
                builder.setItems(blue_array) { _, position ->
                    blue_set_mv.setRight_tv(blue_array!![position])
                    Utils.putCache("BlueToothName", listblue!![position].Name)
                    Utils.putCache("BlueToothAddress", listblue!![position].ID)
                }
                builder.show()
            }
        }
    }
}