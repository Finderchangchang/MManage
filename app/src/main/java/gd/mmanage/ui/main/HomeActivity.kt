package gd.mmanage.ui.main

import android.Manifest
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arialyy.frame.module.AbsModule
import com.google.gson.*
import gd.mmanage.R
import gd.mmanage.adapter.MainAdapter
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.config.url
import gd.mmanage.control.EmployeeModule
import gd.mmanage.control.LoginModule
import gd.mmanage.control.UserModule
import gd.mmanage.databinding.ActivityMainBinding
import gd.mmanage.method.UpdateManager
import gd.mmanage.method.Utils
import gd.mmanage.model.*
import kotlinx.android.synthetic.main.activity_main.*
import net.tsz.afinal.FinalDb
import pub.devrel.easypermissions.EasyPermissions
import android.R.attr.path
import android.os.Build
import android.view.View
import gd.mmanage.DownloadUtils
import kotlinx.android.synthetic.main.frag_main.*
import org.mozilla.universalchardet.Constants
import java.io.File


class HomeActivity : BaseActivity<ActivityMainBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        when (result) {
            command.employee + 3 -> {//存储从业人员信息
                success as NormalRequest<JsonElement>
                if (success.obj != null) {
                    var mode: PageModel<*> = Gson().fromJson<PageModel<*>>(success.obj, PageModel::class.java)
                    mode.data as List<EmployeeModel>
                    var em = JsonParser().parse(success.obj.toString()).asJsonObject.getAsJsonArray("data")//解析data里面的数据
                    db!!.deleteAll(EmployeeModel::class.java)
                    em.map { Gson().fromJson<EmployeeModel>(it, EmployeeModel::class.java) }.forEach { db!!.save(it) }
                }
            }
            command.user + 2 -> {//检测当前设备的蓝牙id
                success as NormalRequest<JsonElement>
                if (success.obj != null) {
                    var model = success.obj as JsonArray
                    for (i in 0 until model.size()) {
//                    var json: EquipmentModel = Gson().fromJson<EquipmentModel>(model[0], EquipmentModel::class.java)
//                    if (json.equipmentStatus.equals("01")) {//设置读取出的蓝牙的id
//                        Utils.putCache(sp.equipment_id, json.enterpriseId)
//                        getModule(UserModule::class.java, this).get_equipments("C021306020001")
//                    }
                        var json: EquipmentModel = Gson().fromJson<EquipmentModel>(model[0], EquipmentModel::class.java)
                        if (json.equipmentStatus.equals("01")) {//设置读取出的蓝牙的id
                            Utils.putCache(sp.equipment_id, json.enterpriseId)
                            getModule(UserModule::class.java, this).get_equipments(json.enterpriseId)
                        }
                    }
                }
            }

            command.login + 1 -> {//检查版本更新
                success as NormalRequest<JsonElement>
                if (success.code == 0) {//提示更新
                    //toast(success.obj)
                    var model = Gson().fromJson<UpdateModel>(success.obj, UpdateModel::class.java)
                    var vv = Utils.version
                    var ss = model.AndroidVersion
                    if (Utils.version != model.AndroidVersion) {
                        if (!EasyPermissions.hasPermissions(this@HomeActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            EasyPermissions.requestPermissions(this@HomeActivity, "需要下载新的apk",
                                    2, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        } else {
                            val builder = AlertDialog.Builder(this@HomeActivity)
                            builder.setTitle("提示")
                            builder.setMessage("您有新的版本，请及时更新~~")
                            builder.setNegativeButton("确定") { _, _ ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    DownloadUtils(this).downloadAPK(url.key + model.AndroidUpdateUrl, "新版本Apk.apk")
                                } else {
                                    UpdateManager(this@HomeActivity).checkUpdateInfo(url.key + model.AndroidUpdateUrl)
                                }
                            }
                            builder.show()
                        }
                    }
                }
            }
            command.employee + 5 -> {
                var s = success as NormalRequest<String>
                if (success.obj != null) {
                    var s = success.obj!!.toString().split(",")
                    if (s.size == 2) {
                        tv3.visibility = View.GONE
                        tv2.text = s[0]//待取车
                        tv2.visibility = View.GONE
                        if ("0" == s[0]) {
                            tv2.visibility = View.GONE
                        } else {
                            tv2.visibility = View.GONE
                        }
                        if ("0" == s[1]) {
                            tv3.visibility = View.GONE
                        } else {
                            tv3.visibility = View.GONE
                        }
                        tv3.text = s[1]//
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
        getModule(LoginModule::class.java, this).check_version()//检查更新
        var mAdapter = MainAdapter(supportFragmentManager)
        tab_pager.adapter = mAdapter
        //预加载页面的个数
        tab_pager.offscreenPageLimit = 2
        alphaIndicator!!.setViewPager(tab_pager)
        getModule(EmployeeModule::class.java, this).get_employees(HashMap())
    }

    override fun onResume() {
        super.onResume()
        getModule(EmployeeModule::class.java, this).get_num()
    }
}
