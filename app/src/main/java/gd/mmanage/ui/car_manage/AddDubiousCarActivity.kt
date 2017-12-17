package gd.mmanage.ui.car_manage

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddDubiousCarBinding
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.SuspiciousModel
import kotlinx.android.synthetic.main.activity_add_dubious_car.*
import net.tsz.afinal.FinalDb

/**
 *可疑车辆登记
 * */
class AddDubiousCarActivity : BaseActivity<ActivityAddDubiousCarBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        success as NormalRequest<*>
        when (result) {
            command.car_manage + 12 -> {
                success as NormalRequest<*>
                if (success.obj != null) {
                    var s = ""
                    var model: SuspiciousModel = Gson().fromJson(success.obj.toString(), SuspiciousModel::class.java)
                    ky_model.SuspiciousRemarks = model.SuspiciousRemarks
                    ky_model.SuspiciousId = model.SuspiciousId
                    ky_model.SuspiciousType = model.SuspiciousType
                    ky_model.SuspiciousPerson = model.SuspiciousPerson
                    ky_desc_et.setText(model.SuspiciousRemarks)
                    binding.kyType = model.SuspiciousTypeName
                    binding.portName = model.SuspiciousPerson
                }
            }
            else -> {
                save_btn.isEnabled = true
                success as NormalRequest<JsonElement>
                if (success.code == 0) {
                    finish()
                    toast("添加成功")
                } else {
                    toast(success.message)
                }
            }
        }

        dialog!!.dismiss()
    }

    override fun onError(result: Int, error: Any?) {
        save_btn.isEnabled = true
        dialog!!.dismiss()
    }

    var db: FinalDb? = null
    var ky_state_list: List<CodeModel> = ArrayList<CodeModel>()
    var ky_array: Array<String?>? = null//人员状态的集合
    var ky_model: SuspiciousModel = SuspiciousModel()
    var control: CarManageModule? = null
    var emp_array: Array<String?>? = null//从业人员的集合
    var employees: List<EmployeeModel>? = null//从业人员model
    var vehicleId = ""
    var dialog: LoadingDialog.Builder? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        vehicleId = intent.getStringExtra("vehicleId")
        dialog = LoadingDialog.Builder(this).setTitle(R.string.save_loading)//初始化dialog
        control = getModule(CarManageModule::class.java, this)
        if (!TextUtils.isEmpty(vehicleId)) {
            control!!.get_dubious_detail(vehicleId)
        }
        init_data()
        ll1.setOnClickListener {
            val builder = AlertDialog.Builder(this)  //先得到构造器
            builder.setItems(ky_array) { dialog, which ->
                binding.kyType = ky_state_list[which].Name
                ky_model.SuspiciousType = ky_state_list[which].ID
            }
            builder.create().show()
        }

        ll2.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setItems(emp_array) { dialog, which ->
                ky_model.SuspiciousPerson = employees!![which].EmployeeName
                binding.portName = employees!![which].EmployeeName
            }
            builder.create().show()
        }
        save_btn.setOnClickListener {
            ky_model.SuspiciousRemarks = ky_desc_et.text.toString().trim()
            ky_model.VehicleId = vehicleId
            ky_model.SuspiciousTime = Utils.normalTime
            save_btn.isEnabled = false
            dialog!!.show()
            control!!.save_warn(ky_model)
        }
    }

    fun init_data() {
        //初始化从业人员数据
        employees = db!!.findAll(EmployeeModel::class.java)
        emp_array = arrayOfNulls(employees!!.size)
        for (i in 0 until employees!!.size) {
            emp_array!![i] = employees!![i].EmployeeName
        }
        if (TextUtils.isEmpty(vehicleId)) {
            if (employees!!.isNotEmpty()) {
                ky_model.SuspiciousPerson = employees!![0].EmployeeName
                binding.portName = employees!![0].EmployeeName
            }
        }
        //初始化可疑状态
        ky_state_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_SuspiciousType'")
        ky_array = arrayOfNulls(ky_state_list!!.size)
        for (id in 0 until ky_array!!.size) {
            var model = ky_state_list!![id]
            ky_array!![id] = model.Name
        }
        if (TextUtils.isEmpty(vehicleId)) {
            if (ky_state_list.size > 0) {
                binding.kyType = ky_state_list[0].Name
                ky_model.SuspiciousType = ky_state_list[0].ID
            }
        }
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_dubious_car
    }
}