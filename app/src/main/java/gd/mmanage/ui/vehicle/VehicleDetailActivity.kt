package gd.mmanage.ui.vehicle

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityVehicleDetailBinding
import gd.mmanage.control.CarManageModule
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.*
import gd.mmanage.ui.car_manage.AddDubiousCarActivity
import gd.mmanage.ui.car_manage.AddGetCarActivity
import gd.mmanage.ui.car_manage.AddServiceActivity
import gd.mmanage.ui.parts.AddPartsActivity
import gd.mmanage.ui.parts.AddPartsServicesActivity
import kotlinx.android.synthetic.main.activity_vehicle_detail.*

/**
 * 车辆承接详细信息
 * @author 杰
 * */
class VehicleDetailActivity : BaseActivity<ActivityVehicleDetailBinding>(), AbsModule.OnCallback {
    var control: CarManageModule? = null
    var adapter: CommonAdapter<PartsModel>? = null//资讯
    var answer_list = ArrayList<PartsModel>()
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        control = getModule(CarManageModule::class.java, this)
        control!!.get_vehicleById(intent.getStringExtra("id"))
        adapter = object : CommonAdapter<PartsModel>(this, answer_list, R.layout.item_part) {
            override fun convert(holder: CommonViewHolder, model: PartsModel, position: Int) {
                holder.setText(R.id.name_tv, model.PartsName + "  " + model.PartsSpecifications)
                holder.setText(R.id.price_tv, "￥" + model.PartesPrice)
                holder.setText(R.id.company_type_tv, model.PartsManufacturer)
                holder.setText(R.id.count_tv, "共" + model.PartsNumber + "件")
                holder.setVisible(R.id.bottom_rl, false)
                holder.setVisible(R.id.bottom_line, false)
            }
        }
        lv.adapter = adapter
        btn1.setOnClickListener {
            startActivityForResult(Intent(this@VehicleDetailActivity, AddGetCarActivity::class.java)
                    .putExtra("vehicleId", binding.model.Vehicle!!.VehicleId), 11)

        }
        btn2.setOnClickListener {
            startActivityForResult(Intent(this@VehicleDetailActivity, AddServiceActivity::class.java)
                    .putExtra("vehicleId", binding.model.Vehicle!!.VehicleId), 12)
        }
        btn3.setOnClickListener {
            startActivityForResult(Intent(this@VehicleDetailActivity, AddDubiousCarActivity::class.java)
                    .putExtra("vehicleId", binding.model.Vehicle!!.VehicleId), 13)
        }
        btn4.setOnClickListener {
            startActivityForResult(Intent(this@VehicleDetailActivity, AddPartsServicesActivity::class.java)
                    .putExtra("vehicleId", binding.model.Vehicle!!.VehicleId), 14)
        }

    }

    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 2 -> {//查询出来的结果
                success as NormalRequest<*>
                var model = Gson().fromJson<DetailModel>(success.obj.toString(), DetailModel::class.java)
                binding.model = model
                if (model != null) {
                    if (model.Parts != null) {
                        pj_ll.visibility = View.VISIBLE
                        pj_tv.visibility = View.VISIBLE
                        adapter!!.refresh(model.Parts)
                    }
                    if (model.Repair != null) {
                        xl_ll.visibility = View.VISIBLE
                        xl_tv.visibility = View.VISIBLE
                    }
                    if (model.Suspicious != null) {
                        bj_ll.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_vehicle_detail
    }
}