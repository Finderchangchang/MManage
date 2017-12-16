package gd.mmanage.ui.parts

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.google.gson.JsonElement
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddInBoundBinding
import gd.mmanage.databinding.ActivityAddPartsServiceBinding
import gd.mmanage.method.Utils
import gd.mmanage.model.*
import gd.mmanage.ui.inbound.InBoundsModule
import kotlinx.android.synthetic.main.activity_add_parts_service.*
import net.tsz.afinal.FinalDb

/**
 * 配件业务添加
 * */
class AddPartsServiceActivity : BaseActivity<ActivityAddPartsServiceBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        success as NormalRequest<JsonElement>
        if (success.code == 0) {
            setResult(12)
            finish()
            toast("添加成功")
        } else {
            toast(success.message)
        }
        save_btn.isEnabled = true
        dialog!!.dismiss()
    }

    override fun onError(result: Int, error: Any?) {
        save_btn.isEnabled = true
        dialog!!.dismiss()
    }

    var dialog: LoadingDialog.Builder? = null

    var model: PartsBusinessModel = PartsBusinessModel()
    var control: PratsModule? = null
    var vehicleId = ""
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        control = getModule(PratsModule::class.java, this)
        vehicleId = intent.getStringExtra("vehicleId")
        name_ll.setOnClickListener {
            startActivityForResult(Intent(this, SearchPartsActivity::class.java)
                    .putExtra("only_selected", false), 11)
        }
        save_btn.setOnClickListener {
            if (TextUtils.isEmpty(tv4.text.toString().trim())) {
                toast("出库数量不能为空")
            } else {
                save_btn.isEnabled = false
                dialog!!.show()
                model.Number = tv4.text.toString().trim()//配件数量
                model.Comment = desc_et.text.toString().trim()
                model.CreateTime = Utils.normalTime
                model.VehicleId = vehicleId
                control!!.add_service_prat(model)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 13) {
            var m = data!!.getSerializableExtra("model") as PartsModel
            model.PartsId = m.PartsId
            name_tv.text = m.PartsName
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_parts_service
    }
}