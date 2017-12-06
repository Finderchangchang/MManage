package gd.mmanage.model

import android.text.TextUtils
import net.tsz.afinal.sqlite.Id
import java.io.Serializable

/**
 * Created by Administrator on 2017/11/5.
 */

class EmployeeModel : Serializable {
    @Id
    var ids = 0
    /// <summary>
    /// 人员编码
    /// </summary>
    var EmployeeId = ""

    /// <summary>
    /// 人员姓名
    /// </summary>
    var EmployeeName = ""

    /// <summary>
    /// 人员性别
    /// </summary>
    var EmployeeSex = ""

    /// <summary>
    /// 人员民族
    /// </summary>
    var EmployeeNation = ""


    /// <summary>
    /// 人员证件类型
    /// </summary>
    var EmployeeCertType = "01"
    //企业编码
    var EnterpriseId = ""


    /// <summary>
    /// 人员证件号码
    /// </summary>
    var EmployeeCertNumber = ""

    /// <summary>
    /// 人员地址
    /// </summary>
    var EmployeeAddress = ""


    /// <summary>
    /// 联系手机
    /// </summary>
    var EmployeePhone = ""

    /// <summary>
    /// 人员状态
    /// </summary>
    var EmployeeState = ""


    /// <summary>
    /// 入职日期
    /// </summary>
    var EmployeeEntryDate = ""


    /// <summary>
    /// 离职日期
    /// </summary>
    var EmployeeLeaveDate = ""


    /// <summary>
    /// 创建时间
    /// </summary>
    var EmployeeCreateTime = ""

    /// <summary>
    /// 最后修改时间
    /// </summary>
    var EmployeeLastTime = ""
    var EntryDateBegin = ""//入职起始时间
    var EntryDateEnd = ""//入职结束时间

    fun setSex(): String {
        return if (!TextUtils.isEmpty(EmployeeSex)) {
            when (EmployeeSex) {
                "2" -> "女"
                else -> "男"
            }
        } else {
            "男"
        }
    }
}
