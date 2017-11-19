package gd.mmanage.model

import net.tsz.afinal.sqlite.Id

/**
 * Created by Administrator on 2017/11/16.
 */

class CodeModel {

    constructor(ID: String, Name: String) {
        this.ID = ID
        this.Name = Name
    }

    constructor()

    /**
     * ID : 01
     * Name : 汉族
     * Status :
     * Remarks : null
     */
    @Id
    var ids = 0
    var ID: String = ""
    var Name: String = ""
    var Status: String = ""
    var Remarks: String = ""
    var CodeName: String = ""//存在数据库的表名
}
