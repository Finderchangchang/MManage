package gd.mmanage.ui.parts

import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.databinding.ActivityPartDetailBinding
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.PartsModel
import kotlinx.android.synthetic.main.activity_add_parts.*

/**
 * 添加配件
 * @author 杰
 * */
class PartDetailActivity : BaseActivity<ActivityPartDetailBinding>(), AbsModule.OnCallback {
    var control: PratsModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(PratsModule::class.java, this)
        title_bar.setLeftClick { finish() }
        control!!.get_partById(intent.getStringExtra("id"))
    }

    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.parts + 2 -> {//查询出来的结果
                success as NormalRequest<JsonElement>
                var mode: PartsModel = Gson().fromJson<PartsModel>(success.obj, PartsModel::class.java)
                binding.model = mode
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_part_detail
    }
}