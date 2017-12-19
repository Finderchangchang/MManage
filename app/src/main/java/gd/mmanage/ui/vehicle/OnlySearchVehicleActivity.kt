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
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivitySearchOnlyVehicleBinding
import gd.mmanage.databinding.ActivitySearchVehicleBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.method.UtilControl
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.VehicleModel
import gd.mmanage.ui.car_manage.AddDubiousCarActivity
import gd.mmanage.ui.car_manage.AddGetCarActivity
import gd.mmanage.ui.car_manage.AddServiceActivity
import gd.mmanage.ui.parts.AddPartsServiceActivity
import gd.mmanage.ui.parts.AddPartsServicesActivity
import kotlinx.android.synthetic.main.activity_search_only_vehicle.*

/**
 * 只查询未取车辆
 * */
class OnlySearchVehicleActivity : BaseActivity<ActivitySearchOnlyVehicleBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        main_srl.isRefreshing = false
        when (result) {
            command.car_manage + 3 -> {
                success as NormalRequest<JsonElement>
                if (page_index == 1) {
                    answer_list = java.util.ArrayList()
                }
                if (success.obj != null) {
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
    }


    override fun onError(result: Int, error: Any?) {
        main_srl.isRefreshing = false
    }

    var adapter: CommonAdapter<VehicleModel>? = null//资讯
    var answer_list = ArrayList<VehicleModel>()
    var choice_model = VehicleModel()//条件查询model
    var position = "1"
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        choice_model.VehicleTakeState = "01"
        when (intent.getStringExtra("id")) {
            "1" -> {
                title_bar.center_Tv.text = "取车登记"
            }
            "2" -> {
                title_bar.center_Tv.text = "维修登记"
            }
            "3" -> {
                title_bar.center_Tv.text = "可疑车辆登记"
            }
            "4" -> {
                title_bar.center_Tv.text = "配件更换"
            }
        }
        control = getModule(CarManageModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<VehicleModel>(this, answer_list, R.layout.item_vehicle_only) {
            override fun convert(holder: CommonViewHolder, model: VehicleModel, position: Int) {
                holder.setText(R.id.name_tv, model.VehicleNumber)
                holder.setText(R.id.price_tv, model.VehicleColor + model.VehicleBrand)
                holder.setText(R.id.company_type_tv, model.VehiclePerson)
                holder.setText(R.id.count_tv, model.VehiclePersonPhone)
                when (intent.getStringExtra("id")) {
                    "2" -> {
                        if (TextUtils.isEmpty(model.RepairCount)) {
                            holder.setText(R.id.btn, "登记")
                        } else {
                            holder.setText(R.id.btn, "修改")
                        }
                    }
                    "3" -> {
                        if (TextUtils.isEmpty(model.SuspiciousCount)) {
                            holder.setText(R.id.btn, "登记")
                        } else {
                            holder.setText(R.id.btn, "修改")
                        }
                    }
                }
                holder.setOnClickListener(R.id.btn) {
                    when (intent.getStringExtra("id")) {
                        "1" -> {
                            startActivityForResult(Intent(this@OnlySearchVehicleActivity, AddGetCarActivity::class.java)
                                    .putExtra("vehicleId", model.VehicleId), 11)
                        }
                        "2" -> {
                            startActivityForResult(Intent(this@OnlySearchVehicleActivity, AddServiceActivity::class.java)
                                    .putExtra("vehicleId", model.VehicleId), 12)
                        }
                        "3" -> {
                            startActivityForResult(Intent(this@OnlySearchVehicleActivity, AddDubiousCarActivity::class.java)
                                    .putExtra("vehicleId", model.VehicleId), 13)
                        }
                        "4" -> {
                            startActivityForResult(Intent(this@OnlySearchVehicleActivity, AddPartsServicesActivity::class.java)
                                    .putExtra("vehicleId", model.VehicleId), 14)
                        }
                    }
                }
            }
        }
        title_bar.setLeftClick { finish() }
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
            startActivityForResult(Intent(this@OnlySearchVehicleActivity, VehicleDetailActivity::class.java)
                    .putExtra("id", answer_list[position].VehicleId), 11)
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
        //when (resultCode) {
        //12, 1 -> {//刷新数据
        page_index = 1
        try {
            choice_model = data!!.getSerializableExtra("model") as VehicleModel
        } catch (e: Exception) {

        }
        load_data()
        // }
        //}
    }

    var control: CarManageModule? = null
    var page_index = 1
    override fun setLayoutId(): Int {
        return R.layout.activity_search_only_vehicle
    }
}