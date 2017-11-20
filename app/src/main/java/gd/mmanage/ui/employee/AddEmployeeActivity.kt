package gd.mmanage.ui.employee

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.arialyy.frame.module.AbsModule
import com.google.gson.JsonElement
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.EmployeeModule
import gd.mmanage.control.LoginModule
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.databinding.ActivityLoginBinding
import gd.mmanage.method.UtilControl
import gd.mmanage.method.Utils
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest

import kotlinx.android.synthetic.main.activity_add_employee.*
import net.tsz.afinal.FinalDb

/**
 * 添加从业人员信息
 * */
class AddEmployeeActivity : BaseActivity<ActivityAddEmployeeBinding>(), AbsModule.OnCallback {
    private var control: EmployeeModule? = null
    var is_add = true//true:添加。false：删除
    var employee: EmployeeModel? = null//传递过来的从业人员信息
    override fun onSuccess(result: Int, success: Any?) {
        //添加
        success as NormalRequest<*>
        when (success.code) {
            0 -> {
                when (result) {
                    command.employee -> toast("添加成功")
                    command.employee + 1 -> toast("修改成功")
                }
                finish()
            }
            else -> toast(success.message)
        }
    }

    override fun onError(result: Int, error: Any?) {
        toast(error as String)
    }

    var db: FinalDb? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)//创建数据库查询实例
        employee = intent.getSerializableExtra("model") as EmployeeModel
        is_add = employee != null
        if (is_add) ll1.visibility = View.GONE//添加隐藏编号
        employee!!.EmployeeCertType = "1"
        employee!!.EmployeePhone = "17093215800"
        employee!!.EmployeeState = "1"
        employee!!.EmployeeEntryDate = "2017-11-12"
        binding.model = employee//数据绑定操作
        control = getModule(EmployeeModule::class.java, this)
        title_bar.setLeftClick { finish() }
        //读卡操作
        read_card_btn.setOnClickListener {

        }
        //性别选择
        ll3.setOnClickListener {
            builder.setItems(arrayOf("男", "女")) { _, position ->
                employee!!.EmployeeSex = position.toString()
                when (position) {
                    1 -> tv3.text = "女"
                    else -> tv3.text = "男"
                }
            }
            builder.show()
        }
        //户籍选择
        ll5.setOnClickListener {

        }
        //文化程度选择
        ll6.setOnClickListener {

        }
        //入职时间选择
        ll8.setOnClickListener {

        }
        //添加从业人员
        save_btn.setOnClickListener {
            if (check_null()) {
                control!!.add_employee(UtilControl.change(binding.model))
            }
        }
    }

    /**
     * 检测必填字段
     * @return true 全部通过
     * */
    private fun check_null(): Boolean {
        return when {
            Utils.etIsNull(et2) -> {
                toast("姓名不能为空")
                false
            }
            Utils.etIsNull(et4) -> {
                toast("身份证号不能为空")
                false
            }
            Utils.etIsNull(et7) -> {
                toast("详细地址不能为空")
                false
            }
            else -> true
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_employee
    }
}
