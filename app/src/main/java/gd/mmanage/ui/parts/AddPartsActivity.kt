package gd.mmanage.ui.parts

import android.os.Bundle
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PartsModel
import kotlinx.android.synthetic.main.activity_add_parts.*

/**
 * 添加配件
 * @author 杰
 * */
class AddPartsActivity : BaseActivity<ActivityAddPartsBinding>(), AbsModule.OnCallback {
    var control: PratsModule? = null
    var model: PartsModel? = null
    var is_add = false//true：添加 false:修改
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        model = intent.getSerializableExtra("model") as PartsModel
        is_add = TextUtils.isEmpty(model!!.PartsId)//判断是添加还是修改
        if (!is_add) title_bar.setCentertv("配件修改")
        control = getModule(PratsModule::class.java, this)
        title_bar.setLeftClick { finish() }
        binding.model = model
        //保存数据操作
        save_btn.setOnClickListener {
            model = binding.model

            model!!.PartsType = "1"
            //model!!.PartsEnterpriseId="C021306020001"
            control!!.add_prat(model!!)
            //builder.show()
        }
    }

    override fun onSuccess(result: Int, success: Any?) {
        success as NormalRequest<JsonElement>
        when (success.code) {
            0 -> {
                setResult(12)
                finish()
                if (is_add) toast("添加成功") else toast("修改成功")
            }
            else -> {
                if (is_add) toast("添加失败") else toast("修改失败")
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        if (is_add) toast("添加失败") else toast("修改失败")
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_parts
    }
}