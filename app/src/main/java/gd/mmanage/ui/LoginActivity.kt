package gd.mmanage.ui

import android.Manifest
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.LoginModule
import gd.mmanage.databinding.ActivityLoginBinding

import android.content.Intent
import android.text.TextUtils
import gd.mmanage.model.NormalRequest
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.method.Utils
import gd.mmanage.service.DownConfigService
import gd.mmanage.ui.main.HomeActivity
import pub.devrel.easypermissions.EasyPermissions
import gd.mmanage.ui.config.RegCompanyActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity<ActivityLoginBinding>(), AbsModule.OnCallback, EasyPermissions.PermissionCallbacks {
    private var control: LoginModule? = null
    var dialog: LoadingDialog.Builder? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this)
        control = getModule(LoginModule::class.java, this)//初始化网络请求
        //StatusBarUtil.setTransparent(this)//设置状态栏颜色
        imei_tv.setOnClickListener {
            if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE)) {
                EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                        2, Manifest.permission.READ_PHONE_STATE)
            } else {
                startActivity(Intent(this@LoginActivity, RegCompanyActivity::class.java))
            }
        }
        id_et.setText(Utils.getCache(sp.user_id))
        pwd_et.setText(Utils.getCache(sp.pwd))
        if (TextUtils.isEmpty(Utils.getCache(sp.sys_camera_show))) {
            Utils.putCache(sp.sys_camera_show, "×")
        }
        //登录按钮
        login_btn.setOnClickListener {
            var name = id_et.text.toString().trim()
            var pwd = pwd_et.text.toString().trim()
            when {
                TextUtils.isEmpty(name) -> toast("请输入用户名")
                TextUtils.isEmpty(pwd) -> toast("请输入密码")
                else -> {
                    if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                                2, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

                    } else {
                        login_btn.isEnabled = false
                        dialog!!.setTitle("登录中，请稍后...")
                        dialog!!.show()
                        control!!.user_login(name, pwd)//登录操作
                    }
                }
            }
        }
        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                    2, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        } else {
            binding.imei = "当前设备IMEI：" + Utils.imei
        }
        //标志不存在，执行下载配置信息操作
        if (TextUtils.isEmpty(Utils.getCache(sp.down_all))) {
            startService(Intent(this, DownConfigService::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限,否则无法注册企业信息",
                    2, Manifest.permission.READ_PHONE_STATE)
        } else {
            binding.imei = "当前设备IMEI：" + Utils.imei
        }
    }

    //失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                    2, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        }
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
