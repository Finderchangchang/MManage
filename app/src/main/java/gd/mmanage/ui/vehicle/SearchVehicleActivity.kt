package gd.mmanage.ui.vehicle

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import gd.mmanage.method.UtilControl
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
    var choice_model = VehicleModel()//条件查询model

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        control = getModule(CarManageModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<VehicleModel>(this, answer_list, R.layout.item_vehicle) {
            override fun convert(holder: CommonViewHolder, model: VehicleModel, position: Int) {
                holder.setText(R.id.name_tv, model.VehicleNumber)
                var right_tv = ""
                if (!TextUtils.isEmpty(model.VehicleColor)) right_tv += model.VehicleColor
                if (!TextUtils.isEmpty(model.VehicleBrand)) right_tv += model.VehicleBrand
                holder.setText(R.id.price_tv, right_tv)
                holder.setText(R.id.company_type_tv, model.VehiclePerson)
                if (!TextUtils.isEmpty(model.VehiclePersonPhone)) {
                    holder.setText(R.id.count_tv, model.VehiclePersonPhone)
                }
                if (model.VehicleTakeState == "01") {
                    holder.setVisible(R.id.update_line, true)
                    holder.setVisible(R.id.update_rl, true)
                } else {
                    holder.setInVisible(R.id.update_line)
                    holder.setInVisible(R.id.update_rl)
                }
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {
                    startActivityForResult(Intent(this@SearchVehicleActivity, AddPersonActivity::class.java)
                            .putExtra("model", model), 12)
                }
            }
        }
        title_bar.setLeftClick { finish() }
        title_bar.setRightClick {
            startActivityForResult(Intent(this@SearchVehicleActivity, ChoiceVehicleActivity::class.java)
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
            startActivityForResult(Intent(this@SearchVehicleActivity, VehicleDetailActivity::class.java)
                    .putExtra("id", answer_list[position].VehicleId), 11)
        }
        //添加配件信息
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchVehicleActivity, AddPersonActivity::class.java)
                    .putExtra("model", choice_model), 11)
        }
        load_data()
    }

    fun load_data() {
        var map = UtilControl.change(choice_model)
        map.put("page_index", page_index.toString())
        control!!.get_vehicles(map)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        refresh_list(data)

    }

    fun refresh_list(data: Intent?) {
        page_index = 1
        if (data != null) {
            choice_model = data.getSerializableExtra("model") as VehicleModel
        }
        load_data()
    }

    var control: CarManageModule? = null
    var page_index = 1
    override fun setLayoutId(): Int {
        return R.layout.activity_search_vehicle
    }
}