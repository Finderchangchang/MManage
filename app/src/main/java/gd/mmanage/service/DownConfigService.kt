package gd.mmanage.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.arialyy.frame.core.ModuleFactory
import com.arialyy.frame.module.AbsModule
import com.arialyy.frame.module.IOCProxy
import com.lzy.okgo.OkGo
import gd.mmanage.callback.LzyResponse
import gd.mmanage.config.command
import gd.mmanage.config.sp
import gd.mmanage.config.url
import gd.mmanage.control.ConfigModule
import gd.mmanage.control.HttpUtils
import gd.mmanage.method.Utils
import gd.mmanage.model.CodeModel
import gd.mmanage.model.NormalRequest
import net.tsz.afinal.FinalDb
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import java.lang.Exception
import kotlin.concurrent.thread

class DownConfigService : Service() {
    var db: FinalDb? = null

    /**
     * 数据库存取操作
     * @param cmd 当前执行的命令
     * @param lists 需要存的数据信息
     * */
    fun save_down(cmd: Int, lists: List<CodeModel>) {
        var cmd = cmd
        var code_name = cmd_name(cmd)
        val thread = Thread(Runnable {
            for (model in lists) {
                model.CodeName = code_name
                db!!.save(model)
            }
            cmd = cmd + 1
            if (cmd == command.config + 11) {
                //全部下载完成
                Utils.putCache(sp.down_all, "1")
            } else {
                //继续执行下载操作
                val message = Message()
                message.what = 1
                var bu = Bundle()
                bu.putInt("code_name", cmd)
                message.data = bu
                mHandler.sendMessage(message)
            }
        })
        thread.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        db = FinalDb.create(this)
        db!!.deleteAll(CodeModel::class.java)
        down_config(command.config)
        return super.onStartCommand(intent, flags, startId)
    }

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    down_config(msg.data.getInt("code_name"))
                }
            }
            super.handleMessage(msg)
        }
    }

    fun down_config(cmd: Int) {
        var code_name = cmd_name(cmd)
        OkGo.post(url.get_code + code_name)
                .execute(object : JsonCallback<LzyResponse<List<CodeModel>>>() {
                    override fun onSuccess(t: LzyResponse<List<CodeModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.Success) {
                            save_down(cmd, t.Data!!)
                        } else {
                            //更新字典失败
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        //更新字典失败
                        var s = ""
                    }
                })
    }

    /**
     * 根据命令符转化为字典名称
     * */
    fun cmd_name(cmd: Int): String {
        return when (cmd) {
            command.config -> "Code_EmployeeState"
            command.config + 1 -> "Code_SuspiciousType"
            command.config + 2 -> "Code_RepairType"
            command.config + 3 -> "Code_RepairReasonType"
            command.config + 4 -> "Code_PartsType"
            command.config + 5 -> "Code_EquipmentType"//设备分类
            command.config + 6 -> "Code_VehicleType"//车辆类型
            command.config + 7 -> "Code_VehicleTakeState"//
            command.config + 8 -> "Code_EnterpriseState"//企业状态
            command.config + 9 -> "Code_EnterpriseVehicleType"//修车状态
            else -> "Code_Nation"
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
