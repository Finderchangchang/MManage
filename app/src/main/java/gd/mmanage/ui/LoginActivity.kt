package gd.mmanage.ui

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.widget.Toast
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.LoginModule
import gd.mmanage.databinding.ActivityLoginBinding

import kotlinx.android.synthetic.main.activity_login.*
import android.content.Context.ALARM_SERVICE
import android.app.AlarmManager
import android.content.Context
import android.os.SystemClock
import gd.mmanage.service.LongRunningService
import android.content.Intent
import android.text.TextUtils
import com.jaeger.library.StatusBarUtil
import gd.mmanage.model.NormalRequest


class LoginActivity : BaseActivity<ActivityLoginBinding>(), AbsModule.OnCallback {
    private var control: LoginModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(LoginModule::class.java, this)
        StatusBarUtil.setTransparent(this)
        login_btn.setOnClickListener {
            var name = id_et.text.toString().trim()
            var pwd = pwd_et.text.toString().trim()
            when {
                TextUtils.isEmpty(name) -> toast("请输入用户名")
                TextUtils.isEmpty(pwd) -> toast("请输入密码")
                else -> control!!.user_login(name, pwd)//登录操作
            }
        }
    }

    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            1000 -> {
                success as NormalRequest<String>
                if (success.result) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
                toast(success.obj)
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }


}
