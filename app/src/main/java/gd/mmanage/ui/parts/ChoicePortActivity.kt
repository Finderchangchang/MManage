package gd.mmanage.ui.parts

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityChoiceEmployeeBinding
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EmployeeModel
import kotlinx.android.synthetic.main.activity_choice_employee.*
import net.tsz.afinal.FinalDb
import net.tsz.afinal.view.DatePickerDialog

/**
 * 配件条件查询
 * @author 杰
 * */
class ChoicePortActivity : BaseActivity<ActivityChoiceEmployeeBinding>() {
    var model: EmployeeModel = EmployeeModel()
    var db: FinalDb? = null
    var zt_list: List<CodeModel>? = null
    var zt_array: Array<String?>? = null//人员状态的集合
    var datePickerDialog: DatePickerDialog? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        model = intent.getSerializableExtra("model") as EmployeeModel
        db = FinalDb.create(this)
        binding.model = model
        sex_ll.setOnClickListener { dialog(arrayOf("男", "女", "全部"), 1) }
        state_ll.setOnClickListener { dialog(zt_array!!, 2) }
        //查询接口
        search_btn.setOnClickListener {
            var model = binding.model
            setResult(1, Intent().putExtra("model", model))
            finish()
        }
        start_time_ll.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.EntryDateBegin)
            datePickerDialog!!.datePickerDialog(start_time_ev)
        }
        end_time_ll.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.EntryDateEnd)
            datePickerDialog!!.datePickerDialog(end_time_ev)
        }
        init_data()
    }

    //初始化数据
    fun init_data() {
        zt_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_EmployeeState'")
        zt_array = arrayOfNulls(zt_list!!.size)
        var zt_id = "01"
        if (!TextUtils.isEmpty(model!!.EmployeeState)) {
            zt_id = model!!.EmployeeState
        }
        for (id in 0 until zt_array!!.size) {
            var model = zt_list!![id]
            zt_array!![id] = model.Name
            if (zt_id == model.ID) {
                binding.state = model.Name
            }
        }
        if (!TextUtils.isEmpty(model.EmployeeSex)) {
            when (model.EmployeeSex) {
                "0" -> sex_tv.text = "男"
                "1" -> sex_tv.text = "女"
                else -> sex_tv.text = "全部"
            }
        } else {
            sex_tv.text = "全部"
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
                1 -> {//性别
                    model.EmployeeSex = which.toString()
                    when (which) {
                        1 -> sex_tv.text = "女"
                        2 -> {
                            sex_tv.text = "全部"
                            model.EmployeeSex = ""
                        }
                        else -> sex_tv.text = "男"
                    }
                }
                2 -> {//人员状态
                    model!!.EmployeeState = zt_list!![which].ID
                    state_tv.text = zt_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_choice_employee
    }
}