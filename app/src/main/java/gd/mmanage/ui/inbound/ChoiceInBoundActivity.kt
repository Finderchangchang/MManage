package gd.mmanage.ui.inbound

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityChoiceEmployeeBinding
import gd.mmanage.databinding.ActivityChoiceInBoundBinding
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.InBoundModel
import gd.mmanage.model.PartsModel
import kotlinx.android.synthetic.main.activity_choice_in_bound.*
import net.tsz.afinal.FinalDb
import net.tsz.afinal.view.DatePickerDialog

/**
 * 入库单条件查询
 * @author 杰
 * */
class ChoiceInBoundActivity : BaseActivity<ActivityChoiceInBoundBinding>() {
    var model: PartsModel = PartsModel()
    var db: FinalDb? = null
    var xm_list: List<CodeModel> = ArrayList<CodeModel>()
    var xm_array: Array<String?>? = null//配件类型集合
    var datePickerDialog: DatePickerDialog? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        model = intent.getSerializableExtra("model") as PartsModel
        db = FinalDb.create(this)
        binding.model = model
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
        start_time_ev.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.CreateTimeBegin)
            datePickerDialog!!.datePickerDialog(start_time_ev)
        }
        end_time_ll.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.CreateTimeEnd)
            datePickerDialog!!.datePickerDialog(end_time_ev)
        }
        end_time_ev.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, model.CreateTimeEnd)
            datePickerDialog!!.datePickerDialog(end_time_ev)
        }
        init_data()
        type_ll.setOnClickListener {
            val builder = AlertDialog.Builder(this)  //先得到构造器
            builder.setItems(xm_array) { dialog, which ->
                type_tv.text = xm_list[which].Name
                model!!.PartsType = xm_list[which].ID
            }
            builder.create().show()
        }
    }

    //初始化数据
    fun init_data() {
        xm_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_PartsType'")//Code_RepairReasonType
        xm_array = arrayOfNulls(xm_list!!.size)
        for (id in 0 until xm_array!!.size) {
            var m = xm_list[id]
            xm_array!![id] = m.Name
            if (m.ID == model!!.PartsType) {
                type_tv.text = m.Name
            }
        }
    }


    override fun setLayoutId(): Int {
        return R.layout.activity_choice_in_bound
    }
}