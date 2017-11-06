package gd.mmanage.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.inten
import gd.mmanage.databinding.ActivityMainBinding
import gd.mmanage.method.Utils

class MainActivity : BaseActivity<ActivityMainBinding>() {
    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    var mBluetoothAdapter: BluetoothAdapter? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        if (!TextUtils.isEmpty(Utils.getCache(inten.blue_tooth))) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (!mBluetoothAdapter!!.isEnabled) {
                mBluetoothAdapter!!.enable()
            }
        } else {

        }
    }


    /**
     * 检测权限
     * */
    private fun checkPermission(): Boolean {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    /**
     * 权限处理结果
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {

        } else if (requestCode == 1) {

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
