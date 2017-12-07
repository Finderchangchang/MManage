package gd.mmanage.ui.notice

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.arialyy.frame.module.AbsModule
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.EmployeeModule
import gd.mmanage.databinding.ActivitySearchEmployeeBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.EmployeeModel
import kotlinx.android.synthetic.main.activity_search_employee.*
import gd.mmanage.model.NormalRequest
import java.util.ArrayList
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.Gson
import gd.mmanage.method.UtilControl
import gd.mmanage.model.PageModel
import gd.mmanage.ui.employee.AddEmployeeActivity
import gd.mmanage.ui.employee.ChoiceEmployeeActivity


/**
 * 通知通告管理
 * */
class SearchNoticeActivity : BaseActivity<ActivitySearchEmployeeBinding>(), AbsModule.OnCallback {
    var adapter: CommonAdapter<EmployeeModel>? = null//资讯
    var answer_list = ArrayList<EmployeeModel>()
    var page_index = 1//当前页码数
    var control: EmployeeModule? = null
    var dialog: LoadingDialog.Builder? = null
    var chice_model: EmployeeModel = EmployeeModel()
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        control = getModule(EmployeeModule::class.java, this)
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
                    startActivityForResult(Intent(this@SearchNoticeActivity, AddEmployeeActivity::class.java)
                            .putExtra("model", answer_list[position]), 11)
                }
            }
        }

        title_bar.setLeftClick { finish() }
        title_bar.setRightClick {
            startActivityForResult(Intent(this@SearchNoticeActivity, ChoiceEmployeeActivity::class.java)
                    .putExtra("model", chice_model), 11)
        }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {
            page_index++
            load_data()
        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->

        }
        //添加从业人员
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchNoticeActivity, AddEmployeeActivity::class.java)
                    .putExtra("model", EmployeeModel()), 11)
        }
        main_srl.setOnRefreshListener {
            page_index = 1
            load_data()
        }
        load_data()
    }

    fun load_data() {
        dialog!!.show()
        var map = UtilControl.change(chice_model)
        map.put("page_index", page_index.toString())
        control!!.get_notice(map)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {//查询
                page_index = 1
                chice_model = data!!.getSerializableExtra("model") as EmployeeModel
                load_data()
            }
            2 -> {//添加或者修改
                page_index = 1
                load_data()
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search_employee
    }

    override fun onSuccess(result: Int, success: Any?) {
        close()
        when (result) {
            command.employee + 4 -> {
                success as NormalRequest<JsonElement>
                if (page_index == 1) {
                    answer_list = ArrayList()
                }
                var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                mode.data as List<EmployeeModel>
                var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                em.map { Gson().fromJson<EmployeeModel>(it, EmployeeModel::class.java) }
                        .forEach { answer_list.add(it) }
                adapter!!.refresh(answer_list)
                main_lv.getIndex(page_index, 20, mode.ItemCount)
            }
        }
    }


    override fun onError(result: Int, error: Any?) {
        close()
    }

    fun close() {
        main_srl.isRefreshing = false
        dialog!!.dismiss()
    }

}