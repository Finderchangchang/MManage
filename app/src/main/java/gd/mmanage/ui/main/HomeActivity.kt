package gd.mmanage.ui.main

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gd.mmanage.R
import gd.mmanage.adapter.MainAdapter
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.control.EmployeeModule
import gd.mmanage.databinding.ActivityMainBinding
import gd.mmanage.model.EmployeeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.PageModel
import kotlinx.android.synthetic.main.activity_main.*
import net.tsz.afinal.FinalDb

class HomeActivity : BaseActivity<ActivityMainBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.employee + 3 -> {
                var s = success
                success as NormalRequest<JsonElement>
                var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                mode.data as List<EmployeeModel>
                var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                db!!.deleteAll(EmployeeModel::class.java)
                em.map { Gson().fromJson<EmployeeModel>(it, EmployeeModel::class.java) }
                        .forEach {
                            var sa = ""
                            try {
                                db!!.save(it)
                            } catch (e: Exception) {
                                sa = ""
                            }
                        }
            }
        }
    }

    override fun onError(result: Int, error: Any?) {

    }

    var db: FinalDb? = null

    companion object {
        var context: HomeActivity? = null
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)
        context = this
        var mAdapter = MainAdapter(supportFragmentManager)
        tab_pager.adapter = mAdapter
        //预加载页面的个数
        tab_pager.offscreenPageLimit = 3
        alphaIndicator!!.setViewPager(tab_pager)
        getModule(EmployeeModule::class.java, this).get_employees(HashMap())
    }
}
