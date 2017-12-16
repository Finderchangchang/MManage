package gd.mmanage.ui

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.LoginModule
import gd.mmanage.databinding.ActivityLoginBinding

import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import android.databinding.repacked.apache.commons.codec.digest.DigestUtils
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.jaeger.library.StatusBarUtil
import gd.mmanage.model.NormalRequest
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.config.url
import gd.mmanage.method.Sha1
import gd.mmanage.method.UpdateManager
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel
import gd.mmanage.model.UpdateModel
import gd.mmanage.service.DownConfigService
import gd.mmanage.ui.config.DownHotelActivity
import gd.mmanage.ui.main.HomeActivity
import kotlinx.android.synthetic.main.activity_add_parts.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import gd.mmanage.ui.config.RegCompanyActivity


class LoginActivity : BaseActivity<ActivityLoginBinding>(), AbsModule.OnCallback, EasyPermissions.PermissionCallbacks {
    private var control: LoginModule? = null
    var dialog: LoadingDialog.Builder? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this)
        control = getModule(LoginModule::class.java, this)//初始化网络请求
        //StatusBarUtil.setTransparent(this)//设置状态栏颜色
        imei_tv.setOnClickListener {
            //            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            // 将文本内容放到系统剪贴板里。
//            cm.text = Utils.imei
//            Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@LoginActivity, RegCompanyActivity::class.java))
        }
        id_et.setText(Utils.getCache(sp.user_id))
        pwd_et.setText(Utils.getCache(sp.pwd))
        //登录按钮
        login_btn.setOnClickListener {
            var name = id_et.text.toString().trim()
            var pwd = pwd_et.text.toString().trim()
            when {
                TextUtils.isEmpty(name) -> toast("请输入用户名")
                TextUtils.isEmpty(pwd) -> toast("请输入密码")
                else -> {
                    login_btn.isEnabled = false
                    dialog!!.setTitle("登录中，请稍后...")
                    dialog!!.show()
                    control!!.user_login(name, pwd)//登录操作
                }
            }
        }
//        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)) {
//            EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
//                    2, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
//
//        }
        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                    2, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        } else {
            binding.imei = "当前设备IMEI：" + Utils.imei
        }
        //标志不存在，执行下载配置信息操作
        //if (TextUtils.isEmpty(Utils.getCache(sp.down_all))) {
        startService(Intent(this, DownConfigService::class.java))
        //}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        binding.imei = "当前设备IMEI：" + Utils.imei

    }

    //失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {

    }

    /**
     * 网络访问成功处理
     * */
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.login -> {//登录接口
                var s = success as NormalRequest<String>
                if (success.code == 0) {
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                    var obb = success.obj.toString()
                    if (!TextUtils.isEmpty(obb)) obb = obb.substring(1, obb.length - 1)
                    Utils.putCache(sp.token, obb)
                    Utils.putCache(sp.user_id, id_et.text.toString().trim())//姓名
                    Utils.putCache(sp.pwd, pwd_et.text.toString().trim())//姓名
                } else {
                    if (!TextUtils.isEmpty(success.message)) toast(success.message)
                }
                login_btn.isEnabled = true
                dialog!!.dismiss()
            }
        }
        dialog!!.dismiss()
    }

    /**
     * 网络访问失败处理
     * */
    override fun onError(result: Int, error: Any?) {
        dialog!!.dismiss()
        login_btn.isEnabled = true
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }
}
