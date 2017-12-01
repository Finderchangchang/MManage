package gd.mmanage.base

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.config.command
import gd.mmanage.control.EmployeeModule
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import gd.mmanage.ui.employee.AddEmployeeActivity
import kotlinx.android.synthetic.main.activity_search_employee.*
import java.util.ArrayList

/**
 * 简单查询页面的父类
 * */
class SearchActivity : SearchBaseActivity<EmployeeModel>(), AbsModule.OnCallback, SearchBaseActivity.ILoad_Data, SearchBaseActivity.ISearch_Result {
    override fun result(json: String) {
        var em = JsonParser().parse(json).asJsonObject.getAsJsonArray("data")//解析data里面的数据
        em.map { Gson().fromJson<EmployeeModel>(it, EmployeeModel::class.java) }
                .forEach { answer_list.add(it) }
        adapter!!.refresh(answer_list)
    }

    override fun onLoad() {
        control!!.get_employees(choice)
    }

    var adapter: CommonAdapter<EmployeeModel>? = null//资讯
    var control: EmployeeModule? = null
    override fun init(savedInstanceState: Bundle?) {
        control = getModule(EmployeeModule::class.java, this)
        initData(this, this)
        super.init(savedInstanceState)
        adapter = object : CommonAdapter<EmployeeModel>(this, answer_list, R.layout.item_employee) {
            override fun convert(holder: CommonViewHolder, model: EmployeeModel, position: Int) {
                if (TextUtils.isEmpty(model.EmployeeName)) {
                    model.EmployeeName = ""
                }
                holder.setText(R.id.name_tv, model.EmployeeName)
                if (TextUtils.isEmpty(model.EmployeeCertNumber)) {
                    model.EmployeeCertNumber = "未知"
                }
                holder.setText(R.id.id_card_tv, model.EmployeeCertNumber)
                if (TextUtils.isEmpty(model.EmployeeAddress)) {
                    model.EmployeeAddress = "暂无"
                }
                holder.setText(R.id.address_tv, model.EmployeeAddress)
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {
                    startActivityForResult(Intent(this@SearchActivity, AddEmployeeActivity::class.java)
                            .putExtra("model", answer_list[position]), 11)
                }

            }
        }
        title_bar.setRightClick { }
        main_lv!!.adapter = adapter
        //item点击事件
        main_lv!!.setOnItemClickListener { parent, view, position, id ->

        }
        //添加从业人员
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchActivity, AddEmployeeActivity::class.java)
                    .putExtra("model", EmployeeModel()), 11)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {//查询
                page_index = 1
            }
            2 -> {//添加或者修改

            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search_employee
    }

}