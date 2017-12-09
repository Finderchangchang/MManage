package gd.mmanage.ui

import android.Manifest
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


class LoginActivity : BaseActivity<ActivityLoginBinding>(), AbsModule.OnCallback, EasyPermissions.PermissionCallbacks {
    private var control: LoginModule? = null
    var dialog: LoadingDialog.Builder? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        control = getModule(LoginModule::class.java, this)//初始化网络请求
        StatusBarUtil.setTransparent(this)//设置状态栏颜色
        //登录按钮
        login_btn.setOnClickListener {
            if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE)) {
                EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                        2, Manifest.permission.READ_PHONE_STATE);
            } else {
                var name = id_et.text.toString().trim()
                var pwd = pwd_et.text.toString().trim()
                when {
                    TextUtils.isEmpty(name) -> toast("请输入用户名")
                    TextUtils.isEmpty(pwd) -> toast("请输入密码")
                    else -> {
                        control!!.user_login(name, pwd)//登录操作
                    }
                }
            }
        }
        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this@LoginActivity, "您需要允许以下权限",
                    2, Manifest.permission.READ_PHONE_STATE);
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

    var update_path = ""
    //成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {

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
            }
            command.login + 1 -> {//检查版本更新
                success as NormalRequest<JsonElement>
                if (success.code == 0) {//提示更新
                    //toast(success.obj)
                    var model = Gson().fromJson<UpdateModel>(success.obj, UpdateModel::class.java)
                    var vv = Utils.version
                    if (Utils.version != model.AndroidVersion) {
                        if (!EasyPermissions.hasPermissions(this@LoginActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            EasyPermissions.requestPermissions(this@LoginActivity, "需要下载新的apk",
                                    2, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        } else {
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("提示")
                            builder.setMessage("您有新的版本，请及时更新~~")
                            builder.setNegativeButton("确定") { dialog, which ->
                                UpdateManager(this@LoginActivity).checkUpdateInfo(url.key + model.AndroidUpdateUrl)
                            }
                            builder.show()
                        }
                    }
                }
            }
        }
        dialog!!.dismiss()
    }

    /**
     * 网络访问失败处理
     * */
    override fun onError(result: Int, error: Any?) {
        dialog!!.dismiss()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }
}
