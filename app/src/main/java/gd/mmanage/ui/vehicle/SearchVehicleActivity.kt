package gd.mmanage.ui.vehicle

import android.content.Intent
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivitySearchVehicleBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.VehicleModel
import gd.mmanage.control.CarManageModule
import kotlinx.android.synthetic.main.activity_search_vehicle.*

/**
 * 查询配件信息
 * */
class SearchVehicleActivity : BaseActivity<ActivitySearchVehicleBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        main_srl.isRefreshing = false
        when (result) {
            command.car_manage + 3 -> {
                success as NormalRequest<JsonElement>
                if (page_index == 1) {
                    answer_list = java.util.ArrayList()
                }
                var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                mode.data as List<VehicleModel>
                var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                em.map { Gson().fromJson<VehicleModel>(it, VehicleModel::class.java) }
                        .forEach { answer_list.add(it) }
                adapter!!.refresh(answer_list)
                main_lv.getIndex(page_index, 20, mode.ItemCount)
            }
        }
    }


    override fun onError(result: Int, error: Any?) {
        main_srl.isRefreshing = false
    }

    var adapter: CommonAdapter<VehicleModel>? = null//资讯
    var answer_list = ArrayList<VehicleModel>()
    var choice = HashMap<String, String>()//查询的条件

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        control = getModule(CarManageModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<VehicleModel>(this, answer_list, R.layout.item_vehicle) {
            override fun convert(holder: CommonViewHolder, model: VehicleModel, position: Int) {
                holder.setText(R.id.name_tv, model.VehicleNumber)
                holder.setText(R.id.price_tv, model.VehicleColor + model.VehicleBrand)
                holder.setText(R.id.company_type_tv, model.VehiclePerson)
                holder.setText(R.id.count_tv, model.VehiclePersonPhone)
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {
                    startActivityForResult(Intent(this@SearchVehicleActivity, AddPersonActivity::class.java)
                            .putExtra("model", model), 11)
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
            page_index++
            control!!.get_vehicles(choice)
        }
        main_srl.setOnRefreshListener {
            choice.put("PartsEnterpriseId", "")
            control!!.get_vehicles(choice)
        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->
            startActivityForResult(Intent(this@SearchVehicleActivity, VehicleDetailActivity::class.java)
                    .putExtra("id", answer_list[position].VehicleId), 11)
        }
        //添加配件信息
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchVehicleActivity, AddPersonActivity::class.java)
                    .putExtra("model", VehicleModel()), 11)
        }
        control!!.get_vehicles(choice)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            12 -> {//刷新数据
                choice.put("page_index", page_index.toString())
                control!!.get_vehicles(choice)
            }
        }
    }

    var control: CarManageModule? = null
    var page_index = 1
    override fun setLayoutId(): Int {
        return R.layout.activity_search_vehicle
    }
}