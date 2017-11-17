package gd.mmanage.ui

import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.LoginModule
import gd.mmanage.databinding.ActivityLoginBinding

import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import android.text.TextUtils
import com.jaeger.library.StatusBarUtil
import gd.mmanage.model.NormalRequest
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.config.command
import gd.mmanage.model.CodeModel
import gd.mmanage.ui.config.DownHotelActivity
import gd.mmanage.ui.main.HomeActivity


class LoginActivity : BaseActivity<ActivityLoginBinding>(), AbsModule.OnCallback {
    private var control: LoginModule? = null
    var dialog: LoadingDialog.Builder? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        control = getModule(LoginModule::class.java, this)//初始化网络请求
        StatusBarUtil.setTransparent(this)//设置状态栏颜色
        //登录按钮
        login_btn.setOnClickListener {
            var name = id_et.text.toString().trim()
            var pwd = pwd_et.text.toString().trim()
            when {
                TextUtils.isEmpty(name) -> toast("请输入用户名")
                TextUtils.isEmpty(pwd) -> toast("请输入密码")
                else -> {
                    dialog!!.show()
                    //control!!.user_login(name, pwd)//登录操作
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }
        control!!.check_version()//检查更新
    }

    /**
     * 网络访问成功处理
     * */
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.login -> {//登录接口
                success as NormalRequest<ArrayList<CodeModel>>//强转
                when (success.code) {
                    1 -> {//跳转到主页
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                    2 -> {//跳转到绑定code页面
                        startActivity(Intent(this@LoginActivity, DownHotelActivity::class.java))
                    }
                }
                if (TextUtils.isEmpty(success.message)) {
                    toast(success.message)
                }
            }
            command.login + 1 -> {//检查版本更新
                success as NormalRequest<String>
                if (success.result) {//提示更新
                    toast(success.obj)
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
