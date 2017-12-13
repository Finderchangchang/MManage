package gd.mmanage.base

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityMainBinding
import gd.mmanage.method.OnlyLoadListView
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import java.util.ArrayList

/**
 * 简单查询页面的父类
 * */
abstract class SearchBaseActivity<T> : BaseActivity<ActivityMainBinding>(), AbsModule.OnCallback {
    var answer_list = ArrayList<T>()
    var page_index = 1//当前页码数
    var choice = HashMap<String, String>()//查询的条件
    var dialog: LoadingDialog.Builder? = null
    var main_lv: OnlyLoadListView? = null
    var main_srl: SwipeRefreshLayout? = null//刷新控件
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        val main_lv_id = resources.getIdentifier("main_lv", "id", packageName)
        main_lv = findViewById(main_lv_id) as OnlyLoadListView
        main_srl = findViewById(resources.getIdentifier("main_srl", "id", packageName)) as SwipeRefreshLayout

        title_bar.setRightClick { }
        //解决listview和srl冲突问题
        main_lv!!.setSRL(main_srl)
        //加载数据
        main_lv!!.setInterface {
            page_index++
            load!!.onLoad()
        }
        main_srl!!.setOnRefreshListener {
            page_index = 0
            load!!.onLoad()
        }
        load!!.onLoad()
    }

    interface ILoad_Data {
        fun onLoad()
    }

    interface ISearch_Result {
        fun result(json: String)
    }

    var load: ILoad_Data? = null
    var result: ISearch_Result? = null
    fun initData(listener: ILoad_Data, res: ISearch_Result) {
        load = listener
        this.result = res
    }

    override fun onSuccess(result: Int, success: Any?) {
        close()
        when (result) {
            command.employee + 3 -> {
                success as NormalRequest<JsonElement>
                if (page_index == 1) {
                    answer_list = ArrayList()
                }
                var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                this.result!!.result(success.obj.toString())
                main_lv!!.getIndex(page_index, 20, mode.ItemCount)
            }
        }
    }

    override fun onError(result: Int, error: Any?) {
        close()
    }

    fun close() {
        main_srl!!.isRefreshing = false
        dialog!!.dismiss()
    }

}