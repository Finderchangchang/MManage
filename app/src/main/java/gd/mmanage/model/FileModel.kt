package gd.mmanage.model

import java.io.Serializable

/**
 * Created by Administrator on 2017/11/30.
 */

class FileModel : Serializable {
    //byte[] FileContent;
    var FileContent: String = ""
    var FileName = ""
    var FileType = ""
    var FileForeignId = ""
    var FileSuffix = ".jpg"

    constructor() {}

    constructor(fileContent: String, fileName: String, fileType: String, fileForeignId: String) {
        FileContent = fileContent
        FileName = fileName
        FileType = fileType
        FileForeignId = fileForeignId
    }
}
