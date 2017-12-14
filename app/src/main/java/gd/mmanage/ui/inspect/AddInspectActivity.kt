package gd.mmanage.ui.inspect

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import com.jiangyy.easydialog.LoadingDialog
import com.zkteco.android.IDReader.IDPhotoHelper
import com.zkteco.android.IDReader.WLTService
import com.zkteco.id3xx.IDCardReader
import com.zkteco.id3xx.meta.IDCardInfo
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.control.CarManageModule
import gd.mmanage.control.EmployeeModule
import gd.mmanage.databinding.ActivityAddEmployeeBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.UtilControl
import gd.mmanage.method.Utils
import gd.mmanage.method.uu
import gd.mmanage.method.uu.compressImage
import gd.mmanage.model.*
import gd.mmanage.ui.CameraPersonActivity
import gd.mmanage.ui.DemoActivity

import kotlinx.android.synthetic.main.activity_add_employee.*
import net.tsz.afinal.FinalDb
import net.tsz.afinal.view.DatePickerDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 添加民警检查信息
 * */
class AddInspectActivity : BaseActivity<ActivityAddEmployeeBinding>(), AbsModule.OnCallback {
    private var control: EmployeeModule? = null
    var is_add = true//true:添加。false：删除
    var employee: PoliceInspectModel? = null//传递过来的从业人员信息
    var dialog: LoadingDialog.Builder? = null

    override fun onSuccess(result: Int, success: Any?) {
        //添加
        success as NormalRequest<*>
        when (success.code) {
            0 -> {
                when (result) {
                    command.employee -> toast("添加成功")
                    command.employee + 1 -> toast("修改成功")
                }
                setResult(2)
                finish()
            }
            else -> toast(success.message)
        }
        dialog!!.dismiss()
    }

    override fun onError(result: Int, error: Any?) {
        toast(error as String)
    }

    var is_cc = 0
    var db: FinalDb? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        db = FinalDb.create(this)//创建数据库查询实例
        employee = intent.getSerializableExtra("model") as PoliceInspectModel
        is_add = employee != null
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        is_cc = intent.getIntExtra("is_cc", 0)
        employee!!.EnterpriseId = Utils.getCache(sp.company_id)
//        binding.model = employee//数据绑定操作
        control = getModule(EmployeeModule::class.java, this)

        read_ocr_btn.setOnClickListener {
            startActivityForResult(Intent(this@AddInspectActivity, CameraPersonActivity::class.java)
                    .putExtra("position", "2"), 1)
        }
        when (intent.getIntExtra("is_cc", 0)) {
            1 -> {
                save_btn.visibility = View.GONE
                read_card_btn.visibility = View.GONE
                title_bar.center_Tv.text = "从业人员信息"
//                control!!.get_employee(employee!!.EmployeeId)
            }
            0 -> {
                //ll1.visibility = View.GONE//添加隐藏编号
                title_bar.center_Tv.text = "从业人员添加"
            }
            2 -> {
                title_bar.center_Tv.text = "从业人员修改"
//                control!!.get_employee(employee!!.EmployeeId)
            }
        }
        //性别选择
        ll3.setOnClickListener { if (is_cc != 1) dialog(arrayOf("男", "女"), 1) }
        //人员状态
        ll8.setOnClickListener { if (is_cc != 1) dialog(zt_array!!, 2) }
        //添加从业人员
        save_btn.setOnClickListener {
            if (check_null()) {
                dialog!!.show()
                var map = HashMap<String, String>()
                var model = binding.model
                model.EmployeeEntryDate = Utils.normalTime
                map = UtilControl.change(model)
                control!!.add_employee(map)
            }
        }
        ll4.setOnClickListener { if (is_cc != 1) dialog(mz_array!!, 3) }
        initNation()
    }

    var mz_array: Array<String?>? = null//民族的集合
    var zt_array: Array<String?>? = null//人员状态的集合

    /**
     * 加载民族的字典
     * */
    fun initNation() {
//        var nation_id = "01"
//        if (!TextUtils.isEmpty(employee!!.EmployeeNation)) {
//            nation_id = employee!!.EmployeeNation
//        }
//        var zt_id = "01"
//        if (!TextUtils.isEmpty(employee!!.EmployeeState)) {
//            zt_id = employee!!.EmployeeState
//        }
//        nation_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_Nation'")
//        mz_array = arrayOfNulls(nation_list!!.size)
//        for (id in 0 until nation_list!!.size) {
//            var model = nation_list!![id]
//            mz_array!![id] = model.Name
//            if (nation_id == model.ID) {
//                binding.nation = model.Name
//            }
//        }
//        zt_list = db!!.findAllByWhere(CodeModel::class.java, " CodeName='Code_EmployeeState'")
//        zt_array = arrayOfNulls(zt_list!!.size)
//        for (id in 0 until zt_array!!.size) {
//            var model = zt_list!![id]
//            zt_array!![id] = model.Name
//            if (zt_id == model.ID) {
//                binding.state = model.Name
//                binding.model.EmployeeState = model.ID
//            }
//        }

    }

    var nation_list: List<CodeModel>? = null
    var zt_list: List<CodeModel>? = null

    /**
     * 弹出的列表选择框
     * */
    fun dialog(key: Array<String?>, method: Int) {
        //dialog参数设置
        val builder = AlertDialog.Builder(this)  //先得到构造器
        builder.setItems(key) { dialog, which ->
            when (method) {
                1 -> {//性别
//                    employee!!.EmployeeSex = which.toString()
                    when (which) {
                        1 -> tv3.text = "女"
                        else -> tv3.text = "男"
                    }
                }
                2 -> {//人员状态
//                    employee!!.EmployeeState = zt_list!![which].ID
                    tv8.text = zt_list!![which].Name
                }
                3 -> {//民族
//                    employee!!.EmployeeNation = nation_list!![which].ID
                    binding.nation = nation_list!![which].Name
                }
            }
            dialog.dismiss()
        }
        builder.create().show()
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
            Utils.etIsNull(et5) -> {
                toast("身份证号不能为空")
                false
            }
            Utils.etIsNull(et6) -> {
                toast("详细地址不能为空")
                false
            }
            Utils.etIsNull(et7) -> {
                toast("联系电话不能为空")
                false
            }
            Utils.isChinaPhoneLegal(et7.text.toString().trim()) -> {
                toast("请输入正确的手机号")
                false
            }
            else -> true
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_inspect
    }
}
