package gd.mmanage.ui.employee

import android.content.Intent
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivitySearchEmployeeBinding
import gd.mmanage.method.CommonAdapter
import gd.mmanage.method.CommonViewHolder
import gd.mmanage.model.EmployeeModel
import kotlinx.android.synthetic.main.activity_search_employee.*
import gd.mmanage.method.OnlyLoadListView
import java.util.ArrayList

/**
 * 从业人员信息管理
 * */
class SearchEmployeeActivity : BaseActivity<ActivitySearchEmployeeBinding>(), AbsModule.OnCallback {
    var adapter: CommonAdapter<EmployeeModel>? = null//资讯
    var answer_list = ArrayList<EmployeeModel>()
    var page_index = 1//当前页码数
    var choice = HashMap<String, String>()//查询的条件
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        adapter = object : CommonAdapter<EmployeeModel>(this, answer_list, R.layout.item_employee) {
            override fun convert(holder: CommonViewHolder, model: EmployeeModel, position: Int) {
                holder.setText(R.id.name_tv, model.EmployeeName)
                holder.setText(R.id.id_card_tv, model.EmployeeCertNumber)
                holder.setText(R.id.address_tv, model.EmployeeAddress)
                //修改操作
                holder.setOnClickListener(R.id.update_ll) {

                }
                //删除操作
                holder.setOnClickListener(R.id.del_ll) {

                }
            }
        }
        title_bar.setLeftClick { finish() }
        title_bar.setRightClick { }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {

        }
        //item点击事件
        main_lv.setOnItemClickListener { parent, view, position, id ->

        }
        //添加从业人员
        add_btn.setOnClickListener {
            startActivityForResult(Intent(this@SearchEmployeeActivity, AddEmployeeActivity::class.java)
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

    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }
}