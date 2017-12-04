package gd.mmanage.model;

/**
 * 01：身份证阅读器（蓝牙）
 * 04：App
 * Created by Administrator on 2017/12/4.
 */

public class EquipmentModel {

    /**
     * EquipmentId : 5A1532C1B8F09A2C484E0A33
     * EnterpriseId : C021306020001
     * EquipmentType : 02
     * EquipmentTypeName : null
     * EquipmentCode : FDFSDFSD54654
     * EquipmentStatus : 01
     * EquipmentStatusName : null
     * EquipmentCreateTime : 2017-11-22
     * EquipmentComment : null
     */

    private String EquipmentId;
    private String EnterpriseId;
    private String EquipmentType;
    private Object EquipmentTypeName;
    private String EquipmentCode;
    private String EquipmentStatus;
    private Object EquipmentStatusName;
    private String EquipmentCreateTime;
    private Object EquipmentComment;

    public String getEquipmentId() {
        return EquipmentId;
    }

    public void setEquipmentId(String EquipmentId) {
        this.EquipmentId = EquipmentId;
    }

    public String getEnterpriseId() {
        return EnterpriseId;
    }

    public void setEnterpriseId(String EnterpriseId) {
        this.EnterpriseId = EnterpriseId;
    }

    public String getEquipmentType() {
        return EquipmentType;
    }

    public void setEquipmentType(String EquipmentType) {
        this.EquipmentType = EquipmentType;
    }

    public Object getEquipmentTypeName() {
        return EquipmentTypeName;
    }

    public void setEquipmentTypeName(Object EquipmentTypeName) {
        this.EquipmentTypeName = EquipmentTypeName;
    }

    public String getEquipmentCode() {
        return EquipmentCode;
    }

    public void setEquipmentCode(String EquipmentCode) {
        this.EquipmentCode = EquipmentCode;
    }

    public String getEquipmentStatus() {
        return EquipmentStatus;
    }

    public void setEquipmentStatus(String EquipmentStatus) {
        this.EquipmentStatus = EquipmentStatus;
    }

    public Object getEquipmentStatusName() {
        return EquipmentStatusName;
    }

    public void setEquipmentStatusName(Object EquipmentStatusName) {
        this.EquipmentStatusName = EquipmentStatusName;
    }

    public String getEquipmentCreateTime() {
        return EquipmentCreateTime;
    }

    public void setEquipmentCreateTime(String EquipmentCreateTime) {
        this.EquipmentCreateTime = EquipmentCreateTime;
    }

    public Object getEquipmentComment() {
        return EquipmentComment;
    }

    public void setEquipmentComment(Object EquipmentComment) {
        this.EquipmentComment = EquipmentComment;
    }
}
