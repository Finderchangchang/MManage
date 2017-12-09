package gd.mmanage.ui.inbound

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.sp
import gd.mmanage.databinding.ActivityAddInBoundBinding
import gd.mmanage.method.Utils
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PartsModel
import gd.mmanage.model.StorageModel
import gd.mmanage.ui.parts.SearchPartsActivity
import kotlinx.android.synthetic.main.activity_add_in_bound.*
import net.tsz.afinal.FinalDb
import net.tsz.afinal.view.DatePickerDialog

class AddInBoundActivity : BaseActivity<ActivityAddInBoundBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        success as NormalRequest<JsonElement>
        if (success.code == 0) {
            setResult(12)
            finish()
            toast("添加成功")
        } else {
            toast(success.message)
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    var model: StorageModel = StorageModel()
    var emp_array: Array<String?>? = null//从业人员的集合
    var employees: List<EmployeeModel>? = null//从业人员model
    var db: FinalDb? = null
    var control: InBoundsModule? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        control = getModule(InBoundsModule::class.java, this)
        employees = db!!.findAll(EmployeeModel::class.java)
        emp_array = arrayOfNulls(employees!!.size)
        for (i in 0 until employees!!.size) {
            emp_array!![i] = employees!![i].EmployeeName
        }
        if (employees!!.isNotEmpty()) {
            model.StoragePerson = employees!![0].EmployeeName
            tv4.text = employees!![0].EmployeeName
        }
        ll3.setOnClickListener {
            startActivityForResult(Intent(this, SearchPartsActivity::class.java)
                    .putExtra("only_selected", false), 11)
        }
        ll4.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setItems(emp_array) { dialog, which ->
                model.StoragePerson = employees!![which].EmployeeName
                tv4.text = employees!![which].EmployeeName
            }
            builder.create().show()
        }
        save_btn.setOnClickListener {
            if (model.StoragePartsId.isEmpty()) {
                toast("请选择需要入库的配件信息")
            } else {
                model.StorageNumber = et6.text.toString().trim()
                model.StorageUser = Utils.getCache(sp.user_id)
                model.StorageTime = Utils.normalTime
                control!!.add_prat(model)
            }
        }
        et6.setSelection(1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 13) {
            var m = data!!.getSerializableExtra("model") as PartsModel
            model.StoragePartsId = m.PartsId
            tv3.text = m.PartsName
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_in_bound
    }
}
