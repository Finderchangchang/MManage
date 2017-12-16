package gd.mmanage.ui.parts

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityAddPartsServicesBinding
import gd.mmanage.databinding.ActivitySearchPartsBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.method.UtilControl
import gd.mmanage.method.Utils
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.model.PartsBusinessModel
import gd.mmanage.model.PartsModel
import gd.mmanage.ui.inbound.ChoiceInBoundActivity
import kotlinx.android.synthetic.main.activity_add_parts_services.*

/**
 * 查询配件信息
 * */
class AddPartsServicesActivity : BaseActivity<ActivityAddPartsServicesBinding>(), AbsModule.OnCallback {
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
            command.parts -> {
                success as NormalRequest<JsonElement>
                if (success.code == 0) {
                    if (save_num == result_list.size - 1) {
                        finish()
                        toast("添加成功")
                    } else {
                        save_num++
                    }
                } else {
                    toast(success.message)
                }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        main_srl.isRefreshing = false
    }

    var adapter: CommonAdapter<PartsModel>? = null//资讯
    var answer_list = ArrayList<PartsModel>()
    var result_list = ArrayList<PartsModel>()
    var vehicleId = ""
    var save_num = 0//当前是第几次保存
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)//http://192.168.1.115:3334/Api/Storage/SearchStorage
        vehicleId = intent.getStringExtra("vehicleId")

        control = getModule(PratsModule::class.java, this)//初始化数据访问层
        adapter = object : CommonAdapter<PartsModel>(this, answer_list, R.layout.item_vehicle_add) {
            override fun convert(holder: CommonViewHolder, model: PartsModel, position: Int) {
                if (TextUtils.isEmpty(model.PartsSpecifications)||model.PartsSpecifications=="null") {
                    model.PartsSpecifications = ""
                }
                holder.setText(R.id.name_tv, model.PartsName + "  " + model.PartsSpecifications)
                holder.setText(R.id.price_tv, "￥" + model.PartesPrice)
                holder.setText(R.id.company_type_tv, model.PartsManufacturer)
                holder.setText(R.id.count_tv, "共" + model.PartsNumber + "件")
                holder.setChecked(R.id.checkbox, model.num > 0)//设置是否点击
                //增加
                holder.setOnClickListener(R.id.jia_iv) {
                    var result = answer_list[position].num + 1
                    if (result <= model.PartsNumber.toInt()) {
                        answer_list[position].num = result
                        adapter!!.refresh(answer_list)
                        holder.setText(R.id.num_tv, result.toString())
                    }
                }
                //减少
                holder.setOnClickListener(R.id.jian_iv) {
                    var result = answer_list[position].num - 1
                    if (result >= 0) {
                        answer_list[position].num = result
                        adapter!!.refresh(answer_list)
                        holder.setText(R.id.num_tv, result.toString())
                    }
                }
            }
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
        //添加配件信息
        add_btn.setOnClickListener {
            answer_list.filter { it.num > 0 }.forEach { result_list.add(it) }
            if (result_list.size == 0) {
                toast("请选择需要添加的配件")
            } else {
                for (key in result_list) {
                    var model = PartsBusinessModel()
                    //model.Comment = desc_et.text.toString().trim()
                    model.Number = key.num.toString()
                    model.PartsId = key.PartsId
                    model.CreateTime = Utils.normalTime
                    model.VehicleId = vehicleId
                    control!!.add_service_prat(model)
                }
            }
        }
        load_data()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            12, 1 -> {//刷新数据
                page_index = 1
                choice_model = data!!.getSerializableExtra("model") as PartsModel
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
        return R.layout.activity_add_parts_services
    }
}