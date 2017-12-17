package gd.mmanage.ui.car_manage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.control.CarManageModule
import gd.mmanage.databinding.ActivityAddCarBinding
import gd.mmanage.databinding.ActivityAddServiceCarBinding
import gd.mmanage.model.*
import kotlinx.android.synthetic.main.activity_add_service_car.*
import net.tsz.afinal.FinalDb
import gd.mmanage.R.layout.dialog
import gd.mmanage.config.command
import gd.mmanage.method.Utils
import gd.mmanage.ui.parts.AddPartsServicesActivity


/**
 *车辆维修登记
 * */
class AddServiceActivity : BaseActivity<ActivityAddServiceCarBinding>(), AbsModule.OnCallback {

    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.car_manage + 7 -> {
                success as NormalRequest<JsonElement>
                if (success.code == 0) {
                    finish()
                    toast("添加成功")
                } else {
                    toast(success.message)
                }
                save_btn.isEnabled = true
                dialog!!.dismiss()
            }
            command.car_manage + 11 -> {
                success as NormalRequest<*>
                if (success.obj != null) {
                    var s = ""
                    var model: RepairModel = Gson().fromJson(success.obj.toString(), RepairModel::class.java)
                    ky_model.RepairId = model.RepairId
                    ky_model.RepairType = model.RepairType
                    ky_model.RepairReasonType = model.RepairReasonType
                    for (mo in 0 until yy_list.size) {//循环codelist
                        var kk = yy_list[mo]//当前循环的codemodel
                        //检测当前id
                        for (i in model.RepairType.split(",")) {
                            if (kk.ID == i) {
                                yy_result!![mo] = true
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(ky_model.RepairReasonType)) {
                        ky_model.RepairType += ","
                    }
                    ky_desc_et.setText(model.RepairComment)
                    binding.kyType = model.RepairReasonTypeName
                    binding.portName = model.RepairTypeName
                }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        save_btn.isEnabled = true
        dialog!!.dismiss()
    }

    var db: FinalDb? = null
    var yy_list: List<CodeModel> = ArrayList<CodeModel>()
    var yy_array: Array<String?>? = null//原因状态的集合
    var yy_result: BooleanArray? = null//原因结果
    var xm_list: List<CodeModel> = ArrayList<CodeModel>()
    var xm_array: Array<String?>? = null//人员状态的集合
    var ky_model: RepairModel = RepairModel()
    var control: CarManageModule? = null
    var vehicleId = ""
    var dialog: LoadingDialog.Builder? = null//转圈的dialog

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        dialog = LoadingDialog.Builder(this).setTitle(R.string.save_loading)//初始化dialog
        vehicleId = intent.getStringExtra("vehicleId")
        control = getModule(CarManageModule::class.java, this)
        if (!TextUtils.isEmpty(vehicleId)) {
            control!!.get_repair_detail(vehicleId)
        }
        init_data()
        ll1.setOnClickListener {
            val builder = AlertDialog.Builder(this)  //先得到构造器
            builder.setItems(xm_array) { dialog, which ->
                binding.kyType = xm_list[which].Name
                ky_model.RepairReasonType = xm_list[which].ID
            }
            builder.create().show()
        }
        ll2.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("修理项目")
            builder.setMultiChoiceItems(yy_array, yy_result,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        yy_result!![which] = isChecked
                    })
            builder.setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which -> })
            builder.setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                var result = ""
                ky_model.RepairType = ""
                for (i in 0 until yy_result!!.size) {
                    var model = yy_result!![i]
                    if (yy_result!![i]) {
                        ky_model.RepairType += yy_list[i].ID + ","
                        result += yy_list[i].Name + ","
                    }
                }
                binding.portName = result.substring(0, result.length - 1)
                ky_model.RepairType = ky_model.RepairType.substring(0, ky_model.RepairType.length - 1)
            })
            builder.create().show()
        }
        save_btn.setOnClickListener {
            if (TextUtils.isEmpty(ky_model.RepairType)) {
                toast("修理项目不能为空")
            } else {
                save_btn.isEnabled = false
                dialog!!.show()
                ky_model.VehicleId = vehicleId
                ky_model.RepairCreateTime = Utils.normalTime
                ky_model.RepairComment = ky_desc_et.text.toString().trim()
                control!!.save_repair(ky_model)
            }
        }
        //更换配件操作
        change_btn.setOnClickListener {
            startActivityForResult(Intent(this@AddServiceActivity, AddPartsServicesActivity::class.java)
                    .putExtra("vehicleId", vehicleId), 14)
        }
    }

    /**
     * 初始化字典数据
     * */
    fun init_data() {
        //初始化修理原因
        xm_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_RepairReasonType'")//Code_RepairReasonType
        xm_array = arrayOfNulls(xm_list.size)
        for (id in 0 until xm_array!!.size) {
            var model = xm_list[id]
            xm_array!![id] = model.Name
        }
        //初始化修理部位
        yy_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_RepairType'")//Code_RepairReasonType
        yy_array = arrayOfNulls(yy_list.size)
        yy_result = BooleanArray(yy_list.size)
        for (id in 0 until yy_array!!.size) {
            var model = yy_list[id]
            yy_array!![id] = model.Name
            yy_result!![id] = false
        }
//        if (yy_list.isNotEmpty()) {
//            binding.kyType = xm_list[0].Name
//            ky_model.RepairReasonType = xm_list[0].ID
//        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_service_car
    }
}