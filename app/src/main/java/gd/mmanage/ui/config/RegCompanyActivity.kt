package gd.mmanage.ui.config

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.UserModule
import gd.mmanage.databinding.ActivityAddPartsServicesBinding
import gd.mmanage.model.NormalRequest
import kotlinx.android.synthetic.main.activity_reg_company.*

class RegCompanyActivity : BaseActivity<ActivityAddPartsServicesBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        var s = ""
        success as NormalRequest<String>
        when (success.code) {
            0 -> {
                toast("修改成功")
                finish()
            }
            else -> {
                toast("修改失败")
            }
        }
        dialog!!.dismiss()
    }

    override fun onError(result: Int, error: Any?) {
        toast("修改失败")
        dialog!!.dismiss()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_reg_company
    }

    var dialog: LoadingDialog.Builder? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle(R.string.save_loading)//初始化dialog
        save_btn.setOnClickListener {
            var et2 = et2.text.toString().trim()
            if (TextUtils.isEmpty(et2)) {
                toast("新密码不能为空")
            } else {
                dialog!!.show()
                getModule(UserModule::class.java, this).reg_company(et2)
            }
        }
    }
}
