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


class LoginActivity : BaseActivity<ActivityLoginBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        Toast.makeText(this@LoginActivity, success.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        getModule(LoginModule::class.java, this).user_login("liu", "pwd")
        val intent = Intent(this, LongRunningService::class.java)
        startService(intent)
    }
}
