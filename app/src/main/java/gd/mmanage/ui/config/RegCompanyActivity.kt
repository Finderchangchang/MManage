package gd.mmanage.ui.config

import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.arialyy.frame.module.AbsModule
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.UserModule
import gd.mmanage.databinding.ActivityAddPartsServicesBinding
import gd.mmanage.method.AllCapTransformationMethod
import gd.mmanage.method.Utils
import gd.mmanage.model.NormalRequest
import kotlinx.android.synthetic.main.activity_reg_company.*

class RegCompanyActivity : BaseActivity<ActivityAddPartsServicesBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        success as NormalRequest<String>
        when (success.code) {
            0 -> {
                toast("绑定成功")
                finish()
            }
            else -> {
                toast(success.message)
            }
        }
        dialog!!.dismiss()
    }

    override fun onError(result: Int, error: Any?) {
        toast("绑定失败")
        dialog!!.dismiss()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_reg_company
    }

    var dialog: LoadingDialog.Builder? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle(R.string.save_loading)//初始化dialog
        et1.text = Utils.imei
        et2.transformationMethod = AllCapTransformationMethod(true);
        save_btn.setOnClickListener {
            var et2 = et2.text.toString().trim()
            when {
                TextUtils.isEmpty(et2) -> toast("请输入正确的企业编码")
                et2.length != 13 -> toast("请输入正确的企业编码")
                else -> {
                    dialog!!.show()
                    getModule(UserModule::class.java, this).reg_company(et2)
                }
            }
        }
        ll1.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            // 将文本内容放到系统剪贴板里。
            cm.text = Utils.imei
            toast("复制成功")
        }
    }
}
