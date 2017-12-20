package gd.mmanage.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gd.mmanage.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dd.*
import java.io.File
import java.util.ArrayList

class DDActivity : AppCompatActivity() {
    var mFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dd)
        btn.setOnClickListener {
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
                                intent = Intent(this@DDActivity, Camera2Activity::class.java)
                            } else {
                                AlertDialog.Builder(this@DDActivity)
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
                            startActivityForResult(intent, 1)
                        }

                        override fun onPermissionDenied(arrayList: ArrayList<String>) {}
                    }).setPermissions(*arrayOf(Manifest.permission.CAMERA)).check()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFile = CommonUtils.createImageFile("mFile")

    }
}
