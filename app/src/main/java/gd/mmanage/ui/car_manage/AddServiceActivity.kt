package gd.mmanage.ui.car_manage

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddCarBinding
import gd.mmanage.databinding.ActivityAddServiceCarBinding
import gd.mmanage.model.*
import kotlinx.android.synthetic.main.activity_add_service_car.*
import net.tsz.afinal.FinalDb

/**
 *车辆维修登记
 * */
class AddServiceActivity : BaseActivity<ActivityAddServiceCarBinding>(), AbsModule.OnCallback {

    override fun onSuccess(result: Int, success: Any?) {
        var s = success as NormalRequest<JsonElement>
        var s1 = ""
    }

    override fun onError(result: Int, error: Any?) {

    }

    var db: FinalDb? = null
    var yy_list: List<CodeModel> = ArrayList<CodeModel>()
    var yy_array: Array<String?>? = null//人员状态的集合

    var xm_list: List<CodeModel> = ArrayList<CodeModel>()
    var xm_array: Array<String?>? = null//人员状态的集合
    var emp_array: Array<String?>? = null//从业人员的集合
    var ky_model: RepairModel = RepairModel()
    var control: CarManageModule? = null
    var vehicleId = ""
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        vehicleId = intent.getStringExtra("vehicleId")
        control = getModule(CarManageModule::class.java, this)
        init_data()
        ll1.setOnClickListener {
            val builder = AlertDialog.Builder(this)  //先得到构造器
            builder.setItems(xm_array) { dialog, which ->
                binding.kyType = yy_list[which].Name
                //ky_model.SuspiciousType = yy_list[which].ID
            }
            builder.create().show()
        }

        ll2.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setItems(yy_array) { dialog, which ->
                //                ky_model.SuspiciousPerson = employees!![which].EmployeeName
//                binding.portName = employees!![which].EmployeeName
            }
            builder.create().show()
        }
        save_btn.setOnClickListener {
            //ky_model.SuspiciousRemarks = ky_desc_et.text.toString().trim()
            ky_model.VehicleId = vehicleId
            //ky_model.SuspiciousTime = "2017-11-12"
            //control!!.save_warn(ky_model)
        }
    }

    fun init_data() {
//初始化修理原因
        xm_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_RepairReasonType'")//Code_RepairReasonType
        xm_array = arrayOfNulls(xm_list!!.size)
        for (id in 0 until xm_array!!.size) {
            var model = xm_list!![id]
            xm_array!![id] = model.Name
        }
        //初始化修理原因
        yy_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_RepairType'")//Code_RepairReasonType
        yy_array = arrayOfNulls(yy_list!!.size)
        for (id in 0 until yy_array!!.size) {
            var model = yy_list!![id]
            yy_array!![id] = model.Name
        }
        if (yy_list.size > 0) {
            binding.kyType = yy_list[0].Name
            //ky_model.SuspiciousType = yy_list[0].ID
        }

    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_service_car
    }
}