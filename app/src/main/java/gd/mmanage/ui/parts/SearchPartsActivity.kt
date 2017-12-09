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
import gd.mmanage.method.UtilControl
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.PartsModel
import gd.mmanage.ui.inbound.ChoiceInBoundActivity
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

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        only_selected = intent.getBooleanExtra("only_selected", true)
        control = getModule(PratsModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<PartsModel>(this, answer_list, R.layout.item_part) {
            override fun convert(holder: CommonViewHolder, model: PartsModel, position: Int) {
                holder.setText(R.id.name_tv, model.PartsName + "  " + model.PartsSpecifications)
                holder.setText(R.id.price_tv, "￥" + model.PartesPrice)
                holder.setText(R.id.company_type_tv, model.PartsManufacturer)
                holder.setText(R.id.count_tv, "共" + model.PartsNumber + "件")
                holder.setVisible(R.id.update_ll, only_selected)
                holder.setVisible(R.id.bottom_line_v, only_selected)
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {
                    if (!only_selected) {//仅选择
                        setResult(13, Intent().putExtra("model", model))
                        finish()
                    } else {
                        startActivityForResult(Intent(this@SearchPartsActivity, AddPartsActivity::class.java)
                                .putExtra("model", model), 11)
                    }
                }
            }
        }
        //控制添加按钮显示隐藏
        if (only_selected) bottom_ll.visibility = View.VISIBLE else bottom_ll.visibility = View.GONE
        title_bar.setRightClick {
            startActivityForResult(
                    Intent(this@SearchPartsActivity, ChoiceInBoundActivity::class.java)
                            .putExtra("model", choice_model), 11)
        }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {
            page_index++
            load_data()
        }
        main_srl.setOnRefreshListener {
            page_index = 1
            load_data()
        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->
            if (!only_selected) {//仅选择
                var intent = Intent()
                intent.putExtra("model", answer_list[position])
                setResult(13, intent)
                finish()
            } else {
                startActivity(Intent(this, PartDetailActivity::class.java)
                        .putExtra("id", answer_list[position].PartsId))
            }
        }
        //添加配件信息
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchPartsActivity, AddPartsActivity::class.java)
                    .putExtra("model", PartsModel()), 11)
        }
        load_data()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            12, 1 -> {//刷新数据
                page_index = 1
                try {
                    choice_model = data!!.getSerializableExtra("model") as PartsModel
                } catch (e: Exception) {
                }
                load_data()
            }
        }
    }

    fun load_data() {
        var map = UtilControl.change(choice_model)
        map.put("page_index", page_index.toString())
        control!!.get_prats(map)

    }

    var control: PratsModule? = null
    var page_index = 1
    var choice_model: PartsModel = PartsModel()
    override fun setLayoutId(): Int {
        return R.layout.activity_search_parts
    }
}