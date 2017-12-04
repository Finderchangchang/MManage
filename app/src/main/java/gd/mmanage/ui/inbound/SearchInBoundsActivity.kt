package gd.mmanage.ui.inbound

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivitySearchInBoundsBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.InBoundModel
import gd.mmanage.ui.parts.AddPartsActivity
import gd.mmanage.ui.parts.PartDetailActivity
import gd.mmanage.ui.parts.PratsModule
import gd.mmanage.ui.parts.SearchPartsActivity
import kotlinx.android.synthetic.main.activity_search_in_bounds.*

/**
 * 入库查询
 * */
class SearchInBoundsActivity : BaseActivity<ActivitySearchInBoundsBinding>(), AbsModule.OnCallback {
    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_search_in_bounds
    }

    override fun onSuccess(result: Int, success: Any?) {
        main_srl.isRefreshing = false
        when (result) {
            command.parts + 1 -> {//查询结果
                success as NormalRequest<JsonElement>
                if (page_index == 1) {
                    answer_list = java.util.ArrayList()
                }
                var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                mode.data as List<InBoundModel>
                var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                em.map { Gson().fromJson<InBoundModel>(it, InBoundModel::class.java) }
                        .forEach { answer_list.add(it) }
                adapter!!.refresh(answer_list)
                main_lv.getIndex(page_index, 20, mode.ItemCount)
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        main_srl.isRefreshing = false
    }

    var adapter: CommonAdapter<InBoundModel>? = null//资讯
    var answer_list = ArrayList<InBoundModel>()
    var choice = HashMap<String, String>()//查询的条件

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        control = getModule(InBoundsModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<InBoundModel>(this, answer_list, R.layout.item_in_bound) {
            override fun convert(holder: CommonViewHolder, model: InBoundModel, position: Int) {
                holder.setText(R.id.name_tv, model.PartsName)
                holder.setText(R.id.price_tv, model.StorageTime)
                holder.setText(R.id.company_type_tv, model.StoragePerson)
                holder.setText(R.id.count_tv, model.StorageNumber + "件")
            }
        }
        title_bar.setLeftClick { finish() }
        title_bar.setRightClick { }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {
            page_index++
            control!!.get_in_bounds(choice)
        }
        main_srl.setOnRefreshListener {
            choice.put("PartsEnterpriseId", "")
            control!!.get_in_bounds(choice)
        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->
            //            startActivity(Intent(this, PartDetailActivity::class.java)
//                    .putExtra("id", answer_list[position].StorageId))
        }
        //入库单添加
        add_in_bound_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchInBoundsActivity, AddInBoundActivity::class.java)
                    .putExtra("storagePartsId", "5A1B6BFF32BF500270AA0A16"), 11)
        }
        //添加配件信息
        add_pj_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchInBoundsActivity, SearchPartsActivity::class.java)
                    .putExtra("model", InBoundModel()), 11)
        }
        control!!.get_in_bounds(choice)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            12 -> {//刷新数据
                choice.put("page_index", page_index.toString())
                control!!.get_in_bounds(choice)
            }
        }
    }

    var control: InBoundsModule? = null
    var page_index = 1

}
