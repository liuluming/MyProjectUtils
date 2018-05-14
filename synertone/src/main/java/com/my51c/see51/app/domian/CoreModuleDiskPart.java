package com.my51c.see51.app.domian;

public class CoreModuleDiskPart {

    private String diskPartName;
    private String totalDiskPart;
    private String leftDiskPart;
    private String filesysDiskPart;


    public String getDiskPartName() {
        return diskPartName;
    }

    public void setDiskPartName(String diskPartName) {
        this.diskPartName = diskPartName;
    }

    public String getTotalDiskPart() {
        return totalDiskPart;
    }

    public void setTotalDiskPart(String totalDiskPart) {
        this.totalDiskPart = totalDiskPart;
    }

    public String getLeftDiskPart() {
        return leftDiskPart;
    }

    public void setLeftDiskPart(String leftDiskPart) {
        this.leftDiskPart = leftDiskPart;
    }

    public String getFilesysDiskPart() {
        return filesysDiskPart;
    }

    public void setFilesysDiskPart(String filesysDiskPart) {
        this.filesysDiskPart = filesysDiskPart;
    }

    @Override
    public String toString() {
        return diskPartName + "\t" + totalDiskPart + leftDiskPart + filesysDiskPart + "\n";
    }

}
