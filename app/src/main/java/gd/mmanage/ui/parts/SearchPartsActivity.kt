package gd.mmanage.ui.parts

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityAddPartsBinding
import gd.mmanage.databinding.ActivitySearchPartsBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.PartsModel
import kotlinx.android.synthetic.main.activity_search_parts.*

/**
 * 查询配件信息
 * */
class SearchPartsActivity : BaseActivity<ActivitySearchPartsBinding>(), AbsModule.OnCallback {
    var only_selected = false//仅仅是选择
    override fun onSuccess(result: Int, success: Any?) {
        main_srl.isRefreshing = false
        when (result) {
            command.parts + 3 -> {
                success as NormalRequest<JsonElement>
                if (page_index == 1) {
                    answer_list = java.util.ArrayList()
                }
                var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                mode.data as List<PartsModel>
                var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                em.map { Gson().fromJson<PartsModel>(it, PartsModel::class.java) }
                        .forEach { answer_list.add(it) }
                adapter!!.refresh(answer_list)
                main_lv.getIndex(page_index, 20, mode.ItemCount)
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        main_srl.isRefreshing = false
    }

    var adapter: CommonAdapter<PartsModel>? = null//资讯
    var answer_list = ArrayList<PartsModel>()
    var choice = HashMap<String, String>()//查询的条件

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        only_selected = intent.getBooleanExtra("only_selected", false)
        control = getModule(PratsModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<PartsModel>(this, answer_list, R.layout.item_part) {
            override fun convert(holder: CommonViewHolder, model: PartsModel, position: Int) {
                holder.setText(R.id.name_tv, model.PartsName + "  " + model.PartsSpecifications)
                holder.setText(R.id.price_tv, "￥" + model.PartesPrice)
                holder.setText(R.id.company_type_tv, model.PartsManufacturer + "件")
                holder.setText(R.id.count_tv, model.PartsNumber)
                holder.setVisible(R.id.update_ll, only_selected)
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {
                    startActivityForResult(Intent(this@SearchPartsActivity, AddPartsActivity::class.java)
                            .putExtra("model", model), 11)
                }
            }
        }
        //控制添加按钮显示隐藏
        if (only_selected) add_btn.visibility = View.GONE else add_btn.visibility = View.VISIBLE
        title_bar.setLeftClick { finish() }
        title_bar.setRightClick { }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {
            page_index++
            control!!.get_prats(choice)
        }
        main_srl.setOnRefreshListener {
            choice.put("PartsEnterpriseId", "")
            control!!.get_prats(choice)
        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->
            startActivity(Intent(this, PartDetailActivity::class.java)
                    .putExtra("id", answer_list[position].PartsId))
        }
        //添加配件信息
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchPartsActivity, AddPartsActivity::class.java)
                    .putExtra("model", PartsModel()), 11)
        }
        control!!.get_prats(choice)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            12 -> {//刷新数据
                choice.put("page_index", page_index.toString())
                control!!.get_prats(choice)
            }
        }
    }

    var control: PratsModule? = null
    var page_index = 1
    override fun setLayoutId(): Int {
        return R.layout.activity_search_parts
    }
}