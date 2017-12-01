package gd.mmanage.model;

/**
 * Created by Administrator on 2017/11/30.
 */

public class FileModel {
    //byte[] FileContent;
    Integer[] FileContent;
    String FileName;
    String FileType;
    String FileForeignId;

    public FileModel(Integer[] fileContent, String fileName, String fileType, String fileForeignId) {
        FileContent = fileContent;
        FileName = fileName;
        FileType = fileType;
        FileForeignId = fileForeignId;
    }
}
