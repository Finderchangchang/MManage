package gd.mmanage.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import butterknife.Bind
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseFragment
import gd.mmanage.config.command
import gd.mmanage.control.LoginModule
import gd.mmanage.control.UserModule
import gd.mmanage.databinding.FragUserBinding
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EnterpriseModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.ui.config.SetActivity
import net.tsz.afinal.FinalDb

/**
 * Created by Administrator on 2017/11/11.
 */

class UserFragment : BaseFragment<FragUserBinding>(), AbsModule.OnCallback {
    var set_iv: ImageView? = null
    var db: FinalDb? = null
    var xc_list: List<CodeModel>? = null
    var qy_list: List<CodeModel>? = null
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.user -> {
                success as NormalRequest<JsonElement>
                var model: EnterpriseModel = Gson().fromJson(success.obj, EnterpriseModel::class.java)
                xc_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_RepairType' and ID='"+model.EnterpriseVehicleType+"'")//修车类型
                if(xc_list!=null&& xc_list!!.isNotEmpty())binding.xc=xc_list!![0].Name
                qy_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_EnterpriseState' and ID='"+model.EnterpriseVehicleType+"'")//企业状态
                if(qy_list!=null&& qy_list!!.isNotEmpty())binding.qy=qy_list!![0].Name
                binding.model = model
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun init(savedInstanceState: Bundle?) {
        control = getModule(UserModule::class.java, this)
        control!!.get_company_info("C021306020001")
        db = FinalDb.create(context)

    }

    override fun load_view(view: View?) {
        set_iv = view!!.findViewById(R.id.set_iv) as ImageView
        set_iv!!.setOnClickListener { startActivity(Intent(HomeActivity.context, SetActivity::class.java)) }
    }

    private var control: UserModule? = null
    /**
     * 延时加载
     */
    override fun onDelayLoad() {

    }

    override fun setLayoutId(): Int {
        return R.layout.frag_user
    }


    override fun dataCallback(result: Int, obj: Any) {

    }
}