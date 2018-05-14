package com.my51c.see51.app.bean;

public class DevStatusBean {

    private String code; // 0 成功 -1失败 -2 会话不存在
    private String msg;//
    private String general;// 一般状态 0 正常 -1 一般错误
    private String current; // 电流 0 正常 -1 过流
    private String hot; // 温度 0 正常 -1 过温
    private String voltage; // 电压 0 正常 -1 低电压
    private String search; // 寻找卫星 0 正常 -1 未找到
    private String orimotor; // 方位电机 0 正常 -1 故障
    private String sendzero; // 发射归零故障 0 正常 -1 故障
    private String pitchmotor; // 俯仰电机故障 0 正常 -1 故障
    private String rollzero; // 横滚归零故障 0 正常 -1 故障
    private String orizero; // 方位归零故障 0 正常 -1 故障
    private String gps; // GPS异常告警 0 正常 -1 故障
    private String rf; // 射频信号故障 0 正常 -1 故障
    private String pitchzero; // 俯仰归零故障 0 正常 -1 故障
    private String odutype; // 0 未知天线类型 1 V4 ,2 V6,3 S6,4 S6A,5 S8,6 S9,7 C6 ,8	// C9
    private String sensor; // sensor故障 0 正常 -1 故障 V、S系列天线(陀螺仪)，C系列(电子罗盘)

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getOrimotor() {
        return orimotor;
    }

    public void setOrimotor(String orimotor) {
        this.orimotor = orimotor;
    }

    public String getSendzero() {
        return sendzero;
    }

    public void setSendzero(String sendzero) {
        this.sendzero = sendzero;
    }

    public String getPitchmotor() {
        return pitchmotor;
    }

    public void setPitchmotor(String pitchmotor) {
        this.pitchmotor = pitchmotor;
    }

    public String getRollzero() {
        return rollzero;
    }

    public void setRollzero(String rollzero) {
        this.rollzero = rollzero;
    }

    public String getOrizero() {
        return orizero;
    }

    public void setOrizero(String orizero) {
        this.orizero = orizero;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getRf() {
        return rf;
    }

    public void setRf(String rf) {
        this.rf = rf;
    }

    public String getPitchzero() {
        return pitchzero;
    }

    public void setPitchzero(String pitchzero) {
        this.pitchzero = pitchzero;
    }

    public String getOdutype() {
        return odutype;
    }

    public void setOdutype(String odutype) {
        this.odutype = odutype;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }


}
