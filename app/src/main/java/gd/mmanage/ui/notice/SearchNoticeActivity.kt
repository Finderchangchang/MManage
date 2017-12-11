package gd.mmanage.ui.notice

import android.content.Intent
import android.os.Bundle
import android.text.Html
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
import gd.mmanage.model.NoticeModel
import gd.mmanage.model.NormalRequest
import java.util.ArrayList
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.Gson
import gd.mmanage.method.UtilControl
import gd.mmanage.model.PageModel
import gd.mmanage.ui.employee.AddEmployeeActivity
import gd.mmanage.ui.employee.ChoiceEmployeeActivity
import kotlinx.android.synthetic.main.activity_search_notice.*


/**
 * 通知通告管理
 * */
class SearchNoticeActivity : BaseActivity<ActivitySearchEmployeeBinding>(), AbsModule.OnCallback {
    var adapter: CommonAdapter<NoticeModel>? = null//资讯
    var answer_list = ArrayList<NoticeModel>()
    var page_index = 1//当前页码数
    var control: EmployeeModule? = null
    var dialog: LoadingDialog.Builder? = null
    var chice_model: NoticeModel = NoticeModel()
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        control = getModule(EmployeeModule::class.java, this)
        adapter = object : CommonAdapter<NoticeModel>(this, answer_list, R.layout.item_notice) {
            override fun convert(holder: CommonViewHolder, model: NoticeModel, position: Int) {
                holder.setText(R.id.title_tv, model.NewsTitle)
                val sb = StringBuilder(model.NewsContent)
                holder.setText(R.id.content_tv, Html.fromHtml(sb.toString()).toString())
                holder.setText(R.id.time_tv, model.CreateTime)
            }
        }
        main_lv.setOnItemClickListener { parent, view, position, id ->
            startActivity(Intent(this, NoticeDetailActivity::class.java)
                    .putExtra("model", answer_list[position]))
        }
        main_lv.adapter = adapter
        //解决listview和srl冲突问题
        main_lv.setSRL(main_srl)
        //加载数据
        main_lv.setInterface {
            page_index++
            load_data()
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
            2 -> {//添加或者修改
                page_index = 1
                load_data()
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search_notice
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
                mode.data as List<NoticeModel>
                var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                em.map { Gson().fromJson<NoticeModel>(it, NoticeModel::class.java) }
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