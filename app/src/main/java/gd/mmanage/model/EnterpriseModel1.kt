package gd.mmanage.model

/**
 * Created by Administrator on 2017/11/16.
 */

class EnterpriseModel1<T> {

    /**
     * Success : true
     * Data : {"EnterpriseId":"C021306020001","EnterpriseName":"东风路汽修店","EnterpriseRegion":"130602500000","EnterpriseRegionName":"保定市新市区南奇派出所","EnterpriseUSCC":"91130000098294309F","EnterpriseVehicleType":"02","EnterpriseVehicleTypeName":"汽车修理","EnterpriseState":"01","EnterpriseStateName":"开业","EnterpriseAddress":"河北省保定市竞秀区东风西路793号","EnterprisePhone":"3068881","EnterpriseFax":"654654","EnterprisePostCode":"073200","EnterpriseOpenDate":"/Date(1510588800000)/","EnterpriseBoss":"张三","EnterpriseBossCertNumber":"130624198709183414","EnterpriseLeader":"李四","EnterpriseLeaderCertNumber":"130624198709183414","EnterpriseLeaderPhone":"13525252223","EnterpriseSecurityPerson":"李警官","EnterpriseBusinessScope":null,"EnterpriseRegisteredCapital":"100","EnterpriseLandArea":"102","EnterpriseMonitoringAddress":null,"EnterpriseCreateTime":"/Date(1510743109530)/","EnterpriseLastTime":null,"EnterpriseCreateUser":null,"EnterpriseComment":null}
     * Message : null
     */

    var Success: Boolean = false
    var Data: T? = null
    var message: String = ""
}
