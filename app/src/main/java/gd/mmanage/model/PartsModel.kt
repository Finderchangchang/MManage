package gd.mmanage.model

import java.io.Serializable

/**
 * 配件Model
 * Created by Administrator on 2017/11/16.
 */

class PartsModel : Serializable {

    /// <summary>
    /// 配件编号
    /// </summary>
    var PartsId = ""

    /// <summary>
    /// 配件名称
    /// </summary>
    var PartsName = ""

    /// <summary>
    /// 配件类型
    /// </summary>
    var PartsType = ""
    /// <summary>
    /// 企业编码
    /// </summary>
    var PartsEnterpriseId = ""

    /// <summary>
    /// 配件价格
    /// </summary>
    var PartesPrice = ""
    var PartsNumber = ""

    /// <summary>
    /// 配件规格
    /// </summary>
    var PartsSpecifications = ""
    /// <summary>
    /// 配件厂家
    /// </summary>
    var PartsManufacturer = ""
    /// <summary>
    /// 配件厂家联系人
    /// </summary>
    var PartsManufacturerLeader = ""
    /// <summary>
    /// 配件厂家联系电话
    /// </summary>
    var PartsManufacturerLeaderPhone = ""

    /// <summary>
    /// 备注
    /// </summary>
    var PartsComment = ""
    /// <summary>
    /// 创建时间
    /// </summary>
    var PartsCreateTime = ""


}
