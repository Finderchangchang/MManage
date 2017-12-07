package gd.mmanage.model

import java.io.Serializable

/**
 * Created by Administrator on 2017/11/30.
 */

class FileModel : Serializable {
    //byte[] FileContent;
    var FileContent: Array<Int?>? = null
    var FileName = ""
    var FileType = ""
    var FileForeignId = ""

    constructor() {}

    constructor(fileContent: Array<Int?>, fileName: String, fileType: String, fileForeignId: String) {
        FileContent = fileContent
        FileName = fileName
        FileType = fileType
        FileForeignId = fileForeignId
    }
}
