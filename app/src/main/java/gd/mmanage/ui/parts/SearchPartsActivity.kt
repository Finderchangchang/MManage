package gd.mmanage.ui.parts

import android.content.Intent
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.databinding.ActivitySearchPartsBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.PartsModel
import kotlinx.android.synthetic.main.activity_search_parts.*

/**
 * 查询配件信息
 * */
class SearchPartsActivity : BaseActivity<ActivitySearchPartsBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    var adapter: CommonAdapter<PartsModel>? = null//资讯
    var answer_list = ArrayList<PartsModel>()

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(PratsModule::class.java)//初始化数据访问层
        adapter = object : CommonAdapter<PartsModel>(this, answer_list, R.layout.item_employee) {
            override fun convert(holder: CommonViewHolder, model: PartsModel, position: Int) {
//                holder.setText(R.id.name_tv, model.PartsComment)
//                holder.setText(R.id.id_card_tv, model.EmployeeCertNumber)
//                holder.setText(R.id.address_tv, model.EmployeeAddress)
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {

                }
                //删除操作
                holder.setOnClickListener(R.id.del_ll) {

                }
            }
        }
        title_bar.setLeftClick { finish() }
        title_bar.setRightClick { }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {

        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->

        }
        //添加配件信息
        add_btn.setOnClickListener {
            startActivity(Intent(this@SearchPartsActivity, AddPartsActivity::class.java))
        }
        control!!.search_parts(page_index, "")
    }

    var control: PratsModule? = null
    var page_index = 1
    override fun setLayoutId(): Int {
        return R.layout.activity_search_parts
    }
}