package gd.mmanage.ui.employee

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityLoginBinding

import kotlinx.android.synthetic.main.activity_add_employee.*

/**
 * 添加从业人员信息
 * */
class AddEmployeeActivity : BaseActivity<ActivityAddEmployeeBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_employee
    }
}
