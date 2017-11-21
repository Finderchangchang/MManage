package gd.mmanage.ui.config

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivitySetBinding
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel
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