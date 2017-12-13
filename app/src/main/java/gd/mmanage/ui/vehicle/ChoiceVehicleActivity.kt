package gd.mmanage.ui.vehicle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityChoiceEmployeeBinding
import gd.mmanage.databinding.ActivityChoiceVehicleBinding
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.VehicleModel
import kotlinx.android.synthetic.main.activity_choice_vehicle.*
import net.tsz.afinal.FinalDb
import net.tsz.afinal.view.DatePickerDialog

/**
 * 承接条件查询
 * @author 杰
 * */
class ChoiceVehicleActivity : BaseActivity<ActivityChoiceVehicleBinding>() {
    var model: VehicleModel = VehicleModel()
    var db: FinalDb? = null
    var zt_list: List<CodeModel>? = null
    var zt_array: Array<String?>? = null//人员状态的集合
    var ve_list: List<CodeModel>? = null
    var ve_array: Array<String?>? = null//车辆状态的集合
    var datePickerDialog: DatePickerDialog? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        model = intent.getSerializableExtra("model") as VehicleModel
        db = FinalDb.create(this)
        binding.model = model
        car_type_ll.setOnClickListener { dialog(ve_array!!, 1) }
        state_ll.setOnClickListener { dialog(zt_array!!, 2) }
        //查询接口
        search_btn.setOnClickListener {
            var model = binding.model
            setResult(1, Intent().putExtra("model", model))
            finish()
        }
        start_time_ll.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.CreateTimeBegin)
            datePickerDialog!!.datePickerDialog(start_time_ev)
        }
        end_time_ll.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.CreateTimeEnd)
            datePickerDialog!!.datePickerDialog(end_time_ev)
        }
        init_data()
    }

    //初始化数据
    fun init_data() {
        zt_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_VehicleTakeState'")
        zt_array = arrayOfNulls(zt_list!!.size)
        var zt_id = "01"
        if (!TextUtils.isEmpty(model!!.VehicleTakeState)) {
            zt_id = model!!.VehicleTakeState
        }
        for (id in 0 until zt_array!!.size) {
            var model = zt_list!![id]
            zt_array!![id] = model.Name
            if (zt_id == model.ID) {
                state_tv.text = model.Name
            }
        }

        ve_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_VehicleType'")
        ve_array = arrayOfNulls(ve_list!!.size)
        var ve_id = "01"
        if (!TextUtils.isEmpty(model.VehicleType)) {
            ve_id = model.VehicleType
        }
        for (id in 0 until ve_array!!.size) {
            var model = ve_list!![id]
            ve_array!![id] = model.Name
            if (ve_id == model.ID) {
                state_tv.text = model.Name
            }
        }
    }

    /**
     * 弹出的列表选择框
     * */
    fun dialog(key: Array<String?>, method: Int) {
        //dialog参数设置
        val builder = AlertDialog.Builder(this)  //先得到构造器
        builder.setItems(key) { dialog, which ->
            when (method) {
                1 -> {//车辆类型
                    model.VehicleType = zt_list!![which].ID
                    car_type_tv.text = zt_list!![which].Name
                }
                2 -> {//取车状态
                    model.VehicleTakeState = zt_list!![which].ID
                    state_tv.text = zt_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_choice_vehicle
    }
}