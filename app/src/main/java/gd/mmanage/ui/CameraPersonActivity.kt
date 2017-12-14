package gd.mmanage.ui

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil.setContentView
import android.databinding.repacked.org.antlr.v4.runtime.misc.MurmurHash.finish
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.CameraSurfaceView
import kotlinx.android.synthetic.main.activity_demo1.*
import net.tsz.afinal.view.LoadingDialog
import java.util.ArrayList
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


/**正方形框图片
 *
 * Created by Administrator on 2017/8/3.
 */

class CameraPersonActivity : AppCompatActivity(), CameraSurfaceView.onScan {
    private val REQUEST_CAMERA_PERMISSIONS = 931

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo1)
        var position = intent.getStringExtra("position")
        when (position) {
            "1" -> {//人脸照（竖版）
                iv.setImageResource(R.mipmap.renlian)
            }
            "2" -> {//证件照(横版)
                iv.setImageResource(R.mipmap.card_bg)
            }
        }
        record_button.setOnClickListener {
            record_button.isClickable = false
            main_cv.takePicture(this)
        }
        onAddCameraClicked()
    }

    override fun get(url: String?) {
        //返回人员头像
        if (url != null) {
            var myintent = intent
            myintent.putExtra("data", url)
            setResult(66, myintent)
        } else {

        }
        finish()
    }

    override fun onResume() {
        /**
         * 设置为横屏
         */
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onResume()
    }

    fun onAddCameraClicked() {
        if (Build.VERSION.SDK_INT > 15) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

            val permissionsToRequest = ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CAMERA_PERMISSIONS)
            }

        }
    }

    val FRAGMENT_TAG = "camera"

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {

        }
    }

}
